package Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Planit.scraping.Throttler;
import Planit.speakersuggestion.scrapespeakers.util.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;
import Planit.speakersuggestion.wordsimilarity.Word2Vec;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;

import com.google.common.base.Optional;

import Planit.dataObjects.Speaker;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Grabs speakers from the Keynote Speakers Canada website.
 * One of the 16 categories of speakers will be chosen based on supplied keywords, and this is where the suggested speakers will come from.
 * In order to choose one of the categories, Word2Vec will be used to find the category which has the highest overall similarity with the keywords.
 * This source is throttled.
 * @author wginsberg
 *
 */
public class KeynoteSpeakersCanada extends Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements
		GetSpeakersContract {

	/**
	 * Categories as they appear on the website
	 */
	private static final String [] rawCategories = {
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

	/**
	 * Categories, as they can be represented outside of this class
	 */
	private static final String [] categories = {
		"business",
		"economy",
		"education",
		"health",
		"science",
		"environment",
		"politics",
		"technology",
		"athletics",
		"sports",
		"adventure",
		"humour",
		"comedy",
		"music",
		"french"};
	
	private static List<String> categoryList;
	
	private static String siteURLbase = "http://www.keynotespeakerscanada.ca";
	private static String searchURLbase = "http://www.keynotespeakerscanada.ca/category-search?category=";
	
	private static Throttler throttler;
	
	public KeynoteSpeakersCanada() {
		throttler = new Throttler(2, TimeUnit.SECONDS);
	}
	
	/**
	 * Attempts to return n speakers in the best category that can be matched to the keywords.
	 */
	public static Collection<Speaker> getSpeakers(List<String> keywords, int n) throws UnknownException{
		
		try {
			throttler.next();
		}
		catch (RuntimeException e){
			throw new UnknownException(e);
		}
		
		String [] relevantCategories;
		try {
			relevantCategories = getMostRelevantCategories(keywords);
		} catch (IOException e) {
			throw new UnknownException(e);
		}
		
		Collection<Speaker> scrapedSpeakers = new HashSet<Speaker>();
		for (int i = 0; i< relevantCategories.length; i++){
			if (scrapedSpeakers.size() >= n){
				break;
			}
			Collection<Speaker> speakers = getSpeakers(categories[i]);
			scrapedSpeakers.addAll(speakers);
		}
		
		//remove extras
		scrapedSpeakers = randomSubset(scrapedSpeakers, n);
		
		for (Speaker speaker : scrapedSpeakers){
			getDetails(speaker);
		}
		
		return scrapedSpeakers;
	}
	
	/**
	 * Returns a random subset of speakers
	 * @param speakers the superset to draw from
	 * @param n the number of speakeres to return
	 * @return
	 */
	private static Collection<Speaker> randomSubset(Collection<Speaker> speakers, int n){
		if (speakers == null){
			return new ArrayList<Speaker>(0);
		}
		
		Collection<Speaker> subset = new ArrayList<Speaker>(n);
		
		Random random = new Random();
		
		for (int q = 0; q < n && !speakers.isEmpty(); q++){
			int rand_i = random.nextInt(speakers.size());
			Iterator<Speaker> iter = speakers.iterator();
			for (int i = 0 ; i < rand_i - 1; i++){
				iter.next();
			}
			subset.add(iter.next());
		}
		
		return subset;
	}
	
	@Override
	public Opinion<Collection<Speaker>, SpeakerSetTrust> getOpinion(
			SpeakersQuery args) throws UnknownException {
		Collection<Speaker> toSuggest = getSpeakers(args.getKeywords(), args.getMinSpeakers());
		if (toSuggest == null){
			return new Opinion<Collection<Speaker>, SpeakerSetTrust>(args, null, null, this);
		}
		return new Opinion<Collection<Speaker>, SpeakerSetTrust>(args, toSuggest, getTrust(args, Optional.of(toSuggest)), this);
	}
	
	
	/**
	 * Returns the speakers associated with the given category, only finding their name and personal page.
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
			throw new UnknownException(e);
		}

		return speakers;
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
		Elements bioElements;
		
		try {
			//get the data
			pageURL = speaker.getPages().get(0).toString();
			document = Jsoup.connect(pageURL).get();
			elements = document.getElementsByClass("mobile-invisible");
			headInfo = elements.get(0);
			bioElements = document.select(".content p");
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
			speaker.setBio(bioElements.text());
			
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
	 * Given a list of keywords, determines the categories that will be the most similar to the keywords.
	 * @param keywords A list of keywords for an event
	 * @return The best categories out of the 16 possible ones for Keynote Speakers Canada, or all categories if no choice could be made
	 * @throws IOException 
	 */
	public static String [] getMostRelevantCategories(List<String> keywords) throws IOException{
		
		getCategoryList();
		
		double [][] similarityMatrix = Word2Vec.getInstance().similarity(categoryList, keywords);
		
		if (similarityMatrix == null){
			return categories;
		}
		
		//pick the category with the maximum similarity score in the matrix
		//here, the maximum score is given to the word with the highest total similarity across keywords
		int bestWordI = MatrixUtil.maximalRow(similarityMatrix);
		if (bestWordI == -1){
			return categories;
		}
		
		return getRawCategories(categoryList.get(bestWordI));
	}
	
	/**
	 * Returns the list of categories as parsed strings. These should not be assumed to correspond literally to categories on the website.
	 * @return
	 */
	public static List<String> getCategoryList(){

		if (categoryList == null){
			categoryList = Arrays.asList(categories);
		}
		
		return categoryList;
	}

	/**
	 * Given an index in the categories list, returns the corresponding indices in the raw category list.
	 * For example if "science" is at index 5, and "Health and Science" is at index 4, then [4] is returned
	 * @param i
	 * @return
	 */
	private static int [] getRawCategories(int i){
		
		if (i < 3){
			return new int [] {i};
		}
		if (i >= 3 && i <= 4){
			return new int [] {3};		
		}
		if (i >= 5 && i <= 7){
			return new int [] {i - 1};
		}
		if (i == 8){
			return new int [] {7, 8};
		}
		if (i == 9){
			return new int [] {9, 10};
		}
		return new int [] {i + 1};
	}
	
	public static String [] getRawCategories(String c){
		int i = getCategoryList().indexOf(c);
		int [] is = getRawCategories(i);
		String [] categories = new String [is.length];
		for (int q = 0; q < is.length; q++){
			categories[q] = rawCategories[is[q]];
		}
		return categories;
	}
	
	/**
	 * There is cost for each category page visited according to the amount of throttling on this source
	 */
	@Override
	public Expenditure[] getCost(SpeakersQuery args) throws Exception {
		return new Expenditure [] {};
	}

	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args,
			Optional<Collection<Speaker>> value) {
		return null;
	}





}
