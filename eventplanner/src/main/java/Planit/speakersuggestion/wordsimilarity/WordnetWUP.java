package Planit.speakersuggestion.wordsimilarity;

import java.util.List;

import edu.cmu.lti.ws4j.WS4J;

/**
 * A class wrapping the functionality of the WS4J library's method for performing word
 * similarity analysis using the Wu and Palmer algorithm.
 * 
 * @author wginsberg
 *
 */
public class WordnetWUP {
	
	/**
	 * Analyze the relatedness of two words
	 * @param word1
	 * @param word2
	 * @return
	 */
	static public double compare(String word1, String word2){
		Double similarity = WS4J.runWUP(word1, word2);
		
		if (similarity < 0d){
			return 0d;
		}
		if (similarity > 1d){
			return 1d;
		}
		return similarity;
	}
	
	/**
	 * Analyze the pair wise relatedness of two lists of words
	 * @param wordList1
	 * @param wordList2
	 * @return
	 */
	static public double [][] compare(List<String> wordList1, List<String> wordList2){
		if (wordList1 == null || wordList2 == null){
			return null;
		}
		double [][] matrix = new double[wordList1.size()][wordList2.size()];
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				matrix[i][j] = compare(wordList1.get(i), wordList2.get(j));
			}
		}
		return matrix;
	}
}
