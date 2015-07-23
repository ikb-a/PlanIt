package edu.toronto.cs.Planit.scraping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import edu.toronto.cs.Planit.dataObjects.DataObjectUtils;
import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.Planit.scraping.Throttler;

/**
 * Scrapes http://www.conferenceboard.ca/conf/default.aspx for events and speakers.
 * @author wginsberg
 *
 */
public class ScrapeConferenceBoardCanada {

	static String conferenceBoardAddress = "http://www.conferenceboard.ca";
	static String eventListingsAddress = conferenceBoardAddress + "/conf/default.aspx";

	/**
	 * The web client is used when a page has javascript content we want to scrape
	 */
	static WebClient webClient;
	static Throttler throttler;
	
	public static void main(String [] args){
		
		main("src/main/resources/scrape/event and speakers/3.json");
		
	}
	
	/**
	 * Scrapes all events and all speakers (where links are available), discovered from the main page.
	 * Saves the results in json format to the supplied file location
	 * @param args
	 */
	public static void main(String savePath){
		
		throttler = new Throttler(1, TimeUnit.SECONDS);
		
		Connection connection;
		Document document;
		
		//connect and get the html document
		connection = Jsoup.connect(eventListingsAddress);
		try {
			document = connection.get();
		} catch (IOException e) {
			System.err.println("Could not connect to " + conferenceBoardAddress);
			return;
		}
		//get links to all events with a page
		List<String> eventLinks = getEventLinks(document);

		//visit each event page to scrape details
		List<Event> scrapedData = new ArrayList<Event>();
		for (String link : eventLinks){
			
			//open the event page
			try {
				if (throttler !=null) throttler.next();
				connection.url(link);
				document = connection.get();
			} catch (IOException e) {
				System.err.println("Could not connect to event page " + link);
				continue;
			}
			
			//get the event
			Event event = getEvent(document);
			
			//open the speakers page
			String speakerPageAddresss = getEventAgendaAddress(link);
			try{
				connection.url(speakerPageAddresss);
				document = connection.get();
			} catch (IOException e) {
				System.err.println("Could not connect to speaker page " + speakerPageAddresss);
				continue;
			}
			
			//get the speakers
			List<Speaker> speakers = null;
			try{
				speakers = getSpeakers(document);
			} catch (Exception e){
				System.err.println("Could not scrape speakers from " + speakerPageAddresss);
			}
			
			if (event != null && speakers != null){
				event.getConfirmedSpeakers().addAll(speakers);
				scrapedData.add(event);
			}
		}
		
		if (!DataObjectUtils.writeObjectJson(scrapedData, savePath)){
			System.out.println("Saved results to " + savePath);
		}
		else {
			System.err.println("Could not save results to file. Printing to stdout instead");
			System.out.println(DataObjectUtils.asJson(scrapedData));
		}
	}
	
	public static Document getDocument(String pageAddress) throws IOException{
		return Jsoup.connect(pageAddress).get();
	}
	
	/**
	 * Given the html document of the event listings page, returns a list of all links to events from that page
	 * @param eventListings
	 * @return
	 */
	public static List<String> getEventLinks(Document eventListings){
		
		List<String> links = new LinkedList<String>();
		
		String query = ".conftable [href]";
		Elements eventElements = Selector.select(query, eventListings.getAllElements());

		for (Element eventElement : eventElements){
			String partialLink = eventElement.attr("href");
			links.add(conferenceBoardAddress + partialLink);
		}
		
		return links;
	}

	/**
	 * Given the address of the event page, returns the address of the event's agenda page 
	 * @param eventLink
	 * @return
	 */
	public static String getEventAgendaAddress(String eventPageAdress){
		String transformed = eventPageAdress.replace("default.aspx", "agenda.aspx");
		return transformed;
	}
		
	public static Event getEvent(Document eventPage){
		String title = getEventTitle(eventPage);
		String description = getEventDescription(eventPage);
		if (title != null && description != null && title.length() > 0 && description.length() > 0){
			Event event = Event.createEvent(title).setDescription(description);
			return event;
		}
		return null;
	}
	
	/**
	 * Given an event's page, returns the description of the event
	 * @param eventPageAddress
	 * @return
	 */
	public static String getEventDescription(Document eventPage){
		String query = "#body.container  p:not([class]), li:not([class])";
		Elements descriptionElements = Selector.select(query, eventPage.getAllElements());
		return descriptionElements == null ? "" : descriptionElements.text();
	}
	
	/**
	 * Given an event's page, returns the title of the event
	 * @param eventPageAddress
	 * @return
	 */
	public static String getEventTitle(Document eventPage){
		String query = "#body.container h1";
		Element titleElement = Selector.select(query, eventPage.getAllElements()).first();
		return titleElement == null ? "" : titleElement.text();
	}
	
	/**
	 * Given the document of the "agenda and speakers" page, returns a list of speakers
	 * @param document
	 * @return
	 */
	public static List<Speaker> getSpeakers(Document document) throws FailingHttpStatusCodeException, IOException{

		//open up the page for "agenda"
		String pageAddress = document.location();
		HtmlPage page = getWebClient().getPage(pageAddress);
		
		//there may need to be a button press to get the speakers
		try{
			HtmlElement buttonElement = page.getHtmlElementById("ctl00_MainRegion_usercontrols_cboc___conference_speakersagenda_ascx1_btnSpeakersOnly");
			page = buttonElement.click();
		}
		catch (ElementNotFoundException | IOException e){
			//if the button could not be pressed then the speakers are likely already on the page
		}
		
		//get the content of the page
		String pageText= page.getPage().asText();
		if (pageText == null || pageText.isEmpty()){
			return new ArrayList<Speaker>(0);
		}
		
		//try assuming the first page format
		List<Speaker> speakers;
		speakers = getSpeakersPageFormat1(page);
		if (speakers != null && speakers.size() > 0){
			return speakers;
		}
		
		//try assuming the second page format
		speakers = getSpeakersPageFormat2(page);
		if (speakers != null && speakers.size() > 0){
			return speakers;
		}
		
		return null;
	}
	
	/**
	 * Extracts names from the text of the "event agenda page"
	 * The first page format that conferenceboard.ca uses will be assumed
	 * @return The list of speakers, or null if none could be accessed
	 */
	private static List<Speaker> getSpeakersPageFormat1(HtmlPage page){
		
		String pageText = page.asText();
		
		//get the section of the page containing the speaker descriptions
		String listStartText = "List of speakers:";
		String listEndText = "The details of this event are subject to change.";
		int listStartIndex = pageText.indexOf(listStartText) + listStartText.length();
		int listEndIndex = pageText.indexOf(listEndText);
		if (listStartIndex > listStartText.length()){
			if (listEndIndex > listStartIndex){

				List<Speaker> speakers = new ArrayList<Speaker>(0);

				String [] speakerSummaries = pageText.substring(listStartIndex, listEndIndex).split("\\n\\s");

				for (int i = 0; i < speakerSummaries.length; i++){

					String [] speakerSummaryLines = speakerSummaries[i].split("\\n");

					/*
					 * Assuming the format of:
					 * <blank line>
					 * <name>
					 * <title>
					 * <organization>
					 * <bio>
					 * ...
					 * </bio>
					 */

					//We'll mandate a name and title at least
					if (speakerSummaryLines == null || speakerSummaryLines.length < 4){
						continue;
					}
					
					//get the name, title
					String name;
					if (speakerSummaryLines[1].length() > 0){
						name = speakerSummaryLines[1];
					}
					else{
						name = null;
					}
					
					String title;
					if (speakerSummaryLines[2].length() > 0 && speakerSummaryLines[3].length() > 0){
						title = speakerSummaryLines[2] + ", " + speakerSummaryLines[3];
					}
					else{
						title = "";
					}
					
					//get the bio
					String bio = null;
					if (speakerSummaryLines.length > 4){
						StringBuilder sb = new StringBuilder();
						for (int q = 4; q < speakerSummaryLines.length; q++){
							if (speakerSummaryLines[q] != null && speakerSummaryLines[q].length() > 0){
								sb.append(speakerSummaryLines[q]);
							}
						}
						if (sb.length() > 0){
							bio = sb.toString();
						}
					}
					else{
						bio = "";
					}
					
					speakers.add(Speaker.createSpeaker(name).setProfessionalTitle(title).setBio(bio).addPage(page.getUrl()));
				}
				return speakers;
			}
		}
		return null;
	}
	
	/**
	 * Extracts names from the "event agenda page"
	 * The second page format that conferenceboard.ca uses will be assumed
	 * @param pageText
	 * @return The list of speakers, or null if none could be accessed
	 */
	private static List<Speaker> getSpeakersPageFormat2(HtmlPage page){
		
		List<Speaker> speakers = null;
		
		//get the table of speakers
		List<DomElement> elements = page.getElementsByIdAndOrName("ctl00_MainRegion_ctl01_EventSpeakersDataList");
		if (elements == null || elements.size() < 1){
			return null;
		}
		List<HtmlElement> speakerElements = elements.get(0).getElementsByTagName("tr");
		if (speakerElements == null || speakerElements.size() < 1){
			return null;
		}
		
		//get the details of each speaker
		for (HtmlElement element : speakerElements){
			
			List<HtmlElement> nameElements =  element.getElementsByTagName("h3");
			if (nameElements == null || nameElements.size() < 1){
				continue;
			}
			
			String name = nameElements.get(0).getTextContent();
			
			//info is title and organization
			List<HtmlElement> infoElements =  element.getElementsByTagName("p");
			if (infoElements == null || infoElements.size() < 1){
				continue;
			}
			
			List<HtmlElement> titleAndOrg = infoElements.get(0).getElementsByTagName("span");
			if (titleAndOrg == null || titleAndOrg.size() != 2){
				continue;
			}
			
			String professionalTitle = titleAndOrg.get(0).getTextContent() + ", " + titleAndOrg.get(1).getTextContent();
			
			//instantiate a new speaker with the details and add it to the list
			Speaker speaker = Speaker.createSpeaker(name).setProfessionalTitle(professionalTitle).addPage(page.getUrl());
			if (speakers == null){speakers = new ArrayList<Speaker>();}			
			speakers.add(speaker);
		}
		
		return speakers;
	}
	
	/**
	 * Returns a generic web client.
	 * 
	 * This method has the side effect that logging from com.gargoylesoftware.htmlunit and org.apache.http is turned off
	 * @return
	 */
	static WebClient getWebClient(){
		if (webClient == null){
			webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		}

		return webClient;
	}
}
