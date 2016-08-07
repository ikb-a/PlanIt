package Planit.speakersuggestion.similarity.sources;

import java.util.Arrays;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.wordsimilarity.CoOccurrence;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;


/**
 * Discrete source wrapper for the Google Search co-occurrence source
 * @author wginsberg
 *
 */
public class DiscreteSource5 extends CoOccurrenceSource {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest input) throws UnknownException{
		
		Double rawValue = 
				CoOccurrence.getInstance().cooccurrence(input.getSpeaker().getName(), Arrays.asList(input.getEvent().getKeyWords()));

		return new Opinion<Double, Void>(input, threshold(rawValue), null, this);
		
	}

	static public Double threshold(Double rawValue){
		if (rawValue == null){
			return null;
		}
		if (rawValue < -0.1d){
			return 1d;
		}
		if (rawValue < 0.1d){
			return 2d;
		}
		return 3d;
	}
	
	@Override
	public String getName(){
		return "bing-keyword-and-speaker-cooccurrence-(discrete)";
	}
}
