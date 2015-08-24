package Planit.speakersuggestion.scrapespeakers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Optional;

import Planit.dataObjects.Speaker;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;
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
	
	KeynoteSpeakersCanada keynoteSource;
	LavinAgencySpeakers lavinSource;
	NSBspeakers nsbSource;
	PremiereSpeakersBureau premiereSource;

	SpeakerSetUnionAggregator aggregator;
	
	public SpeakerRetriever(){
		nsbSource = new NSBspeakers();
		premiereSource = new PremiereSpeakersBureau();
		keynoteSource = new KeynoteSpeakersCanada();
		lavinSource = new LavinAgencySpeakers();
		
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

		System.err.println("Using only NSB.com for speakers");
		
		try{
			opinions.add(nsbSource.getOpinion(query));
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
