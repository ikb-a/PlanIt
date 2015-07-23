package edu.toronto.cs.Planit.ci.ml;

/**
 * A type of quality capturing the probability distribution from a classifier.
 * @author wginsberg
 */
public class ClassDistributionQuality {

	private double [] distribution;

	/**
	 * Create a new quality object with the given class distribution.
	 * @param distribution
	 */
	public ClassDistributionQuality(double[] distribution) {
		super();
		this.distribution = distribution;
	}

	public double[] getDistribution() {
		return distribution;
	}
}
