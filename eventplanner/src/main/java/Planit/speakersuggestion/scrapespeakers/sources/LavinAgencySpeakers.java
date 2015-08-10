package Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Uses TheLavinAgency.com to search for speakers.
 * This source is throttled to one visit to thelavinagency.com per half second
 * @author wginsberg
 *
 */
public class LavinAgencySpeakers extends Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements
		GetSpeakersContract {
	
	private Throttler throttler;
	
	public LavinAgencySpeakers() {
		super();
		throttler = new Throttler(2, TimeUnit.SECONDS);
	}

	@Override
	public Opinion<Collection<Speaker>, SpeakerSetTrust> getOpinion(
			SpeakersQuery query) throws UnknownException {
		
		List<Speaker> speakers = new ArrayList<Speaker>();
		
		//search for each keyword to get speakers
		for (String keyword : query.getKeywords()){
			try {
				
				throttler.next();
				
				Document document = search(keyword);
				List<Speaker> scraped = getSpeakersFromHTMLPage(document);
				
				//remove extras
				if (scraped.size() > query.maxPerKeyword()){
					scraped.subList(query.maxPerKeyword(), scraped.size()).clear();			
				}
				
				if (scraped != null){
					speakers.addAll(scraped);
				}
			} catch (Exception e) {
				continue;
			}
		}

		//go to the personal page of each speaker to get more information
		for (Speaker speaker : speakers){
			try {
				
				throttler.next();
				
				Document document = getPage(speaker);
				scrapeSpeakerDetails(speaker, document);
			} catch (Exception e) {
				continue;
			}
		}
		
		return new Opinion<Collection<Speaker>, SpeakerSetTrust>(query, speakers, getTrust(query, Optional.of(speakers)), this);
	}
	
	/**
	 * Returns the HTML document for the search results of some keyword string 
	 * @param keywords
	 * @return
	 * @throws IOException 
	 */
	public static Document search(String keywords) throws IOException{
		String url = "http://www.thelavinagency.com/search/?search=" + String.join("+", keywords.split("\\s"));
		
		return Jsoup.connect(url).get();
	}
	
	/**
	 * Uses the link set in the speaker object to find their personal HTML page
	 * @param speaker
	 * @throws IOException 
	 */
	public static Document getPage(Speaker speaker) throws IOException{
		String url;
		if (speaker != null && speaker.getPages() != null && speaker.getPages().size() > 0){
			url = speaker.getPages().get(0).toString();
			return Jsoup.connect(url).get();
		}
		return null;
	}
	
	/**
	 * Returns the speakers from a search result page and all the details from the result page for each speaker
	 * @param document
	 * @return
	 */
	public static List<Speaker> getSpeakersFromHTMLPage(Document document){
		
		Elements speakerElements = document.select(".speaker_information");
		List<Speaker> speakers = new ArrayList<Speaker>(speakerElements.size());
		for (Element element : speakerElements){
			String name = element.select(".title").first().text();
			String title = element.select(".caption").first().text();
			String link = element.select(".title a").first().attr("href");
			URL url;
			try{
				url = new URL(link);
			}
			catch (MalformedURLException e){
				url = null;
			}
			Speaker newSpeaker = Speaker.createSpeaker(name).setProfessionalTitle(title);
			if (url != null){
				newSpeaker.addPage(url);
			}
			speakers.add(newSpeaker);
		}
		return speakers;
		
	}
	
	/**
	 * Using the html document of the speaker's personal page, scrapes the bio and topics of interest from the page and sets them in the speaker object
	 * @param document
	 */
	public static void scrapeSpeakerDetails(Speaker speaker, Document document){
		
		//we may get some or all of these elements
		String headline;
		String bio;
		String extendedBio;
		
		try{
			headline = document.select("#content_container #headline").first().text();
		}
		catch (NullPointerException e){
			headline = "";
		}
		try{
			bio = document.select("#content_container .bio").first().text();
		}
		catch (NullPointerException e){
			bio = "";
		}
		try{
			extendedBio = document.select("#content_container #bio_content").first().text();
		}
		catch (NullPointerException e){
			extendedBio = "";
		}
		speaker.setBio(headline + bio + extendedBio);
		
		Elements topicElements = document.select("#topics #topic_titles .topic strong");
		if (topicElements != null && topicElements.size() > 0){
			List<String> topics = new ArrayList<String>(topicElements.size());
			for (Element element : topicElements){
				topics.add(element.text());
			}
			speaker.setTopics(topics);			
		}
	}
	
	@Override
	public Expenditure[] getCost(SpeakersQuery args) throws Exception {
		return new Expenditure [] {};
	}

	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args,
			Optional<Collection<Speaker>> value) {
		return new SpeakerSetTrust();
	}

}
