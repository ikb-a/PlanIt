package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import edu.toronto.cs.Planit.ci.ml.AttributePercentileTrust;
import edu.toronto.cs.Planit.ci.ml.NumericResponse;
import edu.toronto.cs.se.ci.Contract;

/**
 * A contract for comparing two Comparable objects with the measure of comparison being a NumericResponse.
 * @author wginsberg
 *
 */
public interface NumericSimilarityContract extends Contract<ComparisonRequest, NumericResponse<ComparisonRequest, AttributePercentileTrust>, AttributePercentileTrust> {

}
