package PlanIt.monthSuggestion.ci;

import PlanIt.monthSuggestion.sources.Month;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaNominalConverter;

public class EnumNominalConverter implements MLWekaNominalConverter<Month>{

	@Override
	public String convert(Opinion<Month, Void> sourceOutput) {
		// TODO Auto-generated method stub
		return sourceOutput.getValue().toString();
	}

}
