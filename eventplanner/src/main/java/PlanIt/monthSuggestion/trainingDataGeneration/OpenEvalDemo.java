package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.File;
import java.util.logging.Level;

import PlanIt.monthSuggestion.resources.OpenEvalMonthController;
import PlanIt.monthSuggestion.sources.Month;
import PlanIt.monthSuggestion.sources.openEvalThresholdSource;
import PlanIt.monthSuggestion.sources.openEvalThresholdSourceSingleThread;
import Planit.dataObjects.Event;
import Planit.dataObjects.util.EventExtractor;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import edu.toronto.cs.se.ci.utils.searchEngine.MemoizingSearch;

public class OpenEvalDemo {

	public static void main(String[] args) throws Exception {
		// disable annoying HTMLUnit messages produced by UnBubble
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		// Create search engine whose search results are being memoized to the file
		GenericSearchEngine bob = new MemoizingSearch(
				"./src/main/resources/data/monthData/OpenEval/OpenEvalDemoDec2016SearchResults.ser", new UnBubbleSearchHTML());

		EventExtractor extractor = new EventExtractor();
		String CIFile = "./src/main/resources/data/monthData/CI/1_January.json";
		// int index = Integer.parseInt(args[0]);
		int index = 0;
		Event[] events = extractor.extractEventsFromJsonFile(new File(CIFile));
		
		extractor = null;
		
		Event one = events[index];
		System.out.println("Running on element " + index + " of: " + CIFile);
		System.out.println("January:");
		System.out.println(one.getTitle());
		System.out.println(one.getDescription());

		boolean useMultiThread = true;
		if (useMultiThread) {

			OpenEvalMonthController.setSearchEngine(bob);
			OpenEvalMonthController.setVerbose(true);
			openEvalThresholdSource b = new openEvalThresholdSource(Month.January);

			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nFebruary:");
			b = new openEvalThresholdSource(Month.February);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMarch:");
			b = new openEvalThresholdSource(Month.March);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nApril:");
			b = new openEvalThresholdSource(Month.April);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMay:");
			b = new openEvalThresholdSource(Month.May);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJune:");
			b = new openEvalThresholdSource(Month.June);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJuly:");
			b = new openEvalThresholdSource(Month.July);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nAugust:");
			b = new openEvalThresholdSource(Month.August);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nSeptember:");
			b = new openEvalThresholdSource(Month.September);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nOctober:");
			b = new openEvalThresholdSource(Month.October);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nNovember:");
			b = new openEvalThresholdSource(Month.November);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nDecember:");
			b = new openEvalThresholdSource(Month.December);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}
		} else {

			openEvalThresholdSourceSingleThread b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalJan.arff", 0.25, bob);

			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nFebruary:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalFeb.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMarch:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalMar.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nApril:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalApr.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMay:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalMay.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJune:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalJun.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJuly:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalJul.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nAugust:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalAug.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nSeptember:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalSep.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nOctober:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalOct.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nNovember:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalNov.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nDecember:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalDec.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}
		}
		System.out.println(
				"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

}
