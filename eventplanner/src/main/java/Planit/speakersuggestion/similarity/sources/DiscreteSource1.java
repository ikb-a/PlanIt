package Planit.speakersuggestion.similarity.sources;

import java.io.IOException;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.wordsimilarity.Word2Vec;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Discretized version of KeywordWord2vecMax
 * @author wginsberg
 *
 */
public class DiscreteSource1 extends KeywordWord2vecMax {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {

		Double opinion = threshhold(similarity(args));
		return new Opinion<Double, Void>(args, opinion, null, this);
	}
	
	static public Double similarity(ComparisonRequest args) throws UnknownException{
		
		if (args.getEvent().getKeyWords().size() < 1 ||
				args.getSpeaker().getTopicKeywords().size() < 1){
			throw new UnknownException();
		}
		
		try {
			
			double [][] similarityMatrix = Word2Vec.getInstance().similarity(
					args.getEvent().getKeyWords(),
					args.getSpeaker().getTopicKeywords());
			double max = MatrixUtil.max(similarityMatrix);
			
			System.out.printf("KeywordWord2vecMax - %f\n", threshhold(max));
			
			System.out.println();
			System.out.println(args.getEvent().getKeyWords());
			System.out.println(	args.getSpeaker().getTopicKeywords());
			System.out.println();
			
			System.out.println();
			System.out.println(MatrixUtil.toString(similarityMatrix));
			System.out.println();
			
			
			return max;
		} catch (IOException e) {
			throw new UnknownException(e);
		}	
	}
	
	/**
	 * Thresholds a similarity score according to a set of intervals
	 * @param value Any double
	 * @return The value 0, 1, 2 or null if null was supplied
	 */
	private static Double threshhold(Double value){
		
		if (value == null){
			return null;
		}
		else if (value < 0.33d){
			return 1d;
		}
		else if (value <= 0.66d){
			return 2d;
		}
		else{
			return 3d;
		}
	}
	
	@Override
	public String getName(){
		return "Word2Vec event keyword vs speaker topic max (discrete)";
	}
}
