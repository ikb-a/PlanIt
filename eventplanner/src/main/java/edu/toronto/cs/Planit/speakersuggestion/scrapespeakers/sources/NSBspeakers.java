package edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

import org.apache.commons.collections.set.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Speakers from the National Speakers Bureau
 * @author wginsberg
 *
 */
public class NSBspeakers extends BasicSource<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements GetSpeakersContract {
	
	Throttler throttler;
	
	/**
	 * This source is throttled to 1 call per second.
	 */
	public NSBspeakers() {
		throttler = new Throttler(1, TimeUnit.SECONDS);
	}
	
	/**
	 * Queries nsb.com for speakers. Will attempt to return at least the
	 * minimum number of speakers requested, but won't cap the results if there are too many.
	 */
	@Override
	public Collection<Speaker> getResponse(SpeakersQuery input) throws UnknownException {
		
		@SuppressWarnings("unchecked")
		Set<Speaker> output = (Set<Speaker>) SynchronizedSet.decorate(new HashSet<Speaker>());
		int resultPage = 1;
		//query over the pages until enough results are returned
		while (output.size() < input.getMinSpeakers()){
			try{
				String city;
				try{
					city = input.getEvent().getVenue().getAddress().getCity();
				}
				catch (NullPointerException e){
					city = "";
				}
				Collection<Speaker> speakers = getSpeakers(city, resultPage);
				output.addAll(speakers);
				resultPage++;
			}
			catch (UnknownException e){
				//if there was an error when retrieving the first result, then something went wrong
				if (output.isEmpty()){
					throw new UnknownException();
				}
				else{
					break;
				}
			}
		}
		
		return output;
	}

	/**
	 *  This method opens a webpage and extracts the speakers from it. It will throttle its access to the internet.
	 * @param cityName Searches nsb.com with this search term
	 * @param pageNumber Uses the specified page of search results
	 * @return A list of speakers, possibly empty
	 * @throws UnknownException When there was a problem getting speakers
	 */
	private List<Speaker> getSpeakers(String cityName, int pageNumber) throws UnknownException{
	
		//get the url
		URL url;
		try{
			url = new URL(createURL(cityName, pageNumber));
		}
		catch (MalformedURLException e){
			throw new UnknownException(e);
		}
		
		//the list of speakers to return
		ArrayList<Speaker> speakers = new ArrayList<Speaker>();
		
		//throttle the access before attempting connection
		if (!throttler.next()){
			System.err.println("WARNING : throttling failed for nsb.com");
		}
		
		//open the connection
		URLConnection connection = getConnection(url);
				
		if (connection != null){
			try{

				Document document = Jsoup.parse(connection.getInputStream(), null, url.toString());
				
				speakers = new ArrayList<Speaker>();

				Elements elements = document.getElementsByClass("result");
				for (Element element : elements){
					Speaker speaker = speakerFromHtmlElement(element);
					if (speaker != null){
						speakers.add(speaker);
					}
				}
				
			}
			catch (IOException e){
				throw new UnknownException(e);
			}
		}
		
		return speakers;
	}
	
	/**
	 * Returns a URL connection to the specified url, or null if a connection could not be established.
	 * @param query
	 * @return
	 */
	static private URLConnection getConnection(URL url){
	
		URLConnection connection;
		try{
			connection = url.openConnection();
		}
		catch(IOException e){
			return null;
		}
		connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        
        return connection;
	}
	
	static private String createURL(String keyword, int pageNum){
		return String.format("http://nsb.com/search/?keyword=%s&dialogue=keyword&pg=%d", keyword, pageNum);
	}
	
	/**
	 * Taking an html result element, extracts speaker details and returns a new speaker object
	 * @param result
	 * @return
	 */
	static private Speaker speakerFromHtmlElement(Element result){
		
		try{
			//scrape the description div
			Element descriptionElement = result.getElementsByClass("speaker-description").first();
			
			//get name, title
			Element nameElement = descriptionElement.getElementsByTag("h2").first();
			Element titleElement = descriptionElement.getElementsByTag("h3").first();			
			//scrape the topic collage div
			Element topicsElement = result.getElementsByClass("topic-collage").first();
			
			//get the list of topic elements
			Elements topicElements = topicsElement.getElementsByTag("li");
			
			//get the link to the speaker's page
			Element actionsElement = result.getElementsByClass("speaker-actions").first();
			Element linkElement = actionsElement.getElementsByTag("a").first();
			String partialLink = linkElement.attr("href");
			String fullLink = "http://nsb.com" + partialLink;
			URL link = new URL(fullLink);
			
			//initialize the speaker
			Speaker speaker = Speaker.createSpeaker(nameElement.text());
			speaker.setProfessionalTitle(titleElement.text());
			speaker.addPage(link);
			ArrayList<String> topics = new ArrayList<String>(topicElements.size());
			for (int i = 0; i < topicElements.size(); i++){
				topics.add(topicElements.get(i).text());
			}
			speaker.setTopics(topics);
			
			return speaker;
		}
		//if there was an error of this type then the supplied html does not have the form
		// expected, so nothing can be extracted
		catch (NullPointerException | IndexOutOfBoundsException | MalformedURLException e){
			return null;
		}
	}
	
	/**
	 * This source will time out after 0.1 second per name requested
	 * i.e. if there are 10 results on the page then all of them could be retrieved in 1 second
	 * @param args
	 * @return
	 * @throws Exception
	 */
	@Override
	public Expenditure[] getCost(SpeakersQuery query) throws Exception {
		int totalSeconds = (int) Math.round(query.getMinSpeakers() * 0.1);
		return new Expenditure [] {new Time(totalSeconds, TimeUnit.SECONDS)};
	}

	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args, Optional<Collection<Speaker>> value) {
		return null;
	}

}
