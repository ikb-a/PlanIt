package edu.toronto.cs.Planit.ci.ml;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Adaptor;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * An adaptor which transforms (source : I -> Double) into (source : I -> NumericResponse)
 * To implement this abstract class, simply create a new class which extends it and implements some contract.
 * @author wginsberg
 */
public abstract class NumericSourceAdaptor <I, T> extends Adaptor<I, NumericResponse<I, T>, T, I, Double, T> {

	public NumericSourceAdaptor(Source<I, Double, T> around) {
		super(around);
	}

	@Override
	public Expenditure[] getCost(I args, Source<I, Double, T> around)
			throws Exception {
		return around.getCost(args);
	}

	@Override
	public Opinion<NumericResponse<I, T>, T> getOpinion(I args,
			Source<I, Double, T> around) throws UnknownException {
		
		Opinion<Double, T> originalOpinion = around.getOpinion(args);
		NumericResponse<I, T> newReponse =
				new NumericResponse<I, T>(args, around, originalOpinion.getValue());
		return new Opinion<NumericResponse<I, T>, T>(newReponse, originalOpinion.getTrust());
	}

	@Override
	public T getTrust(I args, Optional<NumericResponse<I, T>> value,
			Source<I, Double, T> around) {
		if (value.isPresent()){
			return around.getTrust(args, Optional.of(value.get().get()));
		}
		else{
			return null;
		}
	}

}
