package Planit.speakersuggestion.similarity.sources;

import com.google.common.base.Optional;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.wordsimilarity.CoOccurrence;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

public class CoOccurrenceSource extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {

		Double result = CoOccurrence.getInstance().cooccurrence(args.getSpeaker().getName(), args.getEvent().getKeyWords());
		if (result == null){
			throw new UnknownException();
		}
		return new Opinion<Double, Void>(args, result, null, this);
	}
	
	@Override
	public String getName(){
		return "bing-keyword-and-speaker-cooccurrence-(continuous)";
	}
	
	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		return new Expenditure [0];
	}

	@Override
	public Void getTrust(ComparisonRequest args, Optional<Double> value) {
		return null;
	}

}
