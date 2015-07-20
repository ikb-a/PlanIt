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
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * This aggregator produces a weka dataset which can be used for classification, or saved to a file for use in weka gui / command line interface.
 * @author wginsberg
 */
public class WekaDatasetAggregatorNumeric <I, T, Q> extends AggregatorWrapper<NumericResponse<I, T>, T, Q> {

	Instances dataset;
	Map<Source<I, Double, T>, Integer> attributeIndices;
	
	/**
	 * Create an aggregator which will passively  generate a data set with the given name, and use the given aggregator
	 * to actively to the aggregation which is returned as an opinion.
	 * In the contained data set there will by default be a class attribute called "classification" at index 0.
	 * @param relationName
	 * @param wrapAround
	 */
	public WekaDatasetAggregatorNumeric(String relationName, Aggregator<NumericResponse<I, T>, T, Q> wrapAround){
		super(wrapAround);
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		dataset = new Instances(relationName, attrs, 0);
		Attribute classAttribute = new Attribute("classification");
		dataset.insertAttributeAt(classAttribute , 0);
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
	
	public Instances getDataset(){
		return dataset;
	}
	
	/**
	 * Adds each opinion to the dataset.
	 * Creates a new dataset if this is the first time the method is called.
	 * Adds a new attribute for each opinion when it comes from a source which has not already been seen.
	 */
	@Override
	public void passiveAggregation(
			List<Opinion<NumericResponse<I, T>, T>> opinions,
			Optional<Result<NumericResponse<I, T>, Q>> wrappedAggregationResult) {
		
		//ensure all opinions can be represented by the attributes
		for (Opinion<NumericResponse<I, T>, T> opinion : opinions){
			if (!datasetHasAttribute(opinion)){
				
				//create a new attribute
				String attributeName = opinion.getValue().getSource().getName();
				Attribute attribute = new Attribute(attributeName);
				
				//add it to the data set
				int attributeIndex = dataset.numAttributes();
				getAttributeMap().put(opinion.getValue().getSource(), attributeIndex);
				dataset.insertAttributeAt(attribute, attributeIndex);
			}
		}
		
		//add each opinion to the data set in a single instance
		SparseInstance instance = new SparseInstance(dataset.numAttributes());
		instance.setDataset(dataset);
		instance.setClassMissing();
		//set the values of the instance
		for (Opinion<NumericResponse<I, T>, T> opinion : opinions){
			int attributeIndex = getAttributeMap().get(opinion.getValue().getSource());
			instance.setValue(attributeIndex, opinion.getValue().get());
		}
		dataset.add(instance);
	}


	/**
	 * Returns true if there is an attribute in the existing data set which corresponds to the source which produced the opinion.
	 * @param opinion
	 * @return
	 */
	private boolean datasetHasAttribute(Opinion<NumericResponse<I, T>, T> opinion){
		String toCheckFor = opinion.getValue().getSource().getName();
		return dataset.attribute(toCheckFor) != null;
	}
	
	/**
	 * A map between the name of a source and the attribute which represents the source.
	 * @return
	 */
	private Map<Source<I, Double, T>, Integer> getAttributeMap(){
		if (attributeIndices == null){
			attributeIndices = new HashMap<Source<I, Double, T>, Integer>();
		}
		return attributeIndices;
	}
}
