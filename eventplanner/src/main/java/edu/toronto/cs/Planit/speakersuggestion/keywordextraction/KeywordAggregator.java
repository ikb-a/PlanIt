package edu.toronto.cs.Planit.speakersuggestion.keywordextraction;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * An aggregator meant specifically to aggregate lists of keywords.
 * @author wginsberg
 *
 */
public class KeywordAggregator implements Aggregator<List<String>, Void, Void> {

	/**
	 * Aggregates based on a simple set union.
	 */
	@Override
	public Optional<Result<List<String>, Void>> aggregate(
			List<Opinion<List<String>, Void>> opinions) {
		
		List<String> words = new ArrayList<String>();
		for (Opinion<List<String>, Void> opinion : opinions){
			if (opinion == null || opinion.getValue() == null){
				continue;
			}
			for (String word : opinion.getValue()){
				if (!words.contains(word)){
					words.add(word);
				}
			}
		}
		
		return Optional.of(new Result<List<String>, Void>(words, null));
	}

}
