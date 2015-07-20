package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import edu.toronto.cs.Planit.ci.ml.AttributePercentileTrust;
import edu.toronto.cs.se.ci.Contract;

/**
 * A contract for comparing two Comparable objects with the measure of comparison being a Double.
 * @author wginsberg
 *
 */
public interface SimilarityContract extends Contract<ComparisonRequest, Double, AttributePercentileTrust> {

}
