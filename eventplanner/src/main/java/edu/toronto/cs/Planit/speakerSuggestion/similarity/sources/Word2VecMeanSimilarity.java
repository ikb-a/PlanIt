package edu.toronto.cs.Planit.speakerSuggestion.similarity.sources;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.Planit.speakerSuggestion.similarity.ComparisonRequest;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.Similarity;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.Similarity.nominal;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.SimilarityContract;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.Trust;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.Word2VecSimilarity;
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
public class Word2VecMeanSimilarity extends Source<ComparisonRequest, Similarity, Trust> implements SimilarityContract {

	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		int words1Size = Math.min(100, args.getFirst().getWords().size());
		int words2Size = Math.min(100, args.getSecond().getWords().size());
		return new Expenditure [] {Word2VecSimilarity.getComputationTimeNeeded(words1Size * words2Size)};
	}

	@Override
	public Opinion<Similarity, Trust> getOpinion(ComparisonRequest args)
			throws UnknownException {
		
		List<String> words1 = args.getFirst().getWords(100);
		List<String> words2 = args.getSecond().getWords(100);
		
		try {
			double [][] similarityMatrix = Word2VecSimilarity.getInstance().similarity(words1, words2);
			double avgSim = matrixAverage(similarityMatrix);
			Similarity similarity;
			if (avgSim < 0.0001d){
				similarity = new Similarity(avgSim, nominal.LOW);
			}
			else if (avgSim < 0.002d){
				similarity = new Similarity(avgSim, nominal.MEDIUM);
			}
			else{
				similarity = new Similarity(avgSim, nominal.HIGH);
			}
			Opinion<Similarity, Trust> opinion = new Opinion<Similarity, Trust>(similarity , new Trust());
			return opinion;
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
	public Trust getTrust(ComparisonRequest args, Optional<Similarity> value) {
		return new Trust();
	}


}
