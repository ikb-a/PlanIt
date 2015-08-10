package Planit.speakersuggestion.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.SuggestedSpeakers;
import Planit.speakersuggestion.similarity.sources.DescriptionWord2vecMax;
import Planit.speakersuggestion.similarity.sources.DescriptionWord2vecMean;
import Planit.speakersuggestion.similarity.sources.DescriptionWordnetMax;
import Planit.speakersuggestion.similarity.sources.DescriptionWordnetMean;
import Planit.speakersuggestion.similarity.sources.KeywordWord2vecMax;
import Planit.speakersuggestion.similarity.sources.KeywordWord2vecMean;
import Planit.speakersuggestion.similarity.sources.KeywordWordnetMax;
import Planit.speakersuggestion.similarity.sources.KeywordWordnetMean;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.similarity.util.SimilarityContractWekaCompatible;
import Planit.speakersuggestion.similarity.util.SummingAggregator;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * Holds the contributional implementation of the similarity part of the speaker suggestion feature.
 * This implementation uses a summing aggregation, where all of the responses of the sources are added together.
 * @author wginsberg
 *
 */
public class SuitabilityJudgeSummingAggregation implements SuitabilityJudge{

	private SuggestedSpeakers suggestion;
	
	private CI<ComparisonRequest, Double, Void, Void> ci;
	
	public SuitabilityJudgeSummingAggregation() {
		/*
		 * Register some sources if needed
		 */
		if (Contracts.discover(SimilarityContractWekaCompatible.class).isEmpty()){
			Contracts.register(new KeywordWord2vecMax());
			Contracts.register(new KeywordWord2vecMean());
			Contracts.register(new KeywordWordnetMax());
			Contracts.register(new KeywordWordnetMean());
			Contracts.register(new DescriptionWord2vecMax());
			Contracts.register(new DescriptionWord2vecMean());
			Contracts.register(new DescriptionWordnetMax());
			Contracts.register(new DescriptionWordnetMean());
		}
		
		ci = new CI<ComparisonRequest, Double, Void, Void>(
				SimilarityContractDouble.class,
				new SummingAggregator(),
				new AllSelector<ComparisonRequest, Double, Void>());
	}
	
	@Override
	public Collection<Speaker> evaluate(Event event, Collection<Speaker> speakers, Allowance[] budget)
			throws ExecutionException {
		
		suggestion = new SuggestedSpeakers();
		
		//evaulate each speaker
		Map<Speaker, Double> sums = new HashMap<Speaker, Double>();
		for (Speaker speaker : speakers){
			ComparisonRequest query = new ComparisonRequest(event, speaker);
			try {
				Result<Double, Void> result = ci.apply(query, budget).get();
				sums.put(speaker, result.getValue());
			} catch (InterruptedException e) {
				continue;
			}
		}
		
		//sort by evaluation
		List<Speaker> sortedSpeakers = new ArrayList<Speaker>(sums.size());
		List<Entry<Speaker, Double>> sortedEntries;
		sortedEntries = sums.entrySet().stream().sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue())).collect(Collectors.toList());
		for (Entry<Speaker, Double> entry : sortedEntries){
			sortedSpeakers.add(entry.getKey());
		}
		
		//categorize with even distribution
		int perClass = sortedSpeakers.size() / 3;
		//first take the best from the end of the list
		for (int i = 0; i < perClass; i++){	
			suggestion.getBestSpeakers().add(sortedSpeakers.remove(sortedSpeakers.size() - 1));
		}
		//then take the next best
		for (int i = 0; i < perClass; i++){	
			suggestion.getGoodSpeakers().add(sortedSpeakers.remove(sortedSpeakers.size() - 1));
		}
		//take all remaining as the worst
		while (sortedSpeakers.size() > 0){	
			suggestion.getBadSpeakers().add(sortedSpeakers.remove(sortedSpeakers.size() - 1));
		}
		
		return suggestion.getBestSpeakers();
	}

	@Override
	public SuggestedSpeakers getSuggestion() {
		return suggestion;
	}
}
