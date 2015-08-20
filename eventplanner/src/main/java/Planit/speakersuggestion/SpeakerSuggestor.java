package Planit.speakersuggestion;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.SuggestedSpeakers;
import Planit.speakersuggestion.keywordextraction.KeywordFinder;
import Planit.speakersuggestion.scrapespeakers.SpeakerRetriever;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;
import Planit.speakersuggestion.similarity.SuitabilityJudge;
import Planit.speakersuggestion.similarity.SuitabilityJudgeRankingAggregation;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class SpeakerSuggestor {
	
	/*
	 * Singleton object
	 */
	static private SpeakerSuggestor instance;
	private SpeakerSuggestor() throws Exception{
		speakerRetriever = new SpeakerRetriever();
		speakerJudge = new SuitabilityJudgeRankingAggregation();
	}
	/**
	 * @return An instance of SpeakerSuggestor
	 * @throws Exception if there was a problem initiating a SpeakerSuggestor
	 */
	public static SpeakerSuggestor getInstance() throws Exception{
		if (instance == null){
			instance = new SpeakerSuggestor();
		}
		return instance;
	}
	
	private	SpeakerRetriever speakerRetriever;
	private SuitabilityJudge speakerJudge;
	
	
	/**
	 * Suggests speakers for an event.
	 * @param event The event to which the speakers are suggested for
	 * @param softMin The desired minimum number of speakers to suggest
	 * @param softMax The desired maximum number of speakers to suggest
	 * @param budget Three Time values. The first for how long to spend getting keywords, second time to spend grabbing speakers from the internet, third how long to spend judging the suitability of each speaker
	 * @return An object containing suggest speakers for an event, and the quality with which these suggests come
	 * @throws ExecutionException If there was an exception in an internal contributional implementation
	 * @throws InterruptedException If there was an exception in an internal contributional implementation
	 * @throws UnknownException 
	 */
	public SuggestedSpeakers suggestSpeakers(Event event, int softMin, int softMax, Allowance [] budget) throws InterruptedException, ExecutionException, UnknownException{
		
		if (budget.length != 3){
			throw new IllegalArgumentException("Illegal budget format for speaker suggestion");
		}
		if (!Time.class.isInstance(budget[0]) || !Time.class.isInstance(budget[1]) || !Time.class.isInstance(budget[2])){
			throw new IllegalArgumentException("Illegal budget format for speaker suggestion");
		}
		
		/*
		 * Get keywords
		 */
		KeywordFinder keywordFinder = new KeywordFinder();
		List<String> keywords = keywordFinder.getKeywords(event);
		
		System.out.printf("Extracted keywords %s\n\n", keywords.toString());
		
		/*
		 * Get speakers by searching for keywords
		 */
		//use the maximum multiplied by 5 so that we can get enough speakers to discard the bad ones
		Collection<Speaker> candidateSpeakers = speakerRetriever.getResponse(new SpeakersQuery(keywords, softMax * 5, softMax * 5));

		System.out.printf("Candidate speakers %s\n\n", candidateSpeakers.toString());
		
		/*
		 * Rank the speakers by suitability
		 */
		System.out.printf("Using co-occurrence rank aggregation\n\n");
		speakerJudge.evaluate(event, candidateSpeakers, new Allowance [] {budget[2]});
		SuggestedSpeakers suggestion = speakerJudge.getSuggestion();
		
		return suggestion;
	}
	
}
