package Planit.speakersuggestion.similarity.util;

import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * Returns the sum of the values of the opinions of the sources.
 * @author wginsberg
 *
 */
public class SummingAggregator implements Aggregator<Double, Void, Void> {

	@Override
	public Optional<Result<Double, Void>> aggregate(
			List<Opinion<Double, Void>> opinions) {
		
		double total = 0d;
		for (Opinion<Double, Void> opinion : opinions){
			total += opinion.getValue();
		}
		
		return Optional.of(new Result<Double, Void>(total, null));
	}

}
