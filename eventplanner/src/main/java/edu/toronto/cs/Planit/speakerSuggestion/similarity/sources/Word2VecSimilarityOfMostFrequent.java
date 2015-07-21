package edu.toronto.cs.Planit.speakerSuggestion.similarity.sources;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Optional;

import edu.toronto.cs.Planit.ci.ml.AttributePercentileTrust;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.ComparisonRequest;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.SimilarityContract;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.Word2Vec;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * A class for finding the similarity of the most frequently occuring words from two comparables.
 * @author wginsberg
 *
 */
public class Word2VecSimilarityOfMostFrequent extends Source<ComparisonRequest, Double, AttributePercentileTrust> implements
		SimilarityContract {

	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Opinion<Double, AttributePercentileTrust> getOpinion(
			ComparisonRequest args) throws UnknownException {
		
		String w1 = mostFreqentWord(wordFrequency(args.getEvent().getWords()));
		String w2 = mostFreqentWord(wordFrequency(args.getSpeaker().getWords()));
		
		try {
			double [][] sim = Word2Vec.getInstance().similarity(Arrays.asList(new String [] {w1}), Arrays.asList(new String [] {w2}));
			return new Opinion<Double, AttributePercentileTrust>(sim[0][0], getTrust(args, Optional.of(sim[0][0])));
		} catch (IOException | IndexOutOfBoundsException e) {
			throw new UnknownException(e);
		}
	}

	private Map<String, Integer> wordFrequency(List<String> words){
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		for (String word : words){
			if (!frequencies.containsKey(word)){
				frequencies.put(word, 0);
			}
			frequencies.put(word, frequencies.get(word) + 1);
		}
		return frequencies;
	}
	
	private String mostFreqentWord(Map<String, Integer> map){
		int highestFrequency = 0;
		String highestWord = "";
		for (Entry<String, Integer> entry : map.entrySet()){
			if (entry.getValue() > highestFrequency){
				highestFrequency = entry.getValue();
				highestWord = entry.getKey();
			}
		}
		return highestWord;
	}
	
	@Override
	public AttributePercentileTrust getTrust(ComparisonRequest args,
			Optional<Double> value) {
		return new AttributePercentileTrust(null);
	}

}
