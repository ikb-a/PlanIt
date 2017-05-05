package Planit.fakeevent.ci;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import Planit.dataObjects.Event;
import Planit.dataObjects.util.EventExtractor;
import Planit.fakeevent.sources.AreaCodeValid;
import Planit.fakeevent.sources.CheckOrganizerFB;
import Planit.fakeevent.sources.EmailNameCooccurence;
import Planit.fakeevent.sources.EventCheckContract;
import Planit.fakeevent.sources.EventSource;
import Planit.fakeevent.sources.GoogleMapsVenueAddress;
import Planit.fakeevent.sources.OrganizerFaceBookExists;
import Planit.fakeevent.sources.OrganizerWebSiteExists;
import Planit.fakeevent.sources.TimeIsInPlausibleRange;
import Planit.fakeevent.sources.TitleMatchesDescription;
import Planit.fakeevent.sources.TwitterHandleVerified;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Estimate;
import edu.toronto.cs.se.ci.GenericCI;
import edu.toronto.cs.se.ci.Selector;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaAggregator;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaNominalConverter;
import edu.toronto.cs.se.ci.machineLearning.util.MLWekaNominalAggregator;
import edu.toronto.cs.se.ci.machineLearning.util.MLWekaNominalThresholdAcceptor;
import edu.toronto.cs.se.ci.selectors.AllSelector;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.supervised.instance.ClassBalancer;

public class demo2 {

	private static final String TRAINING_DATA_LOCATION = "./src/main/resources/data/CITrainingData.arff";
	private static final String REAL_EVENT_TEST_DATA_LOC = "./src/main/resources/data/event data/RealEvents_Testing.json";
	private static final String FAKE_EVENT_TEST_DATA_LOC = "./src/main/resources/data/event data/FakeEvents_Testing.json";

	/**
	 * This class trains a CI's ML WEKA aggregator using the .arff file at
	 * {@link TRAINING_DATA_LOCATION}, and then runs the aggregator on the
	 * events stored in a .json file at REAL_EVENT_TEST_DATA_LOC and
	 * FAKE_EVENT_TEST_DATA_LOC, displaying the output.
	 */
	public static void main(String[] args) throws Exception {
		// disable annoying HTMLUnit messages produced by UnBubbleSearch, which
		// is in turn used by some of the sources
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		// Extract the real and fake events so that they can be run on the CI
		EventExtractor ee = new EventExtractor();
		Event[] posEvents = ee.extractEventsFromJsonFile(new File(REAL_EVENT_TEST_DATA_LOC));
		Event[] negEvents = ee.extractEventsFromJsonFile(new File(FAKE_EVENT_TEST_DATA_LOC));

		// create CI
		GenericCI<Event, Integer, String, Void, double[]> ci = createCI();

		// Display CI's output for all the real events, followed by opinion of
		// the fake events.
		System.out.println("\nReal Events:");
		for (Event event : posEvents) {
			displayOpinion(ci, event);
		}
		System.out.println("\nFake Events:");
		for (Event event : negEvents) {
			displayOpinion(ci, event);
		}

		// Close cache on all sources (Not part of normal CI behaviour):
		// This behaviour is thanks to the fact that these are EventSource
		// objects and therefore have a built-in caching feature.
		List<Source<Event, Integer, Void>> sourcesToClose = Contracts.discover(EventCheckContract.class);
		for (Source<Event, Integer, Void> source : sourcesToClose) {
			if (source instanceof EventSource) {
				EventSource toClose = (EventSource) source;
				toClose.close();
			}
		}
	}

	/**
	 * A converter that converts integers to their string equivalent.
	 * 
	 * As these sources return either 0 (false), 1 (true), or -1 (unknown -
	 * really should be throwing an Unknown Exception instead); the output value
	 * is effectively nominal, and can therefore be directly converted to a
	 * string to be a nominal value that WEKA can use.
	 * 
	 * @author ikba
	 *
	 */
	public static class intToString implements MLWekaNominalConverter<Integer> {
		@Override
		public String convert(Opinion<Integer, Void> sourceOutput) {
			return String.valueOf(sourceOutput.getValue());
		}
	}

	public static void displayOpinion(GenericCI<Event, Integer, String, Void, double[]> ci, Event event) {
		// Apply CI on the event with no budget to get a ListenableFuture of the
		// result
		Estimate<String, double[]> estimate = ci.apply(event, new Allowance[] {});
		Result<String, double[]> result = null;

		System.out.println(event.getTitle());

		// Get result from the Future
		try {
			result = estimate.get();
			// result = ci.applySync(test1, new Allowance[]{});
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return;
		}

		// Display result
		System.out.println("Decision of CI: " + result.getValue());

		// Display probabilities produced by WEKA classification into "0"
		// (false) or "1" (true)
		double[] probabilities = result.getQuality();
		System.out.println("Probability of fake: " + probabilities[0]);
		System.out.println("Probability of real: " + probabilities[1]);
	}

	/**
	 * Registers all the sources and creates the CI.
	 * 
	 * @return
	 */
	public static GenericCI<Event, Integer, String, Void, double[]> createCI() {
		Selector<Event, Integer, Void> sel = new AllSelector<Event, Integer, Void>();
		MLWekaNominalConverter<Integer> converter = new intToString();
		MLWekaAggregator<Integer, String, double[]> agg = null;
		try {
			agg = new MLWekaNominalAggregator<Integer>(converter, TRAINING_DATA_LOCATION, new NaiveBayes());
			agg.addFilter(new ClassBalancer());

			// Perform 10 fold cross validation to test Aggregator
			Evaluation result = agg.nFoldCrossValidate(10);
			System.out.println("10 Fold Cross Validation Results:");
			System.out.println(result.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// All values are considered OK, no values are considered GOOD. this
		// should force all source to be called.
		MLWekaNominalThresholdAcceptor<String> acc = new MLWekaNominalThresholdAcceptor<String>(0, 1);

		// TODO: Unused sources: CheckOrganizerFBExact; 
		Contracts.register(new AreaCodeValid());
		Contracts.register(new CheckOrganizerFB());
		Contracts.register(new EmailNameCooccurence());
		Contracts.register(new GoogleMapsVenueAddress());
		Contracts.register(new OrganizerFaceBookExists());
		Contracts.register(new OrganizerWebSiteExists());
		Contracts.register(new TimeIsInPlausibleRange());
		Contracts.register(new TitleMatchesDescription());
		Contracts.register(new TwitterHandleVerified());

		GenericCI<Event, Integer, String, Void, double[]> ci = new GenericCI<Event, Integer, String, Void, double[]>(
				EventCheckContract.class, agg, sel, acc);

		return ci;
	}
}
