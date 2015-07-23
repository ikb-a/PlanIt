package edu.toronto.cs.Planit.speakersuggestion.similarity.util;

import java.util.List;

/**
 * An interface which should be implemented by all objects which will be involved in a word similarity comparison.
 * @author wginsberg
 *
 */
public interface Comparable {
	
	/**
	 * Returns a list of all known words which describe this object.
	 */
	public List<String> getWords();
	
	/**
	 * Return a list of n words which describe this object.
	 */
	public List<String> getWords(int n);
	
}
