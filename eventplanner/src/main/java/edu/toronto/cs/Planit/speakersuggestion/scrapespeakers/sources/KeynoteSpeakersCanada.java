package edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.ci.GetSpeakersContract;
import edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.ci.SpeakerSetTrust;
import edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.ci.SpeakersQuery;
import edu.toronto.cs.se.ci.utils.BasicSource;
import edu.toronto.cs.Planit.scraping.Throttler;

/**
 * Grabs speakers from the Keynote Speakers Canada website.
 * This source is throttled.
 * @author wginsberg
 *
 */
public class KeynoteSpeakersCanada extends BasicSource<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements
		GetSpeakersContract {
	
	public static String [] categories = {
			"Business",
			"Economy",
			"Education",
			"Health and Science",
			"Environment",
			"Politics",
			"Technology",
			"Coaches",
			"Olympians",
			"Sport Commentary",
			"Sport Stars",
			"Great Adventurers",
			"Humour",
			"Comedy",
			"Music",
			"Bilingual Speakers"};

	static String siteURLbase = "http://www.keynotespeakerscanada.ca";
	static String searchURLbase = "http://www.keynotespeakerscanada.ca/category-search?category=";
	static String speakerURLbase = "http://www.keynotespeakerscanada.ca/speaker";
	static String charset = "UTF-8";
	
	static Throttler throttler;
	
	public KeynoteSpeakersCanada() {
		throttler = new Throttler(2, TimeUnit.SECONDS);
	}
	
	@Override
	public Collection<Speaker> getResponse(SpeakersQuery input)
			throws UnknownException {
		
		if (!throttler.next()){
			System.err.println("Warning : throttling failed for Keynote Speakers Canada");
		}
		
		Set<Speaker> speakers = new HashSet<Speaker>(input.getMinSpeakers());
		
		//look on the page for each category
		for (int i = 0; i < categories.length; i++){
			try{
				//we assume that no speaker will appear twice on this website
				speakers.addAll(getSpeakers(categories[i]));
			}
			catch (UnknownException e){
				continue;
			}
		}
		
		//pick a random subset to cap at the maximum allowed to return
		int nToRemove = speakers.size() - input.getMaxSpeakers();
		if (nToRemove > 0){
			List<Speaker> speakerList = new ArrayList<Speaker>(speakers);
			Collections.shuffle(speakerList);
			speakers.removeAll(speakerList.subList(0, nToRemove));
		}
		/*
		int numToRemove = speakers.size() - input.getMaxSpeakers();
		Set<Speaker> toRemove = new HashSet<Speaker>();
		for (Speaker randomSpeaker : speakers){
			if (numToRemove < 1){
				break;
			}
			toRemove.add(randomSpeaker);
			numToRemove--;
		}
		speakers.removeAll(toRemove);
		*/
		
		//grab the details of each speaker
		for (Speaker speaker : speakers){
			if (!throttler.next()){
				System.err.println("Warning : throttling failed for Keynote Speakers Canada");
			}
			getDetails(speaker);
		}
		
		//if nothing was found then something went wrong
		if (speakers.isEmpty()){
			throw new UnknownException();
		}
		else{
			return speakers;
		}
	}
	
	/**
	 * Returns the speakers associated with the given category
	 * @param category
	 * @return
	 */
	public static Set<Speaker> getSpeakers(String category) throws UnknownException{
		try {
			return getSpeakers(getSearchPageURL(category));
		} catch (MalformedURLException e) {
			throw new UnknownException(e);
		}
	}
	
	/**
	 * Examines then supplied search results page and returns the speakers on it.
	 * Speakers will have the name field set, and will have their webpage too.
	 * @param searchPage
	 * @return
	 */
	static Set<Speaker> getSpeakers(URL searchPage) throws UnknownException{
		
		Set<Speaker> speakers = null;
		
		try{
			//get the data from the webpage
			Document htmlDocument = Jsoup.connect(searchPage.toString()).get();
			Elements fieldContents = htmlDocument.getElementsByClass("field-content");
			
			//create a set of speakers
			speakers = new HashSet<Speaker>();
			for (Element fieldContent : fieldContents){
				
				//this element contains all of what we want for one speaker
				Element element = fieldContent.getElementsByTag("a").first();
				
				//check if a name is found
				String name = element.text();
				if (name.length() < 1){
					continue;
				}
				
				//create the speaker
				Speaker speaker = Speaker.createSpeaker(name);
				
				//try to set their webpage
				String speakerPageName = element.attr("href");
				try{
					URL speakerPage = new URL(siteURLbase + speakerPageName);
					speaker.addPage(speakerPage);
				}
				catch(MalformedURLException e){
					continue;
				}
				
				speakers.add(speaker);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		//return the speakers or throw Unknown
		if (speakers == null){
			throw new UnknownException();
		}
		else{
			return speakers;
		}
	}
	
	/**
	 * Uses the URL specified as the webpage of the speaker to get their details.
	 * @param speaker
	 * @return
	 */
	private static void getDetails(Speaker speaker){
		
		String pageURL;
		Document document;
		Elements elements;
		Element headInfo;
		
		try {
			//get the data
			pageURL = speaker.getPages().get(0).toString();
			document = Jsoup.connect(pageURL).get();
			elements = document.getElementsByClass("mobile-invisible");
			headInfo = elements.get(0);
			//this will also contain the name, so remove it
			String professionalTitle = headInfo.text().substring(speaker.getName().length());
			
			Element terms = document.getElementsByClass("terms").get(0);
			Elements termElements = terms.getElementsByTag("a");
			List<String> topics = new ArrayList<String> (termElements.size());
			for (int i = 0; i < termElements.size(); i++){
				topics.add(termElements.get(i).text());
			}
			
			//set the data in the speaker
			speaker.setProfessionalTitle(professionalTitle);
			speaker.setTopics(topics);
			
		} catch (IOException | IndexOutOfBoundsException e) {
			return;
		}
	}
	
	/**
	 * Returns a URL which can be used to search the given category for speakers
	 * @param category
	 * @return
	 * @throws MalformedURLException
	 */
	static URL getSearchPageURL(String category) throws MalformedURLException{
		return new URL(searchURLbase + parseCategory(category));
	}
	
	/**
	 * Parses the category name so that it can be used in a URL
	 * @return
	 */
	private static String parseCategory(String category){
		return category.toLowerCase().replaceAll(" ", "+");
	}
	
	/**
	 * There is cost for each category page visited according to the amount of throttling on this source
	 */
	@Override
	public Expenditure[] getCost(SpeakersQuery args) throws Exception {
		Expenditure [] cost = new Expenditure [] {new Time(throttler.getMinTimeBetweenCalls(), throttler.getTimeUnit())};
		return cost;
	}

	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args,
			Optional<Collection<Speaker>> value) {
		return null;
	}


}
