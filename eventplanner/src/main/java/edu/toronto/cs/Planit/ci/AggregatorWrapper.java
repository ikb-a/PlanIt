package edu.toronto.cs.Planit.ci;

import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * An aggregator which wraps around another aggregator.
 * In the aggregation function the arguments will be passed on to the other aggregator so that this one
 * may have a passive effect such as logging opinions.
 * @author wginsberg
 */
public abstract class AggregatorWrapper <O, T, Q> implements Aggregator<O, T, Q> {

	Aggregator<O, T, Q> around;
	
	/**
	 * @param around The return value of this aggregator will be used for the aggregate() method
	 */
	public AggregatorWrapper (Aggregator<O, T, Q> around){
		this.around = around;
	}
	
	/**
	 * Performs the passive aggregation and the aggregation which is wrapped around.
	 * If the wrapped aggregator is null then Optional.absent() is returned, otherwise the value of that aggregation.
	 */
	@Override
	public Optional<Result<O, Q>> aggregate(List<Opinion<O, T>> opinions){
		Optional<Result<O, Q>> result; 
		if (around == null){
			result = Optional.absent();
		}
		else{
			result = around.aggregate(opinions);
		}
		passiveAggregation(opinions, result);
		return result;
	}
	
	public abstract void passiveAggregation(List<Opinion<O, T>> opinions, Optional<Result<O, Q>> wrappedAggregationResult);
	
}
