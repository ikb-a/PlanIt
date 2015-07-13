package edu.toronto.cs.se.ci.description_similarity.sources;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.description_similarity.SimilarityQuestion;

/**
 * The highest similarity between any pair (event keyword, speaker keyword)
 * @author wginsberg
 *
 */
public class MaxSimilarity extends SimilaritySource {

	@Override
	public Double getResponse(SimilarityQuestion input) throws UnknownException {
		return max(similarityMatrix(input));
	}

	/**
	 * Cost to make the similarity matrix
	 */
	@Override
	public Expenditure[] getCost(SimilarityQuestion args) throws Exception {

		return getSimilarityCost(args.getEventWords().size() * args.getSpeakerWords().size());
	}

}
