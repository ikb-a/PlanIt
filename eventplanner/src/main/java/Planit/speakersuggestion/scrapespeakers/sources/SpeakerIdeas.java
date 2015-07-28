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
import Planit.speakersuggestion.scrapespeakers.ci.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakersQuery;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Scrapes speakers from http://www.speakerideas.com/ by searching for keywords.
 * @author wginsberg
 *
 */
public class SpeakerIdeas extends Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements
		GetSpeakersContract {
	
	private Throttler throttler;
	
	/*
	public static void main (String [] args) throws UnknownException{
		
		Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> source = new SpeakerIdeas();
		List<String> keywords = Arrays.asList(new String [] {"energy", "infrastrucute", "nuclear"});
		SpeakersQuery query = new SpeakersQuery(keywords, 25, 25);
		Collection<Speaker> speakers = source.getOpinion(query).getValue();
		
		System.out.println(speakers);
	}
	*/
	
	public SpeakerIdeas(){
		throttler = new Throttler(30, TimeUnit.MINUTES);
	}
	
	@Override
	public Opinion<Collection<Speaker>, SpeakerSetTrust> getOpinion(
			SpeakersQuery input) throws UnknownException {
		
		if (throttler.next() == false){
			System.err.println("Warning: throttling failed for speakerideas.com");
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
	 * No guarantee is made that exactly n speakers will be returned.
	 */
	public static Collection<Speaker> getSpeakers(List<String> keywords, int n){
		
		Collection<Speaker> speakers = new ArrayList<Speaker>();
		
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
