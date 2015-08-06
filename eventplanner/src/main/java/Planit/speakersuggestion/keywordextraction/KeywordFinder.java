package Planit.speakersuggestion.keywordextraction;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

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
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Estimate;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * A class which finds keywords for an event using a contributional implementation.
 * @author wginsberg
 *
 */
public class KeywordFinder {

	/**
	 * Runs keyword finding on one event and prints the result to standard output.
	 * @param args
	 */
	public static void main (String [] args){
		
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
		Estimate<List<String>, Void> estimate;

		/*
		 * Show the event in console
		 */
		System.out.println("Extracting keywords for event: ");
		System.out.println(event.toString());
		System.out.println();
		
		/*
		 * Show the first estimate of the result
		 */
		boolean needsRefinement;
		estimate = keywords.getResponse(event, budget);
		needsRefinement = !estimate.isDone();
		System.out.println("Estimate of keywords: ");
		Optional<Result<List<String>, Void>> currentEstimate = estimate.getCurrent();
		if (currentEstimate.isPresent()){
			System.out.println(currentEstimate.get().toString());
		}
		else{
			System.out.println("None");
		}
		System.out.println();
		
		/*
		 * Show a refined estimate
		 */
		if (needsRefinement){
			System.out.println("Refined estimate: ");
			try {
				Result<List<String>, Void> result  = estimate.get();
				System.out.println(result.toString());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
	private CI<Event, List<String>, Void, Void> ci;
	private Aggregator<List<String>, Void, Void> agg;

	/**
	 * Instantiates a new key word finder using the sources which are currently available.
	 */
	public KeywordFinder(){
		agg = new KeywordAggregator();
		ci = new CI<Event, List<String>, Void, Void>
		(EventKeywordsContract.class, agg, new AllSelector<Event, List<String>, Void>());
	}
	
	/**
	 * Perform keyword finding on an event
	 * @param event The event to find keywords for
	 * @param budget The budget to spend on finding keywords
	 * @return An CI estimate object from which the keywords can be taken
	 */
	public Estimate<List<String>, Void> getResponse(Event event, Allowance [] budget){
		Estimate<List<String>, Void> estimate = ci.apply(event, budget);
		return estimate;
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
