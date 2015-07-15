package edu.toronto.cs.se.ci.description_similarity;

import java.util.HashMap;


/**
 * This trust object simply stores the source the answer came from
 * @author wginsberg
 *
 */
public class SimilarityTrust {

	public static SimilarityTrust VOID = null;
	
	private static HashMap<SimilarityContract, SimilarityTrust> trusts = null;
	
	/**
	 * Return a trust object for the given source. This may be cached in SimilarityTrust so that a new object does not need to be instantiated ever time.
	 * @param source
	 * @return
	 */
	public static SimilarityTrust of(SimilarityContract source){
		if (trusts == null){
			trusts = new HashMap<SimilarityContract, SimilarityTrust>();
		}
		if (!trusts.containsKey(source)){
			trusts.put(source, new SimilarityTrust(source));
		}
		return trusts.get(source);
	}
	
	private SimilarityContract value;
	
	public SimilarityTrust(SimilarityContract source){
		value = source;
	}
	
	public SimilarityContract getValue(){
		return value;
	}
}
