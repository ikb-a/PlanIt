package edu.toronto.cs.Planit.ci.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

import edu.toronto.cs.Planit.ci.AggregatorWrapper;
import edu.toronto.cs.Planit.ci.ml.WekaCompatibleResponse;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * This aggregator produces a weka data set which can be used for classification, or saved to a file for use in Weka GUI or command line interface.
 * All attributes/sources should have a numeric value range
 * @author wginsberg
 */
public class WekaDatasetAggregatorNumeric <I, Q> extends AggregatorWrapper<WekaCompatibleResponse<I>, Void, Q> {

	Instances dataset;
	double classification;
	Map<Source<?, ?, ?>, Integer> attributeIndices;
	
	/**
	 * Create an aggregator which will passively  generate a data set with the given name, and use the given aggregator
	 * to actively to the aggregation which is returned as an opinion.
	 * In the contained data set there will by default be a class attribute called "classification" at index 0.
	 * @param relationName
	 * @param wrapAround
	 */
	public WekaDatasetAggregatorNumeric(String relationName, Aggregator<WekaCompatibleResponse<I>, Void, Q> wrapAround){
		super(wrapAround);
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute classAttribute = new Attribute("classification");
		attrs.add(classAttribute);
		
		dataset = new Instances(relationName, attrs, 0);
		dataset.setClass(classAttribute);
		dataset.setClassIndex(0);
	}
	
	/**
	 * Create an aggregator which passively creates a data set and always returns an empty opinion.
	 * @param relationName
	 */
	public WekaDatasetAggregatorNumeric(String relationName){
		this(relationName, null);
	}
	
	/**
	 * Returns the data set which has accumulated from executing the aggregation function some number of times.
	 * @return
	 */
	public Instances getDataset(){
		return dataset;
	}

	/**
	 * Adds each opinion to the data set.
	 * Creates a new data set if this is the first time the method is called.
	 * Adds a new attribute for each opinion when it comes from a source which has not already been seen.
	 */
	@Override
	public void passiveAggregation(
			List<Opinion<WekaCompatibleResponse<I>, Void>> opinions,
			Optional<Result<WekaCompatibleResponse<I>, Q>> wrappedAggregationResult) {
		
		//ensure all opinions can be represented by the attributes
		for (Opinion<WekaCompatibleResponse<I>, Void> opinion : opinions){
			if (!datasetHasAttribute(opinion)){
				
				//create a new attribute
				String attributeName = opinion.getValue().getSource().getName();
				Attribute attribute = new Attribute(attributeName);
				
				//add it to the data set
				Integer attributeIndex = dataset.numAttributes();
				getAttributeMap().put(opinion.getValue().getSource(), attributeIndex);
				dataset.insertAttributeAt(attribute, attributeIndex);
			}
		}
		
		//add each opinion to the data set in a single instance
		SparseInstance instance = new SparseInstance(dataset.numAttributes());
		instance.setDataset(dataset);
		instance.setClassValue(getClassification());
		//set the values of the instance
		for (Opinion<WekaCompatibleResponse<I>, Void> opinion : opinions){
			int attributeIndex = getAttributeMap().get(opinion.getValue().getSource());
			instance.setValue(attributeIndex, opinion.getValue().getNumeric());
		}
		dataset.add(instance);
	}


	/**
	 * Returns true if there is an attribute in the existing data set which corresponds to the source which produced the opinion.
	 * @param opinion
	 * @return
	 */
	private boolean datasetHasAttribute(Opinion<WekaCompatibleResponse<I>, Void> opinion){
		String toCheckFor = opinion.getValue().getSource().getName();
		return dataset.attribute(toCheckFor) != null;
	}
	
	/**
	 * A map between the name of a source and the attribute which represents the source.
	 * @return
	 */
	private Map<Source<?, ?, ?>, Integer> getAttributeMap(){
		if (attributeIndices == null){
			attributeIndices = new HashMap<Source<?, ?, ?>, Integer>();
		}
		return attributeIndices;
	}

	/**
	 * Get the classification which is currently being set on instances
	 * @return
	 */
	public double getClassification() {
		return classification;
	}

	/**
	 * Set the classification which should be set on new instances
	 * @param classification
	 */
	public void setClassification(double classification) {
		this.classification = classification;
	}
}
