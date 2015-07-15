package edu.toronto.cs.se.ci.description_similarity.sources;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.description_similarity.SimilarityQuestion;

/**
 * The average similarity of all non zero similarities
 * @author wginsberg
 *
 */
public class FullMeanSimilarity extends SimilaritySource {

	@Override
	public String getName(){
		return "average-of-all-word-similarities";
	}

	@Override
	public Double getResponse(SimilarityQuestion input) throws UnknownException {
		
		double [][] similarities = similarity(input);
		return mean(similarities);
				
	}

	/**
	 * Cost to make the similarity matrix
	 */
	@Override
	public Expenditure[] getCost(SimilarityQuestion args) throws Exception {
		return getSimilarityCost(args.getEventWords().size() * args.getSpeakerWords().size());
	}

}
