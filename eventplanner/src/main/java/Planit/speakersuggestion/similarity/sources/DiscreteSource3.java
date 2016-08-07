package Planit.speakersuggestion.similarity.sources;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;

import java.util.Arrays;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.wordsimilarity.WordnetWUP;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;

/**
 * Discretized version of KeywordWordnetMax
 * @author wginsberg
 *
 */
public class DiscreteSource3 extends KeywordWordnetMax {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args) throws UnknownException{
		
		Double opinion = threshold(similarity(args));
				
		return new Opinion<Double, Void>(args, opinion, null, this);
	}
	
	public static Double similarity(ComparisonRequest args) throws UnknownException{
		
		if (args.getEvent().getKeyWords().length < 1 ||
				args.getSpeaker().getTopicKeywords().size() < 1){
			throw new UnknownException();
		}
		
		double [][] similarityMatrix = WordnetWUP.compare(Arrays.asList(args.getEvent().getKeyWords()), args.getSpeaker().getTopicKeywords());
		double max = MatrixUtil.max(similarityMatrix);
		
		//System.out.printf("KeywordWordnetMax - %f\n", threshold(max));
		
		return max;
	}
	
	private static Double threshold(Double d){
		
		if (d == null){
			return null;
		}
		else if (d < 0.33d){
			return 1d;
		}
		else if (d <= 0.66d){
			return 2d;
		}
		else{
			return 3d;
		}
	}
	@Override
	public String getName(){
		return "Wordnet event keyword vs speaker topic max (discrete)";
	}
}
