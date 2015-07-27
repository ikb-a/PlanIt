package edu.toronto.cs.Planit.speakersuggestion.similarity;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import weka.core.Instances;
import edu.toronto.cs.Planit.ci.ml.ClassDistributionQuality;
import edu.toronto.cs.Planit.ci.ml.WekaCompatibleResponse;
import edu.toronto.cs.Planit.ci.ml.WekaDatasetAggregatorNumeric;
import edu.toronto.cs.Planit.speakersuggestion.similarity.ci.SimilarityContractWekaCompatible;
import edu.toronto.cs.Planit.speakersuggestion.similarity.util.ComparisonRequest;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * A class used for generating training data to train a classifier with, from labeled inputs.
 * @author wginsberg
 *
 */
public class TrainingDataCreator {

	/*
	 * The CI framework is used to invoke all of the sources, and their opinions form the data set through an aggregator
	 */
	CI <ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void, ClassDistributionQuality> ci;
	WekaDatasetAggregatorNumeric<ComparisonRequest, ClassDistributionQuality> aggregator;
	AllSelector<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void> selector;
	
	public TrainingDataCreator(){
		aggregator = new WekaDatasetAggregatorNumeric<ComparisonRequest, ClassDistributionQuality>("speaker-suitability");
		selector = new AllSelector<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void>();
		ci = new CI<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void, ClassDistributionQuality>
		(SimilarityContractWekaCompatible.class, aggregator, selector);
	}
	
	/**
	 * Invoke sources on a new set of inputs and add the results to the data set.
	 * @param cases A set of inputs to generate training data with
	 * @param classification 1, 2, 3 for "best", "good", "bad", respectively, where this applys to every supplied case
	 * @param budget The budget to spend on each input
	 * @throws Throwable If every case failed during execution, the first thrown exception is thrown by this method.
	 */
	public void invokeOnLabeledInput(Collection<ComparisonRequest> cases, double classification, Allowance [] budget) throws Throwable{
		
		//set the classification which should be used when the case is turned into an instance in a dataset
		aggregator.setClassification(classification);
		
		boolean totalFailure = false;
		Throwable firstException = null;
		
		for (ComparisonRequest singleCase : cases){
			try {
				ci.apply(singleCase, budget).get();
			} catch (InterruptedException | ExecutionException e) {
				if (totalFailure = false){
					totalFailure = true;
					firstException = e;
				}
			}
		}
		
		if (totalFailure){
			throw firstException;
		}

	}
	
	/**
	 * Returns the data set which has been accumulated so far. This can be processed if desired using the methods in WekaDatasetAggregatorNumeric
	 * @return A weka data set which resulted from executing sources on inputs
	 */
	public Instances getRawDataSet(){
		return aggregator.getDataset();
	}
}
