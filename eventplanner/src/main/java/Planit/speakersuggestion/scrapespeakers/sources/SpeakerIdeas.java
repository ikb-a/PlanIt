package Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Optional;

import Planit.dataObjects.Speaker;
import Planit.scraping.Throttler;
import Planit.speakersuggestion.scrapespeakers.util.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Scrapes speakers from http://www.speakerideas.com/ by searching for keywords.
 * 
 * Be careful with using this website. They might IP ban you!
 * 
 * @author wginsberg
 *
 */
public class SpeakerIdeas extends Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements
		GetSpeakersContract {

	private static Throttler throttler;
	
	public SpeakerIdeas() {
		
		if (throttler == null){
			throttler = new Throttler(30, TimeUnit.MINUTES);
		}
	}
	
	@Override
	public Opinion<Collection<Speaker>, SpeakerSetTrust> getOpinion(
			SpeakersQuery input) throws UnknownException {
		
		if (throttler == null){
			throttler = new Throttler(30, TimeUnit.MINUTES);
		}
		
		Collection<Speaker> speakers;
		try{
			speakers = getSpeakers(input.getKeywords(), input.getMinSpeakers());
			if (speakers == null || speakers.size() < 1){
				throw new UnknownException();
			}
		}
		catch (Exception e){
			throw new UnknownException(e);
		}
		
		return new Opinion<Collection<Speaker>, SpeakerSetTrust>(speakers, getTrust(input, Optional.of(speakers)));
		
	}

	
	/**
	 * Attempts to scrape n speakers from speakerideas.com by searching for the given keywords.
	 * No more than n speakers will be returned.
	 * @throws UnknownException 
	 */
	public synchronized static Collection<Speaker> getSpeakers(List<String> keywords, int n) throws UnknownException{
		
		try {
			throttler.next();
		}
		catch (RuntimeException e){
			throw new UnknownException(e);
		}
		
		ArrayList<Speaker> speakers = new ArrayList<Speaker>();
		
		boolean iterationMadeProgress = false;
		int resultPage = 1;
		
		//keep going until we have enough to stop
		while (speakers.size() < n){
			
			//use every keyword each time
			for (String searchTerm : keywords){
				try {
					Collection<Speaker> scraped = scrapeSpeakersFromSearchResults(getSearchResults(searchTerm, resultPage));
					if (scraped != null && scraped.size() > 0){
						iterationMadeProgress = true;
						speakers.addAll(scraped);
					}
				} catch (IOException e) {
					continue;
				}
				
			}
			
			if (!iterationMadeProgress){
				break;
			}
			iterationMadeProgress = false;
			resultPage++;
		}
		
		//trim down to n speakers before digging into their detailed personal page
		if (speakers.size() > n){
			speakers.subList(n, speakers.size()).clear();
		}
		
		//get the details
		for (Speaker speaker : speakers){
			
			scrapeSpeakerDetails(speaker);
		}
		
		return speakers;
	}
	
	/**
	 * Returns the HTML document of search results corresponding to a keyword to search for, and a page in the results to go to.
	 * @throws IOException 
	 */
	private static Document getSearchResults(String keyword, int resultPageNumber) throws IOException{
		
		String pageURL = String.format("http://www.speakerideas.com/page/%d/?s=%s", resultPageNumber, keyword);
		Connection connection = Jsoup.connect(pageURL).userAgent("Mozilla");
		Document document = connection.get();
		return document;
	}

	/**
	 * Scrapes as many speakers as possible from the html document of search results.
	 * Only the name, professional title and webpage of the speaker will be scraped.
	 */
	private static Collection<Speaker> scrapeSpeakersFromSearchResults(Document document){

		Collection<Speaker> scrapedSpeakers = new ArrayList<Speaker>();
		
		Elements resultElements = document.select(".uk-article.tm-article-date-true");
		
		for (Element result : resultElements){
			
			try{
				String name = result.getElementsByTag("h1").first().text();
				String pageLocation = result.select(".uk-article-title [href]").first().attr("href");
				String wholeDescription = result.select("p:not([class])").first().text();
				String firstSentence = extractFirstSentence(wholeDescription);
				URL speakerPage = new URL(pageLocation);
				
				Speaker speaker = Speaker.createSpeaker(name).setProfessionalTitle(firstSentence).addPage(speakerPage);
				scrapedSpeakers.add(speaker);
			}
			catch (NullPointerException | MalformedURLException e){
				continue;
			}
		}
		
		return scrapedSpeakers;
	}
	
	/**
	 * Uses the webpage set in the speaker's info to get their bio and topics.
	 * @throws UnknownException 
	 */
	public static void scrapeSpeakerDetails(Speaker speaker) throws UnknownException{
		
		try{
			throttler.next();
		}
		catch (RuntimeException e){
			throw new UnknownException(e);
		}
		
		//open up their page
		String pageLocation = speaker.getPages().get(0).toString();
		Connection connection = Jsoup.connect(pageLocation).userAgent("Mozilla");
		Document document;
		try {
			document = connection.get();
		} catch (IOException e) {
			return;
		}
		
		//get all of the elements of the description
		Elements bioElements = document.select(".tm-article-content p");
		StringBuilder bioBuilder = new StringBuilder();
		List<String> topicList = new ArrayList<String>();
		
		//Add paragraphs from the description until the "Topics" element is reached
		for (Element element : bioElements){			
			if (element.text().startsWith("Topics") || element.text().startsWith("topics")){
				break;
			}
			bioBuilder.append(element.text());
		}
		
		//get all of the topics in a list
		Elements topicElements = document.select(".tm-article-content ul li");
		for (Element element : topicElements){
			topicList.add(element.text());
		}
		
		speaker.setBio(bioBuilder.toString());
		speaker.setTopics(topicList);
	}
	
	/**
	 * Returns the first sentence in a paragraph.
	 */
	private static String extractFirstSentence(String paragraph){
		
		int periodI = paragraph.indexOf('.');
		if (periodI == -1){
			return "";
		}
		return paragraph.substring(0, periodI);
		
	}
	
	@Override
	public Expenditure[] getCost(SpeakersQuery args) throws Exception {
		return new Expenditure [] {new Time(25, TimeUnit.MILLISECONDS)};
	}
	
	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args,
			Optional<Collection<Speaker>> value) {
		return null;
	}


}
