package Planit.speakersuggestion.comparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.selectors.AllSelector;
import Planit.ci.ml.WekaCompatibleResponse;
import Planit.ci.ml.WekaDatasetAggregatorNumeric;
import Planit.speakersuggestion.comparison.sources.CoOccurrenceComparison;
import Planit.speakersuggestion.comparison.sources.SourceAdaptor;

public class DataPlayground {

	public static void main (String [] args) throws InterruptedException, ExecutionException, IOException{
		
		//load training case (one event and a list of speakers)
		String fileLocation = "";
		TrainingCase trainingCase = loadTrainingCase(fileLocation);
		
		//transform into CI inputs
		Collection<SpeakerComparisonRequest> forwardInputs = createForwardCiInputs(trainingCase);
		Collection<SpeakerComparisonRequest> backwardInputs = createBackwardCiInputs(trainingCase);
		

		//create CI
		Contracts.register(new SourceAdaptor(new CoOccurrenceComparison()));
		
		WekaDatasetAggregatorNumeric<SpeakerComparisonRequest, Void> agg;
		AllSelector<SpeakerComparisonRequest, WekaCompatibleResponse<SpeakerComparisonRequest>, Void> selector;
		
		agg = new WekaDatasetAggregatorNumeric<SpeakerComparisonRequest, Void>("Speaker-Comparison");
		selector = new AllSelector<SpeakerComparisonRequest, WekaCompatibleResponse<SpeakerComparisonRequest>, Void>();
		
		CI<SpeakerComparisonRequest, WekaCompatibleResponse<SpeakerComparisonRequest>, Void, Void> ci =
				new CI<SpeakerComparisonRequest, WekaCompatibleResponse<SpeakerComparisonRequest>, Void, Void>(SpeakerComparisonContractWekaCompatible.class, agg, selector);
		
		
		
		//invoke CI on inputs
		agg.setClassification(1d);
		for (SpeakerComparisonRequest input : forwardInputs){
			ci.apply(input, new Allowance [0]).get();
		}
		agg.setClassification(2d);
		for (SpeakerComparisonRequest input : backwardInputs){
			ci.apply(input, new Allowance [0]).get();
		}
		
		//save results
		Instances data = agg.getDataset();
		ArffSaver saver = new ArffSaver();
		String saveFileName = String.format("src/main/resources/speaker suggestion/comparison method/dataset/%s - %s.arff",
				data.relationName(),
				Calendar.getInstance().getTime().toString());
		saver.setInstances(data);
		saver.setDestination(new File(saveFileName));
		saver.writeBatch();
		
		System.out.println("Saved results to " + saveFileName);
	}
	
	/**
	 * Loads training cases from a file which contains one training case
	 */
	static TrainingCase loadTrainingCase(String fileLocation) throws FileNotFoundException{
		Gson gson = new Gson();
		FileReader reader = new FileReader(fileLocation);
		TrainingCase trainingCase = gson.fromJson(reader, TrainingCase.class);
		return trainingCase;
	}
	
	/**
	 * Creates a set of cases of the format (event, speaker1, speaker2) which can be passed to the CI,
	 * given a case of the format (event [best speaker , ... , worst speaker]) 
	 * @param trainingCase
	 */
	static Collection<SpeakerComparisonRequest> createForwardCiInputs(TrainingCase trainingCase){
		Collection<SpeakerComparisonRequest> cases = new ArrayList<SpeakerComparisonRequest>();
		Event event = trainingCase.getEvent();
		List<Speaker> speakers = trainingCase.getSpeakers();
		for (int i = 0; i < speakers.size(); i++){
			for (int j = i + 1; j < speakers.size(); j++){
				cases.add(new SpeakerComparisonRequest(event, speakers.get(i), speakers.get(j)));
			}
		}
		return cases;
	}
	
	/**
	 * Performs the same function as createForwardCiInputs, but each case has the two speakers switched in position
	 * @param trainingCase
	 * @return
	 */
	static Collection<SpeakerComparisonRequest> createBackwardCiInputs(TrainingCase trainingCase){
		Collection<SpeakerComparisonRequest> cases = new ArrayList<SpeakerComparisonRequest>();
		Event event = trainingCase.getEvent();
		List<Speaker> speakers = trainingCase.getSpeakers();
		for (int i = speakers.size() - 1; i >= 0; i--){
			for (int j = i - 1; j >= 0; j--){
				cases.add(new SpeakerComparisonRequest(event, speakers.get(i), speakers.get(j)));
			}
		}
		return cases;
	}
}
