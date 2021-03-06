package Planit.speakersuggestion.similarity.util;

import Planit.ci.ml.WekaSourceAdaptor;
import edu.toronto.cs.se.ci.Source;

public class SourceAdaptor extends WekaSourceAdaptor<ComparisonRequest, Void> implements
		SimilarityContractWekaCompatible {

	String asString;
	
	public SourceAdaptor(Source<ComparisonRequest, Double, Void> around) {
		super(around);
		asString = around.getName();
	}

	@Override
	public String toString(){
		return asString;
	}
	
}
