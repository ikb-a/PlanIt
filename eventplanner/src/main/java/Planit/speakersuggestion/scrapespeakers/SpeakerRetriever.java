package Planit.speakersuggestion.scrapespeakers;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.scrapespeakers.ci.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakerSetUnionAggregator;
import Planit.speakersuggestion.scrapespeakers.ci.SpeakersQuery;
import Planit.speakersuggestion.scrapespeakers.sources.KeynoteSpeakersCanada;
import Planit.speakersuggestion.scrapespeakers.sources.NSBspeakers;
import Planit.speakersuggestion.scrapespeakers.sources.SpeakerIdeas;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

public class SpeakerRetriever {

	/*
	 * Singleton class
	 */
	private SpeakerRetriever(){}
	static private SpeakerRetriever instance;
	static public SpeakerRetriever getInstance(){
		if (instance == null){
			instance = new SpeakerRetriever();
		}
		return instance;
	}
	
	CI <SpeakersQuery, Collection<Speaker>, SpeakerSetTrust, Double> ci;
	
	/**
	 * Executes a contributional implementation to retrieve a set of speakers from the internet.
	 * @param keywords a list of keywords to search for
	 * @param min A soft minimum on the number of speakers to return.
	 * @param max A soft maximum on the number of speakers to return.
	 * @param budget The maximum time to spend getting speakers
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public Collection<Speaker> getSpeakers(List<String> keywords, int min, int max, Allowance [] budget) throws InterruptedException, ExecutionException{
		if (ci == null){
			ci = constructCI();
		}
		SpeakersQuery query = new SpeakersQuery(keywords, min, max);
		Result<Collection<Speaker>, Double> result = ci.apply(query, budget).get();
		return result.getValue();		
	}
	
	/**
	 * Constructs a new contributional implementation for retrieving speakers.
	 * @return
	 */
	private static CI<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust, Double> constructCI(){
		
		/*
		 * Register some sources if needed
		 */
		if (Contracts.discover(GetSpeakersContract.class).isEmpty()){
			Contracts.register(new KeynoteSpeakersCanada());
			Contracts.register(new NSBspeakers());
			Contracts.register(new SpeakerIdeas());
		}
		
		return new CI<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust, Double>
				(GetSpeakersContract.class,
				new SpeakerSetUnionAggregator(),
				new AllSelector<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust>());
	}
}
