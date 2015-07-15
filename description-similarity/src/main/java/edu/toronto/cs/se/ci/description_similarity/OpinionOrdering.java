package edu.toronto.cs.se.ci.description_similarity;

import java.util.Comparator;

import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Orders opinions according to the name of the sources discovered in their SimilarityTrust
 * @author wginsberg
 *
 */
public class OpinionOrdering implements Comparator<Opinion<Double, SimilarityTrust>> {

	private OpinionOrdering(){
		super();
	}
	
	static private Comparator<Opinion<Double, SimilarityTrust>> instance = null;
	
	public static Comparator<Opinion<Double, SimilarityTrust>> getComparator(){
		if (instance == null){
			instance = new OpinionOrdering();
		}
		return instance;
	}
	
	/**
	 * Compares two opinions based on the toString() method of the SimilarityTrust objects inside
	 */
	@Override
	public int compare(Opinion<Double, SimilarityTrust> o1,
			Opinion<Double, SimilarityTrust> o2) {
		int comparison = o1.getTrust().getValue().toString().compareTo(o2.getTrust().getValue().toString());
		return comparison;
	}

}
