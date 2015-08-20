package Planit.speakersuggestion.similarity.sources;

import java.io.IOException;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.wordsimilarity.Word2Vec;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Discretized version of DescriptionWord2vecMean
 * @author wginsberg
 *
 */
public class DiscreteSource2 extends DescriptionWord2vecMean {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args) throws UnknownException{
		
		Double opinion = threshold(similarity(args));
		
		return new Opinion<Double, Void>(args, opinion, null, this);
	}
	
	public static Double similarity(ComparisonRequest args) throws UnknownException{
		try {
			double [][] similarityMatrix = Word2Vec.getInstance().similarity(args.getEvent().getWords(), args.getSpeaker().getWords());
			double mean = MatrixUtil.mean(similarityMatrix);
			
			//System.out.printf("DescriptionWord2vecMean - %f\n", threshold(mean));
			
			return mean;
		} catch (IOException e) {
			throw new UnknownException(e);
		}
	}
	
	private static Double threshold(Double d){
		if (d == null){
			return null;
		}
		else if (d < -0.27d){
			return 1d;
		}
		else if (d <= 0.18d){
			return 2d;
		}
		else{
			return 3d;
		}
	}
	
	@Override
	public String getName(){
		return "Word2Vec event description vs speaker bio mean (discrete)";
	}
}
