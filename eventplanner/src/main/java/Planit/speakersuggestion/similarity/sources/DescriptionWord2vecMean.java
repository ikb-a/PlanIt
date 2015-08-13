package Planit.speakersuggestion.similarity.sources;

import java.io.IOException;

import com.google.common.base.Optional;

import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.wordsimilarity.Word2Vec;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Compares the description of an event to the bio of a speaker.
 * Uses Word2Vec to find the similaritiy between words.
 * Returns the average similarity between any word pair it could analyze.
 * 
 * @author wginsberg
 */
public class DescriptionWord2vecMean extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {

	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {
		
		return new Opinion<Double, Void>(args, similarity(args), null, this);
		
	}
	
	public static Double similarity(ComparisonRequest args) throws UnknownException{
		try {
			double [][] similarityMatrix = Word2Vec.getInstance().similarity(args.getEvent().getWords(), args.getSpeaker().getWords());
			double mean = MatrixUtil.mean(similarityMatrix);
			return mean;
		} catch (IOException e) {
			throw new UnknownException(e);
		}
	}
	
	@Override
	public String getName(){
		return "event_description-speaker_bio-word2vec-mean";
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
