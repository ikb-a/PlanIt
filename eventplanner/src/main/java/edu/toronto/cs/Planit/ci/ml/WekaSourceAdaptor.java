package edu.toronto.cs.Planit.ci.ml;

import com.google.common.base.Optional;

import edu.toronto.cs.Planit.ci.ml.WekaCompatibleResponse;
import edu.toronto.cs.se.ci.Adaptor;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * An adaptor which transforms (source : I -> Double) into (source : I -> WekaCompatibleResponse)
 * To implement this abstract class, simply create a new class which extends it and implements some contract.
 * @author wginsberg
 */
public abstract class WekaSourceAdaptor <I, T> extends Adaptor<I, WekaCompatibleResponse<I>, Void, I, Double, T> {

	public WekaSourceAdaptor(Source<I, Double, T> around) {
		super(around);
	}

	@Override
	public Expenditure[] getCost(I args, Source<I, Double, T> around)
			throws Exception {
		return around.getCost(args);
	}

	@Override
	public Opinion<WekaCompatibleResponse<I>, Void> getOpinion(I args,
			Source<I, Double, T> around) throws UnknownException {
		
		Opinion<Double, T> originalOpinion = around.getOpinion(args);
		WekaCompatibleResponse<I> newReponse =
				new WekaCompatibleResponse<I>(around, args, originalOpinion.getValue());
		return new Opinion<WekaCompatibleResponse<I>, Void>(newReponse, getTrust(args, Optional.of(newReponse), around));
	}

	@Override
	public Void getTrust(I args, Optional<WekaCompatibleResponse<I>> value,
			Source<I, Double, T> around) {
		return null;
	}

}
