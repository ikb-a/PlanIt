package edu.toronto.cs.PlanIt.speakerSuggesetion.comparisons;

import java.util.List;

/**
 * An interface which should be implemented by all objects which will be involved in a word similarity comparison.
 * @author wginsberg
 *
 */
public interface SimilarityComparison {

	public List<String> getWords();
	
}
