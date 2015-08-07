package Planit.speakersuggestion.scrapespeakers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.scrapespeakers.sources.KeynoteSpeakersCanada;
import Planit.speakersuggestion.scrapespeakers.sources.LavinAgencySpeakers;
import Planit.speakersuggestion.scrapespeakers.sources.NSBspeakers;
import Planit.speakersuggestion.scrapespeakers.util.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetUnionAggregator;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Estimate;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * A class for searching for speakers on the internet by using keyword terms.
 * @author wginsberg
 *
 */
public class SpeakerRetriever {
	
	/**
	 * Perform speaker retrieval on one list of keywords and prints the results to standard output.
	 * @param args
	 */
	public static void main(String [] args){
		
		/*
		 * Create a query
		 */
		List<String> keywords = Arrays.asList(new String [] {"coffee", "drinks", "Toronto"});
		SpeakersQuery query = new SpeakersQuery(keywords,  10, 15);

		/*
		 * Set up for speaker searching
		 */
		registerSources();
		Allowance [] budget = new Allowance [] {new Time(10, TimeUnit.SECONDS)};
		SpeakerRetriever speakers = new SpeakerRetriever();

		/*
		 * Show the query in console
		 */
		System.out.println("Searching for speakers based on keywords: \n");
		System.out.println("	" + keywords.toString());
		System.out.println();
		

		try {
			Result<Collection<Speaker>, Double> result = speakers.getResponse(query, budget).get();
			System.out.println("Search results: \n");
			System.out.println("	" + result.getValue().toString());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	private CI <SpeakersQuery, Collection<Speaker>, SpeakerSetTrust, Double> ci;
	
	public SpeakerRetriever(){
		ci = new CI<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust, Double>
			(GetSpeakersContract.class,
					new SpeakerSetUnionAggregator(),
					new AllSelector<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust>());
	}

	/**
	 * Performs a search for speakers on the internet using contributional implementation.
	 * @param query The query dictating how to search for speakers
	 * @return An estimate in progress from a contributional implementation from which a collection of speakers can be taken
	 */
	public Estimate<Collection<Speaker>, Double> getResponse(SpeakersQuery query, Allowance [] budget){
		return ci.apply(query, budget);
	}
	
	/**
	 * Registers a preset collection of sources for the ci.
	 */
	public static void registerSources(){
		Contracts.register(new KeynoteSpeakersCanada());
		Contracts.register(new NSBspeakers());
		Contracts.register(new LavinAgencySpeakers());
	}
}
