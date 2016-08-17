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

	// TODO: Switch sources so that they use Search Objects instead of being
	// hardcoded to use GoogleCSE
	public static void main(String[] args) throws Exception {
		// disable annoying HTMLUnit messages produced by UnBubbleSearch, which
		// is in turn used by some of the sources
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		EventExtractor ee = new EventExtractor();
		Event[] posEvents = ee.extractEventsFromJsonFile(new File(REAL_EVENT_TEST_DATA_LOC));
		Event[] negEvents = ee.extractEventsFromJsonFile(new File(FAKE_EVENT_TEST_DATA_LOC));

		GenericCI<Event, Integer, String, Void, double[]> ci = createCI();

		System.out.println("\nReal Events:");
		for (Event event : posEvents) {
			displayOpinion(ci, event);
		}
		System.out.println("\nFake Events:");
		for (Event event : negEvents) {
			displayOpinion(ci, event);
		}

		// Close cache on all sources (Not part of normal CI behaviour):
		List<Source<Event, Integer, Void>> sourcesToClose = Contracts.discover(EventCheckContract.class);
		for (Source<Event, Integer, Void> source : sourcesToClose) {
			if (source instanceof EventSource) {
				EventSource toClose = (EventSource) source;
				toClose.close();
				// TODO: Add way to remove closed source from contracts.
			}
		}
	}

	public static class intToString implements MLWekaNominalConverter<Integer> {
		@Override
		public String convert(Opinion<Integer, Void> sourceOutput) {
			return String.valueOf(sourceOutput.getValue());
		}
	}

	public static void displayOpinion(GenericCI<Event, Integer, String, Void, double[]> ci, Event event) {
		Estimate<String, double[]> estimate = ci.apply(event, new Allowance[] {});
		Result<String, double[]> result = null;

		System.out.println(event.getTitle());

		try {
			result = estimate.get();
			// result = ci.applySync(test1, new Allowance[]{});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		System.out.println("Decision of CI: " + result.getValue());
		double[] probabilities = result.getQuality();
		assert (probabilities.length == 2);
		System.out.println("Probability of fake: " + probabilities[0]);
		System.out.println("Probability of real: " + probabilities[1]);
	}

	public static GenericCI<Event, Integer, String, Void, double[]> createCI() {
		Selector<Event, Integer, Void> sel = new AllSelector<Event, Integer, Void>();
		MLWekaNominalConverter<Integer> converter = new intToString();
		MLWekaAggregator<Integer, String, double[]> agg = null;
		try {
			agg = new MLWekaNominalAggregator<Integer>(converter, TRAINING_DATA_LOCATION, new NaiveBayes());
			agg.addFilter(new ClassBalancer());
			Evaluation result = agg.nFoldCrossValidate(10);
			System.out.println("10 Fold Cross Validation Results:");
			System.out.println(result.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// All values are considered OK, no values are considered GOOD. this
		// should force all source to be called.
		MLWekaNominalThresholdAcceptor<String> acc = new MLWekaNominalThresholdAcceptor<String>(0, 1);

		// TODO: Unused sources: CheckOrganizerFBExact; Traning data: time in
		// plausible range is always 0?
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
