package Planit.speakersuggestion.keywordextraction.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Planit.speakersuggestion.keywordextraction.resource.YahooContentAnalysis;
import Planit.speakersuggestion.keywordextraction.util.ContentAnalysis;
import Planit.speakersuggestion.keywordextraction.util.WordListKeywordsContract;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Generates keywords based on the suggestions of Yahoo Content Analysis.
 * This source returns all of the keywords Yahoo suggests.
 * @author wginsberg
 *
 */
public class YahooKeywordGeneration extends Source<List<String>, List<String>, Void> implements
		WordListKeywordsContract {

	@Override
	public Opinion<List<String>, Void> getOpinion(List<String> input)
			throws UnknownException {

		ContentAnalysis analysis;
		try{
			analysis = YahooContentAnalysis.analyze(input);
		}
		catch (IOException e){
			throw new UnknownException(e);
		}
		
		List<String> keyWords = new ArrayList<String>(analysis.getAllKeywords());
		return new Opinion<List<String>, Void>(input, keyWords, getTrust(input, Optional.of(keyWords)), this);
	}

	@Override
	public String getName(){
		return "yahoo-content-analysis-keywords";
	}
	
	@Override
	public Expenditure[] getCost(List<String> args) throws Exception {
		return new Expenditure [] {};
	}
	
	@Override
	public Void getTrust(List<String> args, Optional<List<String>> value) {
		return null;
	}


}
