package edu.toronto.cs.PlanIt.scraping;

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
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import edu.toronto.cs.Planit.dataObjects.DataObjectUtils;
import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.PlanIt.scraping.Throttler;

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
	
	/**
	 * Scrapes all events and all speakers (where links are available), discovered from the main page.
	 * @param args
	 */
	public static void main(String [] args){

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		
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
		List<EventAndSpeakers> scrapedData = new ArrayList<EventAndSpeakers>();
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
				scrapedData.add(new EventAndSpeakers(event, speakers));
			}
		}
		
		if (!DataObjectUtils.writeObjectJson(scrapedData, "src/main/resources/scrape/event and speakers/1.json")){
			System.err.println("Could not save results to file. Printing to stdout instead");
			System.out.println(DataObjectUtils.asJson(scrapedData));
		}
	}
	
	/**
	 * Given the html document of the event listings page, returns a list of all links to events from that page
	 * @param eventListings
	 * @return
	 */
	static List<String> getEventLinks(Document eventListings){
		
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
	static String getEventAgendaAddress(String eventPageAdress){
		String transformed = eventPageAdress.replace("default.aspx", "agenda.aspx");
		return transformed;
	}
		
	static Event getEvent(Document eventPage){
		String title = getEventTitle(eventPage);
		String description = getEventDescription(eventPage);
		if (title != null && description != null && title.length() > 0 && description.length() > 0){
			Event event = new Event(title, description);
			return event;
		}
		return null;
	}
	
	/**
	 * Given an event's page, returns the description of the event
	 * @param eventPageAddress
	 * @return
	 */
	static String getEventDescription(Document eventPage){
		String query = "#body.container  p:not([class]), li:not([class])";
		Elements descriptionElements = Selector.select(query, eventPage.getAllElements());
		return descriptionElements == null ? "" : descriptionElements.text();
	}
	
	/**
	 * Given an event's page, returns the title of the event
	 * @param eventPageAddress
	 * @return
	 */
	static String getEventTitle(Document eventPage){
		String query = "#body.container h1";
		Element titleElement = Selector.select(query, eventPage.getAllElements()).first();
		return titleElement == null ? "" : titleElement.text();
	}
	
	/**
	 * Given the document of the "agenda and speakers" page, returns a list of speakers
	 * @param document
	 * @return
	 */
	static List<Speaker> getSpeakers(Document document) throws FailingHttpStatusCodeException, IOException{

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
		
		//get the section of the page containing the speaker descriptions
		String listStartText = "List of speakers:";
		String listEndText = "The details of this event are subject to change.";
		int listStartIndex = pageText.indexOf(listStartText) + listStartText.length();
		int listEndIndex = pageText.indexOf(listEndText);
		if (listStartIndex >= listStartText.length()){
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
					
					//create a speaker and extract their details
					Speaker speaker = new Speaker();
					speaker.addPage(page.getUrl());
					
					//get the name, title
					if (speakerSummaryLines[1].length() > 0){
						speaker.setName(speakerSummaryLines[1]);
					}
					if (speakerSummaryLines[2].length() > 0 && speakerSummaryLines[3].length() > 0){
						speaker.setProfessionalTitle(speakerSummaryLines[2] + ", " + speakerSummaryLines[3]);
					}
					
					//get the bio
					if (speakerSummaryLines.length > 4){
						StringBuilder sb = new StringBuilder();
						for (int q = 4; q < speakerSummaryLines.length; q++){
							if (speakerSummaryLines[q] != null && speakerSummaryLines[q].length() > 0){
								sb.append(speakerSummaryLines[q]);
							}
						}
						if (sb.length() > 0){
							speaker.setBio(sb.toString());
						}
					}
					speakers.add(speaker);
				}
				return speakers;
			}
		}
		return null;
	}
	
	/**
	 * Returns a generic web client.
	 * @return
	 */
	static WebClient getWebClient(){
		if (webClient == null){
			webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
		}
		return webClient;
	}
	
	/**
	 * Private class for holding one event and a list of speakers for the event.
	 * @author wginsberg
	 *
	 */
	static class EventAndSpeakers{
		private Event event;
		private List<Speaker> speakers;
		public EventAndSpeakers(){super();}
		public EventAndSpeakers(Event event, List<Speaker> speakers){
			this.event = event;
			this.speakers = speakers;
		}
		public Event getEvent() {
			return event;
		}
		public List<Speaker> getSpeakers() {
			return speakers;
		}
		public void setEvent(Event event) {
			this.event = event;
		}
		public void setSpeakers(List<Speaker> speakers) {
			this.speakers = speakers;
		}
		
		public String toString(){
			
			StringBuilder sb = new StringBuilder();
			sb.append("event: ");
			try{
				sb.append(getEvent().getTitle());
				sb.append(String.format(", %s ...", getEvent().getTruncatedDescription(75)));
			}
			catch (NullPointerException e){
				sb.append("null");
			}
			sb.append("\n");
			sb.append("speakers: ");
			try{
				sb.append(getSpeakers().toString());
			}
			catch (NullPointerException e){
				sb.append("null");
			}
			sb.append("\n");
			return sb.toString();
		}
	}
	
}
