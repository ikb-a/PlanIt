package Planit.speakersuggestion.similarity.sources;

import com.google.common.base.Optional;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.wordsimilarity.DocumentSimilarity;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Uses the document similarity feature located at http://www.scurtu.it/documentSimilarity.html
 * @author wginsberg
 *
 */
public class DocumentSimilaritySource extends Source<ComparisonRequest, Double, Void> implements
		SimilarityContractDouble {

	@Override
	public String getName(){
		return "document-similarity-scurtu.it";
	}
	
	@Override
	public Opinion<Double, Void> getOpinion(ComparisonRequest args)
			throws UnknownException {

		Event e = args.getEvent();
		Speaker s = args.getSpeaker();
		
		String d1 = String.format("%s\n%s\n", e.getTitle(), e.getDescription());
		String d2 = String.format("%s\n%s\n%s\n%s", s.getName(), s.getProfessionalTitle(), s.getBio(), s.getTopics().toString());
		Double similarity = DocumentSimilarity.similarity(d1, d2);
		if (similarity == null){
			throw new UnknownException();
		}
		else{
			return new Opinion<Double, Void>(args, similarity, null, this);
		}
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
