package edu.toronto.cs.PlanIt.speakerSuggesetion.comparisons;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A basic implementation of SimilarityComparison.
 * @author wginsberg
 *
 */
public abstract class SimilarityComparisonImp implements SimilarityComparison {

	static private List<String> filterWords;
	
	@Override
	abstract public List<String> getWords();

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
	 * Filters words which could have been in the source text but should be removed for a similarity comparison.
	 * This implementation removes "of", "the", "and", "with" and all tokens of less than 3 characters.
	 * @param words
	 * @return
	 */
	public List<String> filterWords(List<String> words){
		return words
				.stream()
				.filter(token -> token.length() > 2 && !getFilterWords().contains(token))
				.collect(Collectors.toList());
	}
	
	public static List<String> getFilterWords(){
		if (filterWords == null){
			filterWords = Arrays.asList(new String [] {
					"of",
					"and",
					"the"});
		}
		return filterWords;
	}
}
