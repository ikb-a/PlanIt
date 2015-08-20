package Planit.speakersuggestion.similarity.sources;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.wordsimilarity.DocumentSimilarity;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Discretized version of DocumentSimilaritySource
 * @author wginsberg
 *
 */
public class DiscreteSource4 extends DocumentSimilaritySource {

	public Opinion<Double, Void> getOpinion(ComparisonRequest args) throws UnknownException{
		
		Double opinion = threshold(similarity(args));

		
		
		return new Opinion<Double, Void>(args, opinion, null, this);
	}
	
	public static Double similarity(ComparisonRequest args) throws UnknownException{
		Event e = args.getEvent();
		Speaker s = args.getSpeaker();
		
		String d1 = String.format("%s\n%s\n", e.getTitle(), e.getDescription());
		String d2 = String.format("%s\n%s\n%s\n%s", s.getName(), s.getProfessionalTitle(), s.getBio(), s.getTopics().toString());
		Double similarity = DocumentSimilarity.similarity(d1, d2);
		
		//System.out.printf("DocumentSimilaritySource - %f\n", threshold(similarity));
		
		if (similarity == null){
			throw new UnknownException();
		}
		else{
			return similarity;
		}
	}
	
	private static Double threshold(Double d){
		if (d == null){
			return null;
		}
		else if ( d < 0.15d){
			return 1d;
		}
		else if (d <= 0.25d){
			return 2d;
		}
		else{
			return 3d;
		}
	}
	
	@Override
	public String getName(){
		return "Document Similarity - Vitalie Scurtu (discrete)";
	}
}
