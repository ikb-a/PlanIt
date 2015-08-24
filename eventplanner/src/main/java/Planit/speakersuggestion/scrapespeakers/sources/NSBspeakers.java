package Planit.speakersuggestion.scrapespeakers.sources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import Planit.speakersuggestion.scrapespeakers.resource.NSBsearchByGoogle;
import Planit.speakersuggestion.scrapespeakers.util.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;

import com.google.common.base.Optional;

import Planit.dataObjects.Speaker;
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
		
		speakers.addAll(NSBsearchByGoogle.getSpeakers(String.join("|", input.getKeywords()), input.getMaxSpeakers()));
	
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
