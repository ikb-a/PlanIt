package Planit.speakersuggestion.similarity.sources;

import com.google.common.base.Optional;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.wordsimilarity.WordnetWUP;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Compares the keywords of an event to the topics of a speaker.
 * Uses Wordnet and the WUP algorithm to find the similaritiy between words.
 * Returns the average similarity between any word pair it could analyze.
 * @author wginsberg
 *
 */
public class KeywordWordnetMean extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {

		double [][] similarityMatrix = WordnetWUP.compare(args.getEvent().getKeyWords(), args.getSpeaker().getTopics());
		double mean = MatrixUtil.mean(similarityMatrix);
		return new Opinion<Double, Void>(args, mean, null, this);
	}
	
	public String getName(){
		return "event_keyword-speaker_topic-Wordnet-mean";
	}
	
	@Override
	public Expenditure[] getCost(ComparisonRequest args) throws Exception {
		return new Expenditure [] {};
	}

	@Override
	public Void getTrust(ComparisonRequest args, Optional<Double> value) {
		return null;
	}
}
