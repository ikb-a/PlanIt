package PlanIt.monthSuggestion.sources;

import PlanIt.monthSuggestion.resources.OpenEvalMonthController;
import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.machineLearning.MLBasicSource;

/**
 * This source is implicitly 12 sources. Given a month at construction and an
 * Event to classify, it uses a SimpleOpenEval to determine whether or not more
 * than a specific number of keywords are related to the month during the
 * venue's country.
 * 
 * @author ikba
 *
 */
public class openEvalThresholdSource extends MLBasicSource<Event, Month> implements MLMonthSuggestionContract {
	/**
	 * The month that this source is checking for relatedness to.
	 */
	Month month;
	/**
	 * Month controller does all of the processing.
	 */
	OpenEvalMonthController control;
	/**
	 * Enable/disable messages
	 */
	boolean verbose = true;

	/**
	 * Setup this source to determine relatedness with {@code thisMonth}. It
	 * will consider the event related if the openeval finds that at least
	 * {@code threshold} keywords are positively classified by the openeval.
	 * 
	 * @param thisMonth
	 *            The month with which relatedness is being checked
	 * @param threshold
	 *            The minimum needed number of keywords that should be
	 *            positively classified to consider the event as being related.
	 */
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

	/**
	 * Setup this source to determine relatedness with {@code thisMonth}. It
	 * will consider the event related if the number of keywords that are
	 * positively classified by the openeval surpasses a default threshold in
	 * {@link OpenEvalMonthController}
	 * 
	 * @param thisMonth
	 *            The month with which relatedness is being checked
	 */
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
		return control.getCost(args, month);
	}

	/**
	 * Note that name changes based on month, so that for each month this source
	 * has a unique name, allowing said unique name to be a valid unique
	 * Attribute name for WEKA
	 */
	@Override
	public String getName() {
		return control.getName(month);
	}

}
