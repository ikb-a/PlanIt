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
import Planit.speakersuggestion.similarity.sources.DiscreteSource1;
import Planit.speakersuggestion.similarity.sources.DiscreteSource2;
import Planit.speakersuggestion.similarity.sources.DiscreteSource3;
import Planit.speakersuggestion.similarity.sources.DiscreteSource4;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractDouble;
import Planit.speakersuggestion.similarity.util.SimilarityContractWekaCompatible;
import Planit.speakersuggestion.similarity.util.SourceAdaptor;
import Planit.speakersuggestion.similarity.util.SummingAggregator;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * Holds the contributional implementation of the similarity part of the speaker suggestion feature.
 * This implementation uses a ranking aggregation, where each similarity source gives a score, and these scores are summed so that speakers can be ranked accordingly.
 * @author wginsberg
 *
 */
public class SuitabilityJudgeRankingAggregation implements SuitabilityJudge{

	private SuggestedSpeakers suggestion;
	
	private CI<ComparisonRequest, Double, Void, Void> ci;
	
	public SuitabilityJudgeRankingAggregation() {
		/*
		 * Register some sources if needed
		 */
		if (Contracts.discover(SimilarityContractWekaCompatible.class).isEmpty()){
			
			Contracts.register(new SourceAdaptor(new DiscreteSource1()));
			Contracts.register(new SourceAdaptor(new DiscreteSource2()));
			Contracts.register(new SourceAdaptor(new DiscreteSource3()));
			Contracts.register(new SourceAdaptor(new DiscreteSource4()));
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
