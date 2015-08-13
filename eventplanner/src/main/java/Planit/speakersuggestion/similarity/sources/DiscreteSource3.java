package Planit.speakersuggestion.similarity.sources;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;
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
		
		if (args.getEvent().getKeyWords().size() < 1 ||
				args.getSpeaker().getTopicKeywords().size() < 1){
			throw new UnknownException();
		}
		
		double [][] similarityMatrix = WordnetWUP.compare(args.getEvent().getKeyWords(), args.getSpeaker().getTopicKeywords());
		double max = MatrixUtil.max(similarityMatrix);
		
		System.out.printf("KeywordWordnetMax - %f\n", threshold(max));
		System.out.println();
		
		System.out.println(args.getEvent().getKeyWords());
		System.out.println(args.getSpeaker().getTopicKeywords());
		System.out.println();
		
		System.out.println(MatrixUtil.toString(similarityMatrix));
		System.out.println();
		
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
