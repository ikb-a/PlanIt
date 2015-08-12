package Planit.speakersuggestion.scrapespeakers.sources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.scrapespeakers.util.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Searches for speakers from the National Speakers Bureau using keywords.
 * @author wginsberg
 *
 */
public class NSBspeakers extends Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements GetSpeakersContract {

	/**
	 * Searches for the supplied keywords and returns at most the desired number of speakers.
	 */
	@Override
	public Opinion<Collection<Speaker>, SpeakerSetTrust> getOpinion(SpeakersQuery input) throws UnknownException {
		
		List<Speaker> speakers = new LinkedList<Speaker>();
		
		for (String keyword : input.getKeywords()){
			List<Speaker> results = NSBsearchByGoogle.getSpeakers(keyword, input.maxPerKeyword());
			if (results.size() > input.maxPerKeyword()){
				results.subList(input.maxPerKeyword(), results.size()).clear();
			}
			speakers.addAll(results);
		}
		if (speakers.size() > input.getMaxSpeakers()){
			speakers.subList(input.getMaxSpeakers(), speakers.size()).clear();
		}

		if (speakers.size() < 1){
			throw new UnknownException();
		}
	
		return new Opinion<Collection<Speaker>, SpeakerSetTrust>(input, speakers, null, this);
		
	}

	@Override
	public Expenditure[] getCost(SpeakersQuery query) throws Exception {
		return new Expenditure [] {};
	}

	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args, Optional<Collection<Speaker>> value) {
		return null;
	}

}
