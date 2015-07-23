package edu.toronto.cs.Planit.speakersuggestion.similarity.ci;

import edu.toronto.cs.Planit.ci.ml.WekaSourceAdaptor;
import edu.toronto.cs.Planit.speakersuggestion.similarity.util.ComparisonRequest;
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
