package Planit.speakersuggestion.similarity.sources;

import java.io.IOException;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.wordsimilarity.Word2Vec;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;

/**
 * Compares the keywords of an event to the topics of a speaker.
 * Uses Word2Vec to find the similaritiy between words.
 * Returns the maximum similarity between any word pair it could analyze.
 * @author wginsberg
 *
 */
public class KeywordWord2vecMax extends Source<ComparisonRequest, Double, Void> implements SimilarityContractDouble {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {

		try {
			double [][] similarityMatrix = Word2Vec.getInstance().similarity(
					args.getEvent().getKeyWords(),
					args.getSpeaker().getTopics());
			double max = MatrixUtil.max(similarityMatrix);
			return new Opinion<Double, Void>(args, max, null, this);
		} catch (IOException e) {
			throw new UnknownException(e);
		}	
	}
	
	public String getName(){
		return "event_keyword-speaker_topic-word2vec-max";
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
