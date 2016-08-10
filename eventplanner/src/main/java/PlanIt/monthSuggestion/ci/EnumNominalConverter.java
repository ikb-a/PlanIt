package PlanIt.monthSuggestion.ci;

import PlanIt.monthSuggestion.sources.Month;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaNominalConverter;

/**
 * This class converts from a {@link PlanIt.monthSuggestion.sources.Month} to a
 * nominal String value (i.e. the name of the month)
 * 
 * @author Ian Berlot-Attwell
 *
 */
public class EnumNominalConverter implements MLWekaNominalConverter<Month> {

	/**
	 * Converts from a {@link PlanIt.monthSuggestion.sources.Month} to a nominal
	 * String value (i.e. the name of the month)
	 */
	@Override
	public String convert(Opinion<Month, Void> sourceOutput) {
		return sourceOutput.getValue().toString();
	}

}
