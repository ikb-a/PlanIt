package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A basic implementation of SimilarityComparison.
 * @author wginsberg
 *
 */
public abstract class ComparableImp implements Comparable {

	transient private List<String> defaultRemovedWords = Arrays.asList(new String [] {"and", "with", "the"});
	
	/**
	 * Given a raw piece of text, outputs a list of all words or tokens from the text.
	 * All characters are converted to lowercase, and punctuation is removed (excepting hyphen and apostrophe)
	 * The returned tokens are split by space characters.
	 * @param text
	 * @return
	 */
	public List<String> parsetext(String text){
		return Arrays.asList(
				text.toLowerCase().
				replaceAll("[\\W&&[^- ']]", "").
				split(" "));
	}
	
	/**
	 * Removes all words from wordList which appear in removeList.
	 * @return A copy of wordList with words from removeList filtered out.
	 */
	protected List<String> removeWords(List<String> wordList, List<String> removeList){
		
		if (removeList == null || removeList.isEmpty()){
			return wordList;
		}
		
		return wordList
				.stream()
				.filter(token -> !removeList.contains(token))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of words which should always be removed by default.
	 * @return
	 */
	public List<String> getDefaultRemovedWords() {
		return defaultRemovedWords;
	}
}
