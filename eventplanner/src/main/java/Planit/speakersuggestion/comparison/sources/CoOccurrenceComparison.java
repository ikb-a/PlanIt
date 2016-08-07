package Planit.speakersuggestion.comparison.sources;

import java.util.Arrays;

import com.google.common.base.Optional;

import Planit.speakersuggestion.comparison.SpeakerComparisonContract;
import Planit.speakersuggestion.comparison.SpeakerComparisonRequest;
import Planit.speakersuggestion.wordsimilarity.CoOccurrence;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * A source for comparing the suitability of speakers by doing co-occurrence queries with event keywords.
 * @author wginsberg
 *
 */
public class CoOccurrenceComparison extends Source<SpeakerComparisonRequest, Double, Void> implements
		SpeakerComparisonContract {

	@Override
	public String getName(){
		return "co-occurrence-query-speaker-comparison";
	}
	
	@Override
	public Opinion<Double, Void> getOpinion(
			SpeakerComparisonRequest args) throws UnknownException {
		
		Double s1Score = CoOccurrence.getInstance().cooccurrence(args.getS1().getName(), Arrays.asList(args.getEvent().getKeyWords()));
		Double s2Score = CoOccurrence.getInstance().cooccurrence(args.getS2().getName(), Arrays.asList(args.getEvent().getKeyWords()));
		
		if (s1Score == null || s2Score == null){
			throw new UnknownException();
		}
		
		Double result = thresholdResults(s1Score, s2Score);
		if (result == null){
			throw new UnknownException();
		}
		
		return new Opinion<Double, Void>(args, result, null, this);
	}

	private Double thresholdResults(Double s1Score, Double s2Score){
		
		if (s1Score == null || s2Score == null){
			return null;
		}
		
		if (s1Score == s2Score){
			return null;
		}
		
		if (s1Score > s2Score){
			return 1d;
		}
		
		if (s2Score > s1Score){
			return 2d;
		}
		
		return null;
	}
	
	@Override
	public Expenditure[] getCost(SpeakerComparisonRequest args)
			throws Exception {
		return new Expenditure [0];
	}

	@Override
	public Void getTrust(SpeakerComparisonRequest args,
			Optional<Double> value) {
		return null;
	}

}
