package edu.toronto.cs.Planit.speakersuggestion.similarity.ci;

import edu.toronto.cs.Planit.speakersuggestion.similarity.util.ComparisonRequest;
import edu.toronto.cs.se.ci.Contract;

/**
 * A contract for comparing two Comparable objects with the measure of comparison being a Double.
 * @author wginsberg
 *
 */
public interface SimilarityContractDouble extends Contract<ComparisonRequest, Double, Void> {

}
