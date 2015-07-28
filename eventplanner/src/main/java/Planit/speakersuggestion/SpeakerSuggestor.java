package Planit.speakersuggestion;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.scrapespeakers.SpeakerRetriever;
import Planit.speakersuggestion.similarity.SuitabilityJudge;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class SpeakerSuggestor {
	
	/*
	 * Singleton object
	 */
	static private SpeakerSuggestor instance;
	private SpeakerSuggestor() throws Exception{
		speakerRetriever = SpeakerRetriever.getInstance();
		speakerJudge = SuitabilityJudge.getInstance();
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
	 * @param budget Two Time values. The first for how long to spend grabbing speakers from the internet, the second for how long to spend judging the suitability of each speaker
	 * @return An object containing suggest speakers for an event, and the quality with which these suggests come
	 * @throws ExecutionException If there was an exception in an internal contributional implementation
	 * @throws InterruptedException If there was an exception in an internal contributional implementation
	 */
	public SuggestedSpeakers suggestSpeakers(Event event, int softMin, int softMax, Allowance [] budget) throws InterruptedException, ExecutionException{
		
		if (budget.length != 2){
			throw new IllegalArgumentException("Illegal budget format for speaker suggestion");
		}
		if (!Time.class.isInstance(budget[0]) || !Time.class.isInstance(budget[1])){
			throw new IllegalArgumentException("Illegal budget format for speaker suggestion");
		}
		
		//use the maximum multiplied by 5 so that we can get enough speakers to discard the bad ones
		Collection<Speaker> candidateSpeakers = speakerRetriever.getSpeakers(softMax * 5, softMax * 5, new Allowance [] {budget[0]});
		speakerJudge.evaluate(event, candidateSpeakers, new Allowance [] {budget[1]});
		return speakerJudge.getSuggestion();
	}
	
}
