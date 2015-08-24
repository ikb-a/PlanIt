package Planit.speakersuggestion.comparison.sources;

import edu.toronto.cs.se.ci.Source;
import Planit.ci.ml.WekaSourceAdaptor;
import Planit.speakersuggestion.comparison.SpeakerComparisonContractWekaCompatible;
import Planit.speakersuggestion.comparison.SpeakerComparisonRequest;

public class SourceAdaptor extends WekaSourceAdaptor<SpeakerComparisonRequest, Void> implements
		SpeakerComparisonContractWekaCompatible {

	public SourceAdaptor(Source<SpeakerComparisonRequest, Double, Void> around) {
		super(around);
	}

}
