package edu.toronto.cs.Planit.speakerSuggestion.similarity.sources;

import java.io.IOException;
import java.util.List;

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
 * Uses Word2Vec to find pairwise word similarity.
 * The returned opinion is the average of all pairwise similarities.
 * This source caps at 100 words for each Comparable
 * @author wginsberg
 *
 */
public class Word2VecMeanSimilarity extends Source<ComparisonRequest, Double, AttributePercentileTrust> implements SimilarityContract {

	private int maxMatrixSize = -1;
	
	/**
	 * Create a new source for word similarity where no more than maxMatrixSize words from each comparable will be used.
	 * @param maxMatrixSize The maximum number of words to use from each Comparable
	 */
	public Word2VecMeanSimilarity(int maxMatrixSize){
		this.maxMatrixSize = maxMatrixSize;
	}
	
	/**
	 * Create a new source for word similarity where all available words will be used.
	 * @param maxMatrixSize
	 */
	public Word2VecMeanSimilarity() {
		this (-1);
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
	public Opinion<Double, AttributePercentileTrust> getOpinion(ComparisonRequest args)
			throws UnknownException {
		
		List<String> words1 = args.getEvent().getWords(100);
		List<String> words2 = args.getSpeaker().getWords(100);
		
		try {
			double [][] similarityMatrix = Word2Vec.getInstance().similarity(words1, words2);
			double avgSim = matrixAverage(similarityMatrix);
			return new Opinion<Double, AttributePercentileTrust>(avgSim, new AttributePercentileTrust(null));
		} catch (IOException | IllegalArgumentException e) {
			throw new UnknownException(e);
		}

	}

	/**
	 * Returns the average of all values in the matrix. This ignores all "unknown" similarities which have the value -1
	 * @throws IllegalArgumentException if there is a null row in the matrix, or if the matrix itself is null
	 */
	public static double matrixAverage(double [][] matrix) throws IllegalArgumentException{
		if (matrix == null){
			throw new IllegalArgumentException();
		}
		int numValues = 0;
		double total = 0;
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				try{
					if (matrix[i][j] == -1d) continue;
					total += matrix[i][j];
					numValues++;
				}
				catch (IndexOutOfBoundsException | NullPointerException e){
					throw new IllegalArgumentException(e);
				}
			}
		}
		if (numValues == 0){
			return 0;
		}
		return total / numValues;
	}
	
	@Override
	public AttributePercentileTrust getTrust(ComparisonRequest args, Optional<Double> value) {
		return new AttributePercentileTrust(null);
	}


}
