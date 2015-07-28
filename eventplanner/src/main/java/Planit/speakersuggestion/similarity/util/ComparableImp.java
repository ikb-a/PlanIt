package Planit.speakersuggestion.similarity.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A basic implementation of SimilarityComparison.
 * @author wginsberg
 *
 */
public abstract class ComparableImp implements Comparable {

	static final String stopWordsFileLocation = "src/main/resources/text/stopwords.txt";
	transient private static Collection<String> defaultRemovedWords;
	
	static final int minimumWordLength = 3;
	
	/**
	 * Returns as many words as possible in the same order as getWords()
	 * @param n The number of words to return, or -1 to return all available words
	 */
	@Override
	public List<String> getWords(int n) {
		if (n == 0){
			return new ArrayList<String>(0);
		}
		List<String> allWords = getWords();
		if (n != -1 && allWords.size() > n){
			return allWords.subList(0, n);
		}
		else{
			return allWords;
		}
	}

	
	/**
	 * Given a raw piece of text, outputs a list of all words or tokens from the text.
	 * All characters are converted to lowercase, and punctuation is removed (excepting hyphen and apostrophe)
	 * The returned tokens are split by space characters.
	 * @param text
	 * @return
	 */
	public static List<String> parsetext(String text){
		if (text == null){
			return new ArrayList<String>(0);
		}
		String [] parsed = text.toLowerCase().replaceAll("[^0-9A-z-' ]+?", " ").split("\\s");
		//not using Arrays.asList() because we need it to be modifyable
		List<String> words = new ArrayList<String>(parsed.length);
		for (int i = 0; i < parsed.length; i++){
			if (parsed[i] != null && parsed[i].length() >= minimumWordLength){
				words.add(parsed[i]);
			}
		}
		return words;
	}

	/**
	 * Returns all of the words in the text which do not appear in the default list of stop words.
	 * @param text
	 * @return
	 */
	static public List<String> extractKeywords(String text){
		List<String> keywords = parsetext(text);
		keywords.removeAll(getDefaultRemovedWords());
		return keywords;
	}
	
	/**
	 * Returns a list of words which should always be removed by default.
	 * @return
	 */
	static public Collection<String> getDefaultRemovedWords() {
		if (defaultRemovedWords == null){
			File stopWordsFile = new File(stopWordsFileLocation);
			assert stopWordsFile.exists();
			assert stopWordsFile.canRead();
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(stopWordsFile));
				defaultRemovedWords = reader.lines().collect(Collectors.toList());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				defaultRemovedWords = new ArrayList<String>();
			}
		}
		return defaultRemovedWords;
	}
}
