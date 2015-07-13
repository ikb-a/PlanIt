package edu.toronto.cs.se.ci.description_similarity.sources;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.description_similarity.SimilarityQuestion;

/**
 * The average similarity of correlation between each maximal pair (event keyword, speaker keyword)
 * @author wginsberg
 *
 */
public class MeanSimilarity extends SimilaritySource {

	@Override
	public Double getResponse(SimilarityQuestion input) throws UnknownException {
		
		double [][] similarities = similarityMatrix(input);
		double [] maxSimilarities = new double[input.getEventWords().size()];
		for (int i = 0; i < maxSimilarities.length; i++){
			maxSimilarities[i] = max(similarities[i]);
		}
		if (maxSimilarities.length < 1){
			throw new UnknownException();
		}
		return sum(maxSimilarities) / maxSimilarities.length;
				
	}

	/**
	 * Cost to make the similarity matrix
	 */
	@Override
	public Expenditure[] getCost(SimilarityQuestion args) throws Exception {
		return getSimilarityCost(args.getEventWords().size() * args.getSpeakerWords().size());
	}

}
