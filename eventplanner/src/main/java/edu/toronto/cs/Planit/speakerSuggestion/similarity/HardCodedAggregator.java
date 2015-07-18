package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

import edu.toronto.cs.Planit.speakerSuggestion.similarity.Similarity.nominal;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * This aggregator uses a simple voting aggregation.
 *
 */
public class HardCodedAggregator implements Aggregator<Similarity, Trust, Quality> {

	/**
	 * Does voting based aggregation, each source has one vote.
	 */
	@Override
	public Optional<Result<Similarity, Quality>> aggregate(
			List<Opinion<Similarity, Trust>> opinions) {

		Map<nominal, Double> tally = new HashMap<Similarity.nominal, Double>();
		
		for (Opinion<Similarity, Trust> opinion : opinions){
			
			if (!tally.containsKey(opinion.getValue().getNominal())){
				tally.put(opinion.getValue().getNominal(), 0d);
			}
			
			tally.put(
					opinion.getValue().getNominal(),
					tally.get(opinion.getValue().getNominal()) + 1);
		}
		
		double maxVote = 0d;
		nominal bestOpinion = nominal.LOW;
		for (nominal opinion : tally.keySet()){
			if (tally.get(opinion) > maxVote){
				maxVote = tally.get(opinion);
				bestOpinion = opinion;
			}
		}
		
		Similarity winner = new Similarity(bestOpinion);
		
		return Optional.of(new Result<Similarity, Quality>(winner, null));
	}

}
