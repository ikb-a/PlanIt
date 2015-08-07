package Planit.speakersuggestion.similarity.util;

import Planit.ci.ml.WekaCompatibleResponse;
import edu.toronto.cs.se.ci.Contract;

/**
 * A contract for comparing two Comparable objects with the measure of comparison being captured in a WekaCompatibleResponse.
 * @author wginsberg
 *
 */
public interface SimilarityContractWekaCompatible extends Contract<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void> {

}
