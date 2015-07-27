package edu.toronto.cs.Planit.speakersuggestion.similarity.sources;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import edu.toronto.cs.Planit.speakersuggestion.similarity.ci.SimilarityContractDouble;
import edu.toronto.cs.Planit.speakersuggestion.similarity.util.ComparisonRequest;
import edu.toronto.cs.Planit.speakersuggestion.similarity.util.Wordnet;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * For the most frequently occurring word in the event and the most frequently occurring in the speaker,
 * Does a query to wordnet to find all of the definitions of each word. The returned value is the percentage
 * of overlap IOU of the words in the two sets of definitions.
 * @author wginsberg
 *
 */
public class WordNetDefinitionOverlap extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {
	
	@Override
	public String getName(){
		return "Wordnet-definitions-overlap";
	}
	
	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {

		Wordnet wordnet = Wordnet.getInstance();
		
		try{
			
			String first = Word2VecSimilarityOfMostFrequent.extractTopN(
					Word2VecSimilarityOfMostFrequent.wordFrequency(args.getEvent().getWords()), 1).get(0);
			String second = Word2VecSimilarityOfMostFrequent.extractTopN(
					Word2VecSimilarityOfMostFrequent.wordFrequency(args.getSpeaker().getWords()), 1).get(0);
			
			List<String> defs1 = wordnet.getDefinitions(wordnet.getPage(first));
			List<String> defs2 = wordnet.getDefinitions(wordnet.getPage(second));
			
			Set<String> defWords1 = new HashSet<String>();
			for (String def : defs1){
				defWords1.addAll(Arrays.asList(def.split(" ")));
			}
			Set<String> defWords2 = new HashSet<String>();
			for (String def : defs2){
				defWords2.addAll(Arrays.asList(def.split(" ")));
			}
			
			Double iou = IOU(defWords1, defWords2);
			if (iou == null){
				return new Opinion<Double, Void>(null, null);
			}
			else{
				return new Opinion<Double, Void>(iou, getTrust(args, Optional.of(iou)));
			}
		}
		catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e){
			throw new UnknownException(e);
		}
		
	}

	/**
	 * Computes and returns the Intersection Over Union of the two word sets.
	 * @param words1 A set of words
	 * @param words2 A set of words
	 * @return
	 */
	public static Double IOU(Set<String> words1, Set<String> words2){
		
		int union = Sets.union(words1, words2).size();
		if (union == 0){
			return 0d;
		}
		int intersection = Sets.intersection(words1, words2).size();
		return (double) intersection / union;
	}
	
	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		return new Expenditure [] {Wordnet.getTime()};
	}

	@Override
	public Void getTrust(ComparisonRequest args, Optional<Double> value) {
		return null;
	}

}
