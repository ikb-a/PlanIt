package PlanIt.monthSuggestion.ci;

import java.util.concurrent.ExecutionException;

import PlanIt.monthSuggestion.resources.OpenEvalMonthController;
import PlanIt.monthSuggestion.sources.Holiday;
import PlanIt.monthSuggestion.sources.MLMonthSuggestionContract;
import PlanIt.monthSuggestion.sources.Month;
import PlanIt.monthSuggestion.sources.Precipitation;
import PlanIt.monthSuggestion.sources.Temperature;
import PlanIt.monthSuggestion.sources.openEvalThresholdSource;
import PlanIt.monthSuggestion.trainingDataGeneration.YandexSearch;
import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Estimate;
import edu.toronto.cs.se.ci.GenericAggregator;
import edu.toronto.cs.se.ci.GenericCI;
import edu.toronto.cs.se.ci.Selector;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.machineLearning.MLToCIContract;
import edu.toronto.cs.se.ci.machineLearning.util.MLWekaNominalAggregator;
import edu.toronto.cs.se.ci.machineLearning.util.MLWekaNominalThresholdAcceptor;
import edu.toronto.cs.se.ci.selectors.AllSelector;
import edu.toronto.cs.se.ci.utils.searchEngine.MemoizingSearch;
import weka.classifiers.bayes.NaiveBayes;

public class demo {
	/**
	 * Folder at which the files are stored. Hard coded for simplicity.
	 */
	public static final String FOLDER = "./src/main/resources/data/monthData/";

	/**
	 * Create a event (hardcoded right now) and classifies it using the CI
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// ADD SOURCES TO CONTRACTS (same sources as those used to train the 
		//aggregator in TrainCIMLAggregator)
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

		Holiday h = new Holiday();
		h.loadSavedHoliday("./src/main/resources/data/monthData/HolidayData/Holidays.ser");
		Contracts.register(h);

		OpenEvalMonthController.setSearchEngine(
				new MemoizingSearch(FOLDER + "CI/memoizedSearchCITrainYandex.ser", new YandexSearch()));
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

		//Create a naive bayes aggregator
		GenericAggregator<Month, String, Void, double[]> agg = new MLWekaNominalAggregator<Month>(
				new EnumNominalConverter(), FOLDER + "CI/CITrainingData_Full_Part2.arff", new NaiveBayes());
		
		// All values are considered OK, no values are considered GOOD. this
		// should force all sources to be called.
		MLWekaNominalThresholdAcceptor<String> acc = new MLWekaNominalThresholdAcceptor<String>(0, 1);
		Selector<Event, Month, Void> sel = new AllSelector<Event, Month, Void>();

		//Create the CI
		GenericCI<Event, Month, String, Void, double[]> ci = new GenericCI<Event, Month, String, Void, double[]>(
				new MLToCIContract<Event, Month>(MLMonthSuggestionContract.class), agg, sel, acc);
		
		
		//Create the event to classify
		Event event;
		
		Address eventAddress = new Address("1333", "Dorval Dr.", "Oakville", "ON", "Canada", "L6M 4G2");
		Venue eventVenue = new Venue("Glen Abbey Golf Course", eventAddress);
		
		String [] keywords = {"Santa Claus Float", "Holiday parade", "Christmas", };
		String description = "A festive Chritmas parade brough to you by the Glenn Abey golf course. Celebrate the spirit of Christmas with all your favourite floats!";
		event = Event.createEvent("Christmas Parade").setVenue(eventVenue).setKeyWords(keywords).setDescription(description);
		
		//Get a future of the result, Zero allowance is fine as costs were not implemented
		//In any of the sources used
		Estimate <String, double[]> estimate = ci.apply(event, new Allowance [] {});
		
		Result<String, double[]> result = null;
		try {
			result = estimate.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		System.out.println("Decision of CI: " + result.getValue());
		double[] probabilities = result.getQuality();
		assert (probabilities.length == 12);
		// Probabilities are in the same order as the months in the .arff training data
		// for the aggregator
		System.out.println("Probability of Jan: " + probabilities[7]);
		System.out.println("Probability of Feb: " + probabilities[8]);
		System.out.println("Probability of Mar: " + probabilities[5]);
		System.out.println("Probability of Apr: " + probabilities[9]);
		System.out.println("Probability of May: " + probabilities[3]);
		System.out.println("Probability of Jun: " + probabilities[0]);
		System.out.println("Probability of Jul: " + probabilities[6]);
		System.out.println("Probability of Aug: " + probabilities[10]);
		System.out.println("Probability of Sep: " + probabilities[4]);
		System.out.println("Probability of Oct: " + probabilities[1]);
		System.out.println("Probability of Nov: " + probabilities[11]);
		System.out.println("Probability of Dec: " + probabilities[2]);

	}

}
