package edu.toronto.cs.se.ci.description_similarity.sources;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.description_similarity.SimilarityQuestion;

/**
 * The standard deviation of the similarities of every pair of words (event keyword, speaker keyword)
 * @author wginsberg
 *
 */
public class StdDevOfSimilarity extends SimilaritySource {
	
	StandardDeviation stdDev;
	
	public StdDevOfSimilarity() {
		stdDev = new StandardDeviation();
	}
	
	@Override
	public Double getResponse(SimilarityQuestion input) throws UnknownException {
		
		stdDev.clear();
		double [][] similarities = similarityMatrix(input);
		for (int i = 0; i < similarities.length; i++){
			stdDev.incrementAll(similarities[i]);
		}
		try{
			return stdDev.getResult();
		}
		catch (MathIllegalArgumentException e){
			throw new UnknownException(e);
		}
	}

	/**
	 * Cost to make the similarity matrix
	 */
	@Override
	public Expenditure[] getCost(SimilarityQuestion args) throws Exception {
		return getSimilarityCost(args.getEventWords().size() * args.getSpeakerWords().size());

	}

}
