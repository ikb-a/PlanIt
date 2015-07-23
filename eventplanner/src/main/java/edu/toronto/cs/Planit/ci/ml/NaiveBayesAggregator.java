package edu.toronto.cs.Planit.ci.ml;

import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * An aggregator which uses the Naive Bayes algorithm to do its aggregation.
 * @author wginsberg
 */
public class NaiveBayesAggregator <I> implements WekaClassifierAggregator<I>{

	private NaiveBayes classifier;
	private Instances dataSet;

	/**
	 * Create a new aggregator given a data set. This will incite a new classifier to be built, using the entire data set to train it.
	 * @param dataSet A data set to train a new classifier on
	 * @throws Exception If a classifier could not be built from the instances
	 */
	public NaiveBayesAggregator(Instances dataSet) throws Exception{
		
		this.dataSet = dataSet;
		classifier = new NaiveBayes();
		classifier.buildClassifier(dataSet);
	}

	@Override
	public Optional<Result<WekaCompatibleResponse<I>, ClassDistributionQuality>> aggregate(
			List<Opinion<WekaCompatibleResponse<I>, Void>> opinions) {
		
		//do the classification
		Instance opinionAsInstance = createInstance(opinions);
		double [] distribution = null;
		try {
			distribution = getClassifier().distributionForInstance(opinionAsInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (distribution == null || distribution.length < 1){
			return Optional.absent();
		}
		
		//return the result
		int nominalValIndex = new Double(distribution[0]).intValue();
		String responseAsString = dataSet.classAttribute().value(nominalValIndex);
		WekaCompatibleResponse<I> response = new WekaCompatibleResponse<I>(null, null, responseAsString);
		ClassDistributionQuality quality = new ClassDistributionQuality(distribution);
		
		return Optional.of(new Result<WekaCompatibleResponse<I>, ClassDistributionQuality>(response, quality));
	}

	@Override
	public Instance createInstance(
			List<Opinion<WekaCompatibleResponse<I>, Void>> opinions) {
		
		SparseInstance instance = new SparseInstance(opinions.size());
		instance.setDataset(dataSet);
		
		//find the attribute that matches the source of the opinion
		for (Opinion<WekaCompatibleResponse<I>, ?> opinion : opinions){
			
			for (Attribute attribute : Util.attributeEnumerationToList(dataSet.enumerateAttributes())){
				
				//set the value of the attribute in the instance
				if (opinion.getValue().getSource().getName().equals(attribute.toString())){
					if (opinion.getValue().isNumeric()){
						instance.setValue(attribute, opinion.getValue().getNumeric());
					}
					else if (opinion.getValue().isNominal()){
						instance.setValue(attribute, opinion.getValue().getNominal());
					}
				}
			}
		}
		return instance;
	}
	
	@Override
	public Classifier getClassifier() {
		return classifier;
	}

	public Instances getDataSet() {
		return dataSet;
	}

}
