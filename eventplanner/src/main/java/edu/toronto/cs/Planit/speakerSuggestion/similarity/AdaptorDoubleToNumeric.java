package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import edu.toronto.cs.Planit.ci.ml.AttributePercentileTrust;
import edu.toronto.cs.Planit.ci.ml.NumericSourceAdaptor;
import edu.toronto.cs.se.ci.Source;

/**
 * This adaptor is a simple implementation of NumericSourceAdaptor
 * @author wginsberg
 *
 * @param <T>
 */
public class AdaptorDoubleToNumeric <T> extends NumericSourceAdaptor<ComparisonRequest, AttributePercentileTrust>
		implements NumericSimilarityContract {

	public AdaptorDoubleToNumeric(
			Source<ComparisonRequest, Double, AttributePercentileTrust> around) {
		super(around);
	}

}
