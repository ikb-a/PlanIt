package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import PlanIt.monthSuggestion.ci.EnumNominalConverter;
import PlanIt.monthSuggestion.resources.OpenEvalMonthController;
import PlanIt.monthSuggestion.sources.Holiday;
import PlanIt.monthSuggestion.sources.MLMonthSuggestionContract;
import PlanIt.monthSuggestion.sources.Month;
import PlanIt.monthSuggestion.sources.Precipitation;
import PlanIt.monthSuggestion.sources.Temperature;
import PlanIt.monthSuggestion.sources.openEvalThresholdSource;
import Planit.dataObjects.Event;
import Planit.dataObjects.util.EventExtractor;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.machineLearning.MLToCIContract;
import edu.toronto.cs.se.ci.machineLearning.util.training.NominalTrainer;
import edu.toronto.cs.se.ci.utils.searchEngine.MemoizingSearch;

public class TrainCIMLAggregator {

	public static final String FOLDER = "./src/main/resources/data/monthData/";

	public static void main(String[] args) throws Exception {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		Map<String, Event[]> trainingData = new HashMap<String, Event[]>();

		EventExtractor extractor = new EventExtractor();
		Event[] JanuaryEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/1_January.json"));
		trainingData.put("January", JanuaryEvents);

		Event[] FebruaryEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/2_February.json"));
		trainingData.put("February", FebruaryEvents);

		Event[] MarchEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/3_March.json"));
		trainingData.put("March", MarchEvents);

		Event[] AprilEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/4_April.json"));
		trainingData.put("April", AprilEvents);

		Event[] MayEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/5_May.json"));
		trainingData.put("May", MayEvents);

		Event[] JuneEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/6_June.json"));
		trainingData.put("June", JuneEvents);

		Event[] JulyEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/7_July.json"));
		trainingData.put("July", JulyEvents);

		Event[] AugustEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/8_August.json"));
		trainingData.put("August", AugustEvents);

		Event[] SeptemberEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/9_September.json"));
		trainingData.put("September", SeptemberEvents);

		Event[] OctoberEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/10_October.json"));
		trainingData.put("October", OctoberEvents);

		Event[] NovemberEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/11_November.json"));
		trainingData.put("November", NovemberEvents);

		Event[] DecemberEvents = extractor.extractEventsFromJsonFile(new File(FOLDER + "CI/12_December.json"));
		trainingData.put("December", DecemberEvents);

		extractor = null;

		// month with least precipitation
		Contracts.register(new Precipitation(1));
		// month with second least precipitation
		Contracts.register(new Precipitation(2));
		// month with third least precipitation
		Contracts.register(new Precipitation(3));

		// month with best temperature
		Contracts.register(new Temperature(1));
		// month with second best temperature
		Contracts.register(new Temperature(2));
		// month with third best temperature
		Contracts.register(new Temperature(3));

		Contracts.register(new Holiday());

		// TODO: Use correct search engine
		OpenEvalMonthController
				.setSearchEngine(new MemoizingSearch(FOLDER + "CI/memoizedSearch.ser", new UnBubbleSearchHTML()));
		OpenEvalMonthController.setVerbose(true);
		Contracts.register(new openEvalThresholdSource(Month.January));
		Contracts.register(new openEvalThresholdSource(Month.February));
		Contracts.register(new openEvalThresholdSource(Month.March));
		Contracts.register(new openEvalThresholdSource(Month.April));
		Contracts.register(new openEvalThresholdSource(Month.May));
		Contracts.register(new openEvalThresholdSource(Month.June));
		Contracts.register(new openEvalThresholdSource(Month.July));
		Contracts.register(new openEvalThresholdSource(Month.August));
		Contracts.register(new openEvalThresholdSource(Month.September));
		Contracts.register(new openEvalThresholdSource(Month.October));
		Contracts.register(new openEvalThresholdSource(Month.November));
		Contracts.register(new openEvalThresholdSource(Month.December));

		NominalTrainer<Event, Month> nt = new NominalTrainer<Event, Month>(
				new MLToCIContract<Event, Month>(MLMonthSuggestionContract.class));
		nt.createNominalTrainingData(trainingData, new EnumNominalConverter(), FOLDER + "CI/CITrainingData_Full1.arff");
	}
}
