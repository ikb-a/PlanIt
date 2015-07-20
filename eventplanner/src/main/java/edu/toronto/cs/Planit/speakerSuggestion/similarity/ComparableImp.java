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

	transient private static List<String> defaultRemovedWords;
	
	/**
	 * Given a raw piece of text, outputs a list of all words or tokens from the text.
	 * All characters are converted to lowercase, and punctuation is removed (excepting hyphen and apostrophe)
	 * The returned tokens are split by space characters.
	 * @param text
	 * @return
	 */
	public static List<String> parsetext(String text){
		return Arrays.asList(
				text.toLowerCase().
				replaceAll("[\\W&&[^- ']]", "").
				split(" "));
	}

	/**
	 * Returns all of the words in the text which are at least 3 characters long and do not appear in the default list of prepositions.
	 * @param text
	 * @return
	 */
	static public List<String> extractKeywords(String text){
		return removeWords(parsetext(text), getDefaultRemovedWords(), 3);
	}
	
	/**
	 * Removes all words from wordList which appear in removeList.
	 * @return A copy of wordList with words from removeList filtered out.
	 */
	static public List<String> removeWords(List<String> wordList, List<String> removeList){
		return removeWords(wordList, removeList, 0);
	}

	/**
	 * Removes all words from wordList which appear in removeList, as well as any words under the minimum word length.
	 * @return A copy of wordList with words from removeList filtered out.
	 */
	static public List<String> removeWords(List<String> wordList, List<String> removeList, int minimumWordLength){
		if (removeList == null || removeList.isEmpty()){
			return wordList;
		}
		
		return wordList
				.stream()
				.filter(token -> token.length() >= minimumWordLength && !removeList.contains(token))
				.collect(Collectors.toList());		
	}
	
	/**
	 * Returns a list of words which should always be removed by default.
	 * @return
	 */
	static public List<String> getDefaultRemovedWords() {
		if (defaultRemovedWords == null){
			defaultRemovedWords = Arrays.asList(new String [] {"and", "with", "the"});
		}
		return defaultRemovedWords;
	}
}
