package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import weka.core.Instances;
import weka.core.converters.SerializedInstancesSaver;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edu.toronto.cs.Planit.ci.AggregatorWrapper;
import edu.toronto.cs.Planit.ci.ml.Trust;
import edu.toronto.cs.Planit.ci.ml.WekaDatasetAggregatorNumeric;
import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.sources.Word2VecMeanSimilarity;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.selectors.AllSelector;

public class Playground {

	static String outputDirectory = "main/resources/dataset/similarity/";
	
	static String eventsLocation = "main/resources/scrape/event and speakers/manual-low.json";
	static EventAndSpeakerRelevance rel;
	static Event [] events;

	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {

		/*
		 * Load events and speakers
		 */
		Gson gson = new Gson();
		events = gson.fromJson(new FileReader(eventsLocation), Event[].class);
		
		/*
		 * Set up the ci function
		 */
		Contracts.register(new Word2VecMeanSimilarity());
		HardCodedAggregator wrappedAgg = new HardCodedAggregator();
		AggregatorWrapper<Similarity, Trust<ComparisonRequest, Similarity>, Double> agg = new WekaDatasetAggregatorNumeric<ComparisonRequest, Similarity, Double>("event speaker similarity", wrappedAgg);
		Allowance [] budget = new Allowance [] {new Time(1, TimeUnit.SECONDS)};
		CI<ComparisonRequest, Similarity, Trust<ComparisonRequest, Similarity>, Double> ci;
		ci = new CI<ComparisonRequest, Similarity, Trust<ComparisonRequest,Similarity>, Double>
			(NumericSimilarityContract.class, agg, new AllSelector<ComparisonRequest, Similarity, Trust<ComparisonRequest,Similarity>>());
		
		/*
		 * execute the ci on the events
		 */
		for (Event event : events){
			for (Speaker speaker : event.getConfirmedSpeakers()){
				try {
					ci.apply(new ComparisonRequest(event, speaker), budget).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*
		 * Save and view the results
		 */
		WekaDatasetAggregatorNumeric<ComparisonRequest, Similarity, Double> wekaAgg = (WekaDatasetAggregatorNumeric<ComparisonRequest, Similarity, Double>) agg;
		Instances instances = wekaAgg.getDataset();
		SerializedInstancesSaver saver = new SerializedInstancesSaver();
		Time currentTime = new Time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		String fileName = outputDirectory + currentTime.toString();
		saver.setDestination(new File(fileName));
		saver.setInstances(instances);
		saver.writeBatch();
		System.out.println(instances.toString());
	}

}
