package Planit.speakersuggestion.comparison;

import Planit.ci.ml.WekaCompatibleResponse;
import edu.toronto.cs.se.ci.Contract;

public interface SpeakerComparisonContractWekaCompatible extends
		Contract<SpeakerComparisonRequest, WekaCompatibleResponse<SpeakerComparisonRequest>, Void> {

}
