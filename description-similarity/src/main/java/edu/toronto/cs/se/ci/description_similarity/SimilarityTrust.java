package edu.toronto.cs.se.ci.description_similarity;

import edu.toronto.cs.se.ci.description_similarity.sources.SimilaritySource;

/**
 * This trust object simply stores the source the answer came from
 * @author wginsberg
 *
 */
public class SimilarityTrust {

	public static SimilarityTrust VOID = null;
	
	private SimilaritySource value;
	
	public SimilarityTrust(SimilaritySource source){
		value = source;
	}
	
	public SimilaritySource getValue(){
		return value;
	}
}
