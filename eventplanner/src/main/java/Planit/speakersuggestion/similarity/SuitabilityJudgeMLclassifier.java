package Planit.speakersuggestion.similarity;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.ci.ml.ClassDistributionQuality;
import Planit.ci.ml.NaiveBayesAggregator;
import Planit.ci.ml.WekaCompatibleResponse;
import Planit.speakersuggestion.SuggestedSpeakers;
import Planit.speakersuggestion.similarity.sources.DescriptionWord2vecMean;
import Planit.speakersuggestion.similarity.sources.DocumentSimilaritySource;
import Planit.speakersuggestion.similarity.sources.KeywordWord2vecMax;
import Planit.speakersuggestion.similarity.sources.KeywordWordnetMax;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import Planit.speakersuggestion.similarity.util.SimilarityContractWekaCompatible;
import Planit.speakersuggestion.similarity.util.SourceAdaptor;
import weka.core.Instances;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Estimate;
import edu.toronto.cs.se.ci.Selector;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * Holds the contributional implementation of the similarity part of the speaker suggestion feature.
 * This implementation uses a machine learned classifier to make its judgments
 * @author wginsberg
 *
 */
public class SuitabilityJudgeMLclassifier implements SuitabilityJudge {
	
	static private final String trainingDataLocation = "src/main/resources/speaker suggestion/dataset/_dataset.arff";
	
	private CI<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void, ClassDistributionQuality> ci;
	
	private SuggestedSpeakers suggestion;
	
	/*
	 * Singleton object
	 */
	static private SuitabilityJudge instance;
	private SuitabilityJudgeMLclassifier () throws Exception{
		ci = createCI();
	}
	/**
	 * Returns an object which can judge the suitability of speakers.
	 * @throws Exception If a classifier needed to be loaded but there was a problem
	 */
	public static SuitabilityJudge getInstance() throws Exception{
		if (instance == null){
			instance = new SuitabilityJudgeMLclassifier();
		}
		return instance;
	}
		
	/* (non-Javadoc)
	 * @see Planit.speakersuggestion.similarity.SuitabilityJudge#evaluate(Planit.dataObjects.Event, java.util.Collection, edu.toronto.cs.se.ci.budget.Allowance[])
	 */
	@Override
	public Collection<Speaker> evaluate(Event event, Collection<Speaker> speakers, Allowance [] budget) throws ExecutionException {
		
		if (speakers == null || speakers.size() < 1){
			return new ArrayList<Speaker>(0);
		}
		
		suggestion = new SuggestedSpeakers();
		
		boolean totalFailure = true;
		Throwable firstFailure = null;
		for (Speaker speaker : speakers){
			ComparisonRequest request = new ComparisonRequest(event, speaker);
			try {
				Estimate<WekaCompatibleResponse<ComparisonRequest>, ClassDistributionQuality> estimate = ci.apply(request, budget);
				Result<WekaCompatibleResponse<ComparisonRequest>, ClassDistributionQuality> result = estimate.get();
				suggestion.addSpeaker(speaker, result.getQuality().getDistribution());
				totalFailure = false;
			} catch (InterruptedException | ExecutionException e) {
				if (firstFailure == null){
					firstFailure = e;
				}
			}
		}
		
		if (totalFailure){
			throw new ExecutionException("SuitabilityJudge failed to execute its contributional implementation", firstFailure);
		}
		
		return suggestion.getBestSpeakers();
	}
	
	/* (non-Javadoc)
	 * @see Planit.speakersuggestion.similarity.SuitabilityJudge#getSuggestion()
	 */
	@Override
	public SuggestedSpeakers getSuggestion() {
		return suggestion;
	}
	/**
	 * Returns a new contributional implementation for judging speaker suitability.
	 * @throws Exception If the classifier could not be loaded or built
	 */
	private static CI<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void, ClassDistributionQuality> createCI() throws Exception{

		/*
		 * Register some sources if needed
		 */
		if (Contracts.discover(SimilarityContractWekaCompatible.class).isEmpty()){
			Contracts.register(new SourceAdaptor(new DescriptionWord2vecMean()));
			Contracts.register(new SourceAdaptor(new KeywordWord2vecMax()));
			Contracts.register(new SourceAdaptor(new KeywordWordnetMax()));
			Contracts.register(new SourceAdaptor(new DocumentSimilaritySource()));
		}
		
		Aggregator<WekaCompatibleResponse<ComparisonRequest>, Void, ClassDistributionQuality> aggregator;
		aggregator = new NaiveBayesAggregator<ComparisonRequest>(loadClassifierTraining());
		Selector<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void> selector;
		selector = new AllSelector<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void>();
		
		return new CI<ComparisonRequest, WekaCompatibleResponse<ComparisonRequest>, Void, ClassDistributionQuality>
		(SimilarityContractWekaCompatible.class,
				aggregator,
				selector);

	}
	
	/**
	 * Load the training data for the classifier used for aggregation.
	 * @return
	 * @throws IOException 
	 */
	private static Instances loadClassifierTraining() throws IOException{
		FileReader reader = new FileReader(trainingDataLocation);
		Instances instances = new Instances(reader);
		instances.setClassIndex(instances.numAttributes() - 1);
		reader.close();
		return instances;
	}
}
