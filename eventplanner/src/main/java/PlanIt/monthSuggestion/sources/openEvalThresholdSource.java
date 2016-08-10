package PlanIt.monthSuggestion.sources;

import PlanIt.monthSuggestion.resources.OpenEvalMonthController;
import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.machineLearning.MLBasicSource;

public class openEvalThresholdSource extends MLBasicSource<Event, Month> implements MLMonthSuggestionContract {
	Month month;
	OpenEvalMonthController control;
	boolean verbose = true;

	public openEvalThresholdSource(Month thisMonth, int threshold) {
		if (thisMonth == null) {
			throw new IllegalArgumentException("monthName is null");
		} else if (threshold < 0) {
			throw new IllegalArgumentException("threshold must be number of keywords >= 0");
		}
		this.month = thisMonth;
		control = OpenEvalMonthController.getInstance();
		control.setCustomThreshold(thisMonth, threshold);
	}

	public openEvalThresholdSource(Month thisMonth) {
		if (thisMonth == null) {
			throw new IllegalArgumentException("monthName is null");
		}
		this.month = thisMonth;
		control = OpenEvalMonthController.getInstance();
	}

	@Override
	public Month getResponse(Event input) throws UnknownException {
		return control.getResponse(input, month);
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		// int numOfKeywords = args.getWords().size();
		// TODO: Add method for integer multiplication of expenditures
		return control.getCost(args, month);
	}

	@Override
	public String getName() {
		return control.getName(month);
	}

}
