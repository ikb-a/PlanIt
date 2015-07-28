package Planit.speakersuggestion.similarity.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import Planit.speakersuggestion.similarity.ci.SimilarityContractDouble;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.Word2Vec;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * A class for finding the similarity of the most frequently occuring words from two comparables.
 * The number of words compared from each object can be set, such that the 5 most frequent, or 10 most frequent, etc. can be compared.
 * The final returned value is the average pairwise similarity of all pairs (frequent event word, frequent speaker word).
 * @author wginsberg
 *
 */
public class Word2VecSimilarityOfMostFrequent extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {
	
	static final int defaultN = 5;
	int n;
	
	/**
	 * @param n The top n words will be compared
	 */
	public Word2VecSimilarityOfMostFrequent(int n) {
		super();
		this.n = n;
	}

	/**
	 * @param The default number of most frequent words will be compared
	 */
	public Word2VecSimilarityOfMostFrequent(){
		this(defaultN);
	}
	
	@Override
	public String getName(){
		return "Word2Vec-Similarity-Of-Most-Frequent-(n=" + String.valueOf(n) + ")";
	}
	
	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		return new Expenditure [] {Word2Vec.getInstance().getComputationTimeNeeded(n*n)};
	}

	@Override
	public Void getTrust(ComparisonRequest args,
			Optional<Double> value) {
		return null;
	}
	
	@Override
	public Opinion<Double, Void> getOpinion(
			ComparisonRequest args) throws UnknownException {
		
		List<String> mostCommon1;
		List<String> mostCommon2;
		int numToCompare1;
		int numToCompare2;
		List<String> toCompare1;
		List<String> toCompare2;
		
		mostCommon1 = extractTopN(wordFrequency(args.getEvent().getWords()), n);
		numToCompare1 = Math.min(defaultN, mostCommon1.size());
		toCompare1 = mostCommon1.subList(0, numToCompare1);
		
		mostCommon2 = extractTopN(wordFrequency(args.getSpeaker().getWords()), n);
		numToCompare2 = Math.min(defaultN, mostCommon2.size());
		toCompare2 = mostCommon2.subList(0, numToCompare2);

		try {
			double [][] sim = Word2Vec.getInstance().similarity(toCompare1, toCompare2);
			double value = Word2VecMeanSimilarity.matrixAverage(sim);
			if (value == -1){
				throw new UnknownException();
			}
			return new Opinion<Double, Void>(value, getTrust(args, Optional.of(value)));
		} catch (IOException | IndexOutOfBoundsException | IllegalArgumentException e) {
			throw new UnknownException(e);
		}
	}

	/**
	 * Given a bag of words, return a map of words and how many times the appear.
	 * @param words
	 * @return
	 */
	public synchronized static Map<String, Integer> wordFrequency(List<String> words){
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		Iterator<String> wordIter = words.iterator();
		while (wordIter.hasNext()){
			String word = wordIter.next();
			if (!frequencies.containsKey(word)){
				frequencies.put(word, 0);
			}
			frequencies.put(word, frequencies.get(word) + 1);
		}
		return frequencies;
	}
	
	/**
	 * Returns up to n words from the map which have the highest integer value in the map.
	 * Ignores any words with frequency 1
	 */
	public static List<String> extractTopN(Map<String, Integer> words, int n){
		//sort in reverse order to make this more efficient
		Comparator<Entry<String, Integer>> compareEntry = (e1, e2) -> -1 * e1.getValue().compareTo(e2.getValue());
		List<Entry<String, Integer>> sortedEntries = words.entrySet().stream().sorted(compareEntry).collect(Collectors.toList());
		List<String> sortedWords = new ArrayList<String>(sortedEntries.size());
		for (int i = 0; i < Math.min(n, sortedEntries.size()); i++){
			if (sortedEntries.get(i).getValue() < 2){
				break;
			}
			sortedWords.add(sortedEntries.get(i).getKey());
		}
		return sortedWords;
	}
}
