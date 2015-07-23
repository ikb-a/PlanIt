package edu.toronto.cs.Planit.ci.ml;

import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Instance;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * An interface to implement for Small Valued CIs which use a classifier from Weka as the aggregation function
 * @author wginsberg
 */
public interface WekaClassifierAggregator <I> extends Aggregator<WekaCompatibleResponse<I>, Void, ClassDistributionQuality> {

	/**
	 * Returns the classifier that will be used to aggregate opinions.
	 * @return
	 */
	Classifier getClassifier();

	/**
	 * Transform the opinions of the sources into an instance which can be classified.
	 * Any opinions which do not have a corresponding attribute in the classifier are discarded, and
	 * any attributes in the classifier which do not have a corresponding opinion are set as missing.
	 * @param opinions Opinions from sources in a contributional implementation
	 * @return A weka instance which is compatible with the classifier used by this aggregator.
	 */
	public Instance createInstance(List<Opinion<WekaCompatibleResponse<I>, Void>> opinions);
	
}
