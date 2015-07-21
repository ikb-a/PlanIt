package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import weka.core.Instances;
import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.ComparisonRequest;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.NumericSimilarityContract;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.sources.Word2VecMaxSimilarity;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.sources.Word2VecMeanSimilarity;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.sources.Word2VecSimilarityOfMostFrequent;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;
import edu.toronto.cs.Planit.ci.ml.AttributePercentileTrust;
import edu.toronto.cs.Planit.ci.ml.NumericResponse;
import edu.toronto.cs.Planit.ci.ml.WekaDatasetAggregatorNumeric;

/**
 * Holds the contributional implementation of the similarity part of the speaker suggestion feature.
 * @author wginsberg
 *
 */
public class Relevance {
	
	private CI<ComparisonRequest, NumericResponse<ComparisonRequest, AttributePercentileTrust>, AttributePercentileTrust, Void> ci;
	private WekaDatasetAggregatorNumeric<ComparisonRequest, AttributePercentileTrust, Void> wekaAgg;
	
	/**
	 * Does comparisons for each speaker on the event and returns the most similar speakers.
	 */
	public Collection<Speaker> compare(Event event, Collection<Speaker> speakers, Allowance [] budget) {
		
		getCI();
		
		for (Speaker speaker : speakers){
			ComparisonRequest request = new ComparisonRequest(event, speaker);
			Result<NumericResponse<ComparisonRequest, AttributePercentileTrust>, Void> result;
			try {
				/*
				 * Estimate<NumericResponse<ComparisonRequest, AttributePercentileTrust>, Void> estimate = ci.apply(request, budget);
				 * result = estimate.get();
				 */
				result = ci.applySync(request, budget);
				System.out.println(result);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				continue;
			}
			/*
			 * TODO: use the value to construct the lists
			 */
		}
		
		return null;
	}
	
	/**
	 * Return the dataset which has been generated from executing on cases.
	 */
	public Instances getDataset(){
		if (wekaAgg != null){
			return wekaAgg.getDataset();			
		}
		return null;
	}
	
	private CI<ComparisonRequest, NumericResponse<ComparisonRequest, AttributePercentileTrust>, AttributePercentileTrust, Void> getCI(){

		if (ci == null){
			ci = new CI<ComparisonRequest, NumericResponse<ComparisonRequest, AttributePercentileTrust>, AttributePercentileTrust, Void>
			(NumericSimilarityContract.class,
					getWekaAgg(),
					new AllSelector<ComparisonRequest, NumericResponse<ComparisonRequest, AttributePercentileTrust>, AttributePercentileTrust>());
		}
		
		if (Contracts.discover(NumericSimilarityContract.class).isEmpty()){
			Contracts.register(new AdaptorDoubleToNumeric<AttributePercentileTrust>(new Word2VecMaxSimilarity(100)));
			Contracts.register(new AdaptorDoubleToNumeric<AttributePercentileTrust>(new Word2VecMeanSimilarity(100)));
			Contracts.register(new AdaptorDoubleToNumeric<AttributePercentileTrust>(new Word2VecSimilarityOfMostFrequent()));
		}

		return ci;
	}
	
	private WekaDatasetAggregatorNumeric<ComparisonRequest, AttributePercentileTrust, Void> getWekaAgg(){
		if (wekaAgg == null){
			wekaAgg = new WekaDatasetAggregatorNumeric<ComparisonRequest, AttributePercentileTrust, Void>("speaker-suitability");
		}
		return wekaAgg;
	}
	
}
