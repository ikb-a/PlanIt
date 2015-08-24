package Planit.speakersuggestion.scrapespeakers.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;

import Planit.dataObjects.Speaker;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * Aggregrates collections of speakers by set union.
 * Output type : Collection<Speaker>
 * Trust type : SpeakerSetTrust
 * Quality type : Double
 * @author wginsberg
 */
public class SpeakerSetUnionAggregator implements Aggregator<Collection<Speaker>, SpeakerSetTrust, Double> {

	/**
	 * Does a set union on the given sets of speakers.
	 * When a speaker appears twice by name the two speaker objects are
	 * merged into one, where the list of webpages for the speaker is updated accordingly.
	 */
	@Override
	public Optional<Result<Collection<Speaker>, Double>> aggregate(
			List<Opinion<Collection<Speaker>, SpeakerSetTrust>> opinions) {
		
		Set<Speaker> speakers = new HashSet<Speaker>();
		Double quality;
		int numNonDistinct = 0;
		
		//for each source's set
		for (Opinion<Collection<Speaker>, SpeakerSetTrust> opinion : opinions){
			//for each speaker the source gave
			Iterator<Speaker> iter = opinion.getValue().iterator();
			while (iter.hasNext()){
				Speaker newSpeaker = iter.next();
				numNonDistinct++;
				//for each existing speaker already aggregated
				for (Speaker oldSpeaker : speakers){
					if (newSpeaker.equals(oldSpeaker)){
						//merge the two speakers
						oldSpeaker.addPages(newSpeaker.getPages());
						iter.remove();
					}
				}
			}
			//union the sets
			speakers.addAll(opinion.getValue());
		}
		
		//the ratio of intersection-ness
		quality = new Double(speakers.size()) / (numNonDistinct);
		return Optional.of(new Result<Collection<Speaker>, Double>(speakers, quality));
	}

}
