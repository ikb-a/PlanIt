package Planit.speakersuggestion.scrapespeakers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.scrapespeakers.sources.KeynoteSpeakersCanada;
import Planit.speakersuggestion.scrapespeakers.sources.LavinAgencySpeakers;
import Planit.speakersuggestion.scrapespeakers.sources.NSBspeakers;
import Planit.speakersuggestion.scrapespeakers.sources.PremiereSpeakersBureau;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetUnionAggregator;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;

/**
 * Feature for scraping speakers off of the internet. Does not use the CI framework.
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
		SpeakerRetriever speakers = new SpeakerRetriever();

		/*
		 * Show the query in console
		 */
		System.out.println("Searching for speakers based on keywords: \n");
		System.out.println("	" + keywords.toString());
		System.out.println();
		
		Collection<Speaker> result = speakers.getResponse(query);
		System.out.println("Search results: \n");
		System.out.println("	" + result.toString());
		
	}
	
	KeynoteSpeakersCanada keynoteSource;
	LavinAgencySpeakers lavinSource;
	NSBspeakers nsbSource;
	PremiereSpeakersBureau premiereSource;

	SpeakerSetUnionAggregator aggregator;
	
	public SpeakerRetriever(){
		nsbSource = new NSBspeakers();
		premiereSource = new PremiereSpeakersBureau();
		
		aggregator = new SpeakerSetUnionAggregator();
	}
	
	/**
	 * Queries all available speaker sources and returns their aggregated responses.
	 * @param query
	 * @return
	 */
	public Collection<Speaker> getResponse(SpeakersQuery query){

		List<Opinion<Collection<Speaker>, SpeakerSetTrust>> opinions =
				new ArrayList<Opinion<Collection<Speaker>, SpeakerSetTrust>>(3);
		
		SpeakersQuery dividedQuery =
				new SpeakersQuery(query.getKeywords(), query.getMinSpeakers() / 3, query.getMaxSpeakers() / 3);
		if (dividedQuery.getMinSpeakers() < 1){
			dividedQuery.setMinSpeakers(1);
		}
		if (dividedQuery.getMaxSpeakers() < 1){
			dividedQuery.setMaxSpeakers(1);
		}

		try{
			opinions.add(premiereSource.getOpinion(dividedQuery));
		} catch (UnknownException e){}
		try{
			opinions.add(nsbSource.getOpinion(dividedQuery));
		} catch (UnknownException e){}
		
		Optional<Result<Collection<Speaker>, Double>> aggregation = aggregator.aggregate(opinions);
		if (aggregation.isPresent()){
			return aggregation.get().getValue();
		}
		else{
			return new ArrayList<Speaker>(0);
		}
	}
	
}
