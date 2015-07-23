package edu.toronto.cs.Planit.ci.ml;

/**
 * This represents the level to which an attribute is the best attribute in the classifier at the point of training/testing.
 * In an ordering of the attributes by how good they are at predicting the correct answer, this represents the sources ranking as a percentile
 * @author wginsberg
 *
 */
public class AttributePercentileTrust implements AttributeTrust {

	private Double percentile;
	
	public AttributePercentileTrust(Double percentile){
		this.percentile = percentile;
	}
	
	public Double getPercentile() {
		return percentile;
	}

	public void setPercentile(Double percentile) {
		this.percentile = percentile;
	}

}
