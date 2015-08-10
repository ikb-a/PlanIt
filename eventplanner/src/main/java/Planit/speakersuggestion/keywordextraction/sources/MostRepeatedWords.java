package Planit.speakersuggestion.keywordextraction.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import Planit.speakersuggestion.keywordextraction.util.WordListKeywordsContract;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Extracts keywords following the rule that the most repeated words are the keywords.
 * @author wginsberg
 *
 */
public class MostRepeatedWords extends Source<List<String>, List<String>, Void> 
	implements WordListKeywordsContract{

	private int n;
	
	/**
	 * Create a new source that will always try to return n keywords from a document.
	 */
	public MostRepeatedWords(int n) {
		super();
		this.n = n;
	}

	public MostRepeatedWords() {
		this(3);
	}

	/**
	 * Returns a map of how many times each string appears in a list.
	 * @param words A list of strings with repetition
	 * @return A map indicating the number of repetitions of each string, excluding strings with repetition less than 2
	 */
	public static Map<String, Integer> buildWordFrequencyMap(List<String> words){
		
		//record the frequency of each word
		Map<String, Integer> frequency = new HashMap<String, Integer>();
		for (String word : words){
			if (!frequency.containsKey(word)){
				frequency.put(word, 1);
			}
			else{
				frequency.put(word, frequency.get(word) + 1);
			}
		}
		
		//remove words which do not repeat
		Iterator<String> wordsInMap = frequency.keySet().iterator();
		while (wordsInMap.hasNext()){
			String key = wordsInMap.next();
			if (frequency.get(key) < 2){
				wordsInMap.remove();
			}
		}
		
		return frequency;
	}
	
	/**
	 * Returns the most frequently repeated strings from a list.
	 * @param words A list of words to extract from
	 * @param n The maximum number of keywords to return 
	 * @return A list of up to n strings from the supplied list
	 */
	public static List<String> extractKeywords(List<String> words, int n){
		
		//sort words by repetition
		Map<String, Integer> frequencies = buildWordFrequencyMap(words);
		List<Entry<String, Integer>> sorted = frequencies.entrySet().stream().sorted((e1, e2) -> -1 * e1.getValue().compareTo(e2.getValue())).collect(Collectors.toList());
		
		//extract the most repeated
		List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < Math.min(n, sorted.size()); i++){
			toReturn.add(sorted.get(i).getKey());
		}
		
		return toReturn;
	}

	@Override
	public Opinion<List<String>, Void> getOpinion(List<String> input)
			throws UnknownException {
		List<String> response = extractKeywords(input, n);
		if (response == null){
			return new Opinion<List<String>, Void> (input, response , getTrust(input, null), this);
		}
		return new Opinion<List<String>, Void> (input, response , getTrust(input, Optional.of(response)), this);
	}

	@Override
	public String getName(){
		return "most-repeated-words-as-keyword";
	}
	
	@Override
	public Void getTrust(List<String> args, Optional<List<String>> value) {
		return null;
	}

	@Override
	public Expenditure[] getCost(List<String> args) throws Exception {
		return new Expenditure [] {};
	}
	
}
