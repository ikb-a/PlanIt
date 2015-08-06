package Planit.speakersuggestion.scrapespeakers.sources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import Planit.dataObjects.Speaker;
import Planit.scraping.Throttler;
import Planit.speakersuggestion.scrapespeakers.ci.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakersQuery;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.utils.BasicSource;

import org.apache.commons.collections.set.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Searches for speakers from the National Speakers Bureau using keywords.
 * @author wginsberg
 *
 */
public class NSBspeakers extends BasicSource<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements GetSpeakersContract {
	
	/*
	public static void main (String [] args) throws UnknownException{
		
		List<String> keywords = Arrays.asList(new String [] {"surf", "ocean", "beach"});
		SpeakersQuery query = new SpeakersQuery(keywords, 5, 15);
		Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> source = new NSBspeakers();
		Collection<Speaker> response = source.getOpinion(query).getValue();
		System.out.println(response);
	}
	*/
	private Throttler throttler;
	
	/**
	 * This source is throttled to 1 call per second.
	 */
	public NSBspeakers() {
		throttler = new Throttler(1, TimeUnit.SECONDS);
	}
	
	/**
	 * Attempts to return n speakers from nsb.com
	 * No guarantee is made that exactly n will be returned.
	 */
	@Override
	public Collection<Speaker> getResponse(SpeakersQuery input) throws UnknownException {
		
		int resultPage = 1;
		boolean iterationMadeProgress = false;
		@SuppressWarnings("unchecked")
		Set<Speaker> scrapedSpeakers = (Set<Speaker>) SynchronizedSet.decorate(new HashSet<Speaker>());	
		
		//scrape until we have enough to stop
		while (scrapedSpeakers.size() < input.getMinSpeakers()){
			//each time consult every keyword / category
			for (String keyword : input.getKeywords()){
				try{
					Collection<Speaker> scrape = getSpeakers(keyword, resultPage);
					if (scrape != null && scrape.size() > 0){
						iterationMadeProgress = true;
						scrapedSpeakers.addAll(scrape);
					}
				}
				catch (UnknownException e){
					continue;
				}
			}
			if (!iterationMadeProgress){
				break;
			}
			iterationMadeProgress = false;
			resultPage++;
		}
		
		return scrapedSpeakers;
	}

	/**
	 *  This method opens a webpage and extracts the speakers from it. It will throttle its access to the internet.
	 * @param keyword Searches nsb.com with this search term
	 * @param pageNumber Uses the specified page of search results
	 * @return A list of speakers, possibly empty
	 * @throws UnknownException When there was a problem getting speakers
	 */
	private List<Speaker> getSpeakers(String keyword, int pageNumber) throws UnknownException{
	
		//the list of speakers to return
		ArrayList<Speaker> speakers = new ArrayList<Speaker>();
		
		//throttle the access before attempting connection
		try {
			throttler.next();
		}
		catch (RuntimeException e){
			throw new UnknownException(e);
		}

		try{

			Connection connection = Jsoup.connect(createURL(keyword, pageNumber));
			Document document = connection.get();
			
			speakers = new ArrayList<Speaker>();

			Elements elements = document.getElementsByClass("speaker-description");
			for (Element element : elements){
				Speaker speaker = speakerFromHtmlElement(element);
				if (speaker != null){
					speakers.add(speaker);
				}
			}

		}
		catch (Exception e){
			throw new UnknownException(e);
		}

		
		return speakers;
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
			String name = result.getElementsByTag("h2").first().text();
			String title = result.getElementsByTag("h3").first().text();
			String bio = result.select("p:not([class])").last().text();
			Elements topicElements = result.select(".topic-collage li");
			ArrayList<String> topics = new ArrayList<String>(topicElements.size());
			for (int i = 0; i < topicElements.size(); i++){
				topics.add(topicElements.get(i).text());
			}
			
			Speaker speaker = Speaker.createSpeaker(name).setBio(bio).setProfessionalTitle(title).setTopics(topics);
			
			return speaker;
		}
		//if there was an error of this type then the supplied html does not have the form
		// expected, so nothing can be extracted
		catch (NullPointerException | IndexOutOfBoundsException e){
			return null;
		}
		catch (Exception e1){
			e1.printStackTrace();
			/*
			 * DEBUG
			 */
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
