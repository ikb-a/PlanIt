package Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Planit.dataObjects.Speaker;
import Planit.internet.GoogleSearch;
import Planit.internet.SearchResult;
import Planit.scraping.Throttler;

/**
 * Scrapes speakers from National Speakers Bureau by doing a Google Search and following links to nsb.com
 * @author wginsberg
 *
 */
public class NSBsearchByGoogle {

	private static Throttler throttler;
	
	/**
	 * Search google for speakers on nsb.com who match the keyword and return them.
	 * @param keyword A keyword to search for
	 * @param n the number of speakers to return
	 * @return
	 */
	public static List<Speaker> getSpeakers(String keyword, int n){
		List<Speaker> speakers = getSpeakersFromSearchResults(keyword);
		//trim extras before extracting details
		if (speakers.size() > n){
			speakers.subList(n, speakers.size()).clear();
		}
		for (Speaker speaker : speakers){
			extractDetails(speaker);
		}
		return speakers;
	}
	
	/**
	 * Searches for the keyword and creates a new speaker object corresponding to every link there is to a speaker on nsb.com
	 * @param keyword
	 * @return A list of speaker objects which contain only the link to their personal page
	 */
	private static List<Speaker> getSpeakersFromSearchResults(String keyword){
		
		//get the results
		List<SearchResult> results = GoogleSearch.search("site:nsb.com/speakers " + keyword);
		List<Speaker> speakers = new ArrayList<Speaker>(results.size());
		
		//scrape from each result
		for (SearchResult result : results){
			try{
				Speaker speaker = Speaker.createSpeaker("");
				speaker.addPage(new URL(result.getLink()));
				speakers.add(speaker);
			}
			catch (MalformedURLException e){
				continue;
			}
		}
		
		return speakers;
	}
	
	/**
	 * Uses the link to the speaker's personal page in order to get details about their name, biography, and speech topics.
	 * This method is throttled to 1 call per second.
	 * @param speaker
	 */
	synchronized private static void extractDetails(Speaker speaker){
		
		getThrottler().next();
		
		Document document;
		try {
			document = Jsoup.connect(speaker.getPages().get(0).toString()).get();
		} catch (IOException e) {
			return;
		}
		
		try{
			String name;
			String title;
			String bio;
			List<String> topics;
			
			name = document.select("h3[itemprop=name]").first().text();
			title = document.select("h4[itemprop=jobtitle]").first().text();
			bio = document.select("#summaryprofile").text();
			
			Elements topicElements = document.select(".topic");			
			topics = new ArrayList<String>(topicElements.size());
			for (Element element : topicElements){
				topics.add(element.text());
			}
			
			speaker.setName(name);
			speaker.setProfessionalTitle(title);
			speaker.setBio(bio);
			speaker.setTopics(topics);
		}
		catch (NullPointerException e){
			return;
		}
	}
	
	private static synchronized Throttler getThrottler(){
		if (throttler == null){
			throttler = new Throttler(1, TimeUnit.SECONDS);
		}
		return throttler;
	}
}
