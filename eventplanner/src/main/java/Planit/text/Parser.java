package Planit.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

	static final String stopWordsFileLocation = "src/main/resources/text/stopwords.txt";
	static Collection<String> stopWords;
	
	/**
	 * Given a raw piece of text, outputs a list of all words or tokens from the text.
	 * All characters are converted to lowercase, and punctuation is removed (excepting hyphen and apostrophe)
	 * The returned tokens are split by whitespace.
	 * @param text
	 * @return
	 */
	public static List<String> parsetext(String text){
		if (text == null){
			return new ArrayList<String>(0);
		}
		String [] parsed = text.toLowerCase().replaceAll("[^0-9A-z-' ]+?", " ").split("\\s+");
		//not using Arrays.asList() because we need it to be modifyable
		List<String> words = new ArrayList<String>(parsed.length);
		for (int i = 0; i < parsed.length; i++){
			if (parsed[i] != null && parsed[i].length() > 1){
				words.add(parsed[i]);
			}
		}
		return words;
	}
	
	/**
	 * Removes all stop words from the supplied list of words.
	 * This method also removes all words containing less than three characters.
	 * @param words
	 */
	static public void removeStopWords(Collection<String> words){
		Collection<String> stopWords = getStopWords();
		words = words.stream().filter(word -> word != null && word.length() > 2 && !stopWords.contains(word)).collect(Collectors.toList());
	}
	
	/**
	 * Returns a list of words which should always be removed by default.
	 * @return
	 */
	static public Collection<String> getStopWords() {
		if (stopWords == null){
			File stopWordsFile = new File(stopWordsFileLocation);
			assert stopWordsFile.exists();
			assert stopWordsFile.canRead();
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(stopWordsFile));
				stopWords = reader.lines().collect(Collectors.toList());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				stopWords = new ArrayList<String>();
			}
		}
		return stopWords;
	}
}
