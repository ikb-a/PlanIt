package Planit.speakersuggestion.keywordextraction;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import Planit.speakersuggestion.keywordextraction.sources.EventCitySource;
import Planit.speakersuggestion.keywordextraction.sources.EventKeywordAdaptor;
import Planit.speakersuggestion.keywordextraction.sources.MostRepeatedWords;
import Planit.speakersuggestion.keywordextraction.sources.UncommonEnglishWords;
import Planit.speakersuggestion.keywordextraction.sources.YahooKeywordGeneration;
import Planit.speakersuggestion.keywordextraction.util.EventKeywordsContract;
import Planit.speakersuggestion.keywordextraction.util.KeywordAggregator;
import Planit.speakersuggestion.keywordextraction.util.KeywordSourceSelector;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Result;

/**
 * @deprecated The Non-CI framework version of this class should be used to avoid bugs.
 * 
 * 
 * Finds keywords for an event using a contributional implementation.
 * 
 * The sources used are:
 * 		Keywords generated from Yahoo Content Analysis API using the event's description
 * 		The most uncommon English words in the event description
 * 		The most repeated words in the event description
 * 		The city of the event as a keyword
 * 
 * Sources are queried with a fall-back composition: The first source used is always
 * Yahoo content analysis, and iff it gives no opinion then every other source is used.
 * 
 * @author wginsberg
 *
 */
public class KeywordFinder {

	/**
	 * Runs keyword finding on one event and prints the result to standard output.
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws UnknownException 
	 */
	public static void main (String [] args) throws InterruptedException, ExecutionException, UnknownException{
		
		/*
		 * Create an event
		 */
		Event event = Event
				.createEvent("Hiroshima Memorial Day")
				.setDescription("1 pm - 2 pm, Nembutsu to pray for world peace and all the victims of "
						+ "the atomic bombing of Hiroshima (70 years ago)  at Tao Sangha Healing Centre. "
						+ "4:30 pm - 8:45 pm, We will join the Hiroshima Nagasaki Day Coalition at the "
						+ "Church of the Holy Trinity. http://www.hiroshimadaycoalition.ca (from "
						+ "4:30 to 6:30 we will provide Charity Shiatsu and donate all proceeds to the "
						+ "Hiroshima Nagasaki Day Coalition for the abolishment of nuclear weapons. ")
						.setVenue(new Venue("The Church of the Holy Trinity (beside Eaton Centre), Toronto",
								new Address("10", "Trinity Square", "Toronto", "ON", "Canada", "")));

		/*
		 * Set up for keyword finding
		 */
		Allowance [] budget = new Allowance [] {new Time(1, TimeUnit.SECONDS)};
		KeywordFinder.registerSources();
		KeywordFinder keywords = new KeywordFinder();

		/*
		 * Show the event in console
		 */
		System.out.println("Extracting keywords for event:\n");
		System.out.println("	" + event.toString());
		System.out.println();
		
		List<String> result = keywords.getResponse(event, budget);
		System.out.println("Keywords for the event are:\n");
		System.out.println("	" + result.toString());
	}
	
	private CI<Event, List<String>, Void, Void> ci;
	private Aggregator<List<String>, Void, Void> agg;

	/**
	 * Instantiates a new key word finder using the sources which are currently available.
	 */
	public KeywordFinder(){
		agg = new KeywordAggregator();
		ci = new CI<Event, List<String>, Void, Void>
		(EventKeywordsContract.class, agg, new KeywordSourceSelector());
	}
	
	/**
	 * Perform keyword finding on an event.
	 * Returns the keywords as well as sets them in the event.
	 * 
	 * @param event The event to find keywords for
	 * @param budget The budget to spend on finding keywords
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws UnknownException 
	 */
	public List<String> getResponse(Event event, Allowance [] budget) throws InterruptedException, ExecutionException, UnknownException{
	
		Result<List<String>, Void> result = ci.applySync(event, budget);
		event.setKeyWords(result.getValue());
		return result.getValue();
	}
	
	/**
	 * Register a preset collection of sources for this CI.
	 * @return 
	 */
	public static void registerSources(){

		//event -> keyword sources
		Contracts.register(new EventCitySource());
		
		//words -> keywords sources
		Contracts.register(new YahooKeywordGeneration());
		Contracts.register(new MostRepeatedWords());
		Contracts.register(new UncommonEnglishWords());
		
		//adaptors
		Contracts.register(new EventKeywordAdaptor(YahooKeywordGeneration.class));
		Contracts.register(new EventKeywordAdaptor(MostRepeatedWords.class));
		Contracts.register(new EventKeywordAdaptor(UncommonEnglishWords.class));

	}
}
