package Planit.speakersuggestion.similarity.sources;

import java.io.IOException;
import java.util.List;

import Planit.speakersuggestion.similarity.ci.SimilarityContractDouble;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.Word2Vec;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * A source which returns the maximum pairwise similarity of two distinct words from two Comparables.
 * @author wginsberg
 *
 */
public class Word2VecMaxSimilarity extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {

	private int maxMatrixSize = -1;
	
	/**
	 * Create a new source for word similarity where no more than maxMatrixSize words from each comparable will be used.
	 * @param maxMatrixSize The maximum number of words to use from each Comparable
	 */
	public Word2VecMaxSimilarity(int maxMatrixSize){
		this.maxMatrixSize = maxMatrixSize;
	}
	
	/**
	 * Create a new source for word similarity where all available words will be used.
	 */
	public Word2VecMaxSimilarity() {
		this (-1);
	}
	
	@Override
	public String getName(){
		return "Word2Vec-Max-Word-Similarity";
	}
	
	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		int numWords1;
		int numWords2;
		if (maxMatrixSize != -1){
			numWords1 = args.getEvent().getWords(maxMatrixSize).size();
			numWords2 = args.getSpeaker().getWords(maxMatrixSize).size();
		}
		else{
			numWords1 = args.getEvent().getWords().size();
			numWords2 = args.getSpeaker().getWords().size();			
		}
		return new Expenditure [] {Word2Vec.getInstance().getComputationTimeNeeded(numWords1 * numWords2)};
	}

	@Override
	public Opinion<Double, Void> getOpinion(
			ComparisonRequest args) throws UnknownException {

		double [][] matrix;
		List<String> words1 = args.getEvent().getWords(maxMatrixSize);
		List<String> words2 = args.getSpeaker().getWords(maxMatrixSize);
		try {
			matrix = Word2Vec.getInstance().similarity(words1, words2);
		} catch (IOException e) {
			throw new UnknownException(e);
		}
		
		Double response = max(words1, words2, matrix);
		if (response == null){
			return new Opinion<Double, Void>(null, null);					
		}
		return new Opinion<Double, Void>(response, getTrust(args, Optional.of(response)));
	}

	/**
	 * Returns the maximum pairwise similarity of two distinct words
	 */
	private Double max(List<String> words1, List<String> words2, double [][] matrix){
		
		if (matrix == null){
			return null;
		}
		
		Double maxValue = null;
		for (int i = 0; i < matrix.length; i++){
			if (matrix[i] == null){
				continue;
			}
			for (int j = 0; j < matrix[i].length; j++){
				//find the max value
				if (maxValue == null || matrix[i][j] > maxValue) {
					//check that the words are distinct
					if (words1.get(i).equals(words2.get(j))){
						continue;
					}
					maxValue = matrix[i][j];
					}
			}
		}
		
		return maxValue;
	}
	
	@Override
	public Void getTrust(ComparisonRequest args,
			Optional<Double> value) {
		return null;
	}


}
