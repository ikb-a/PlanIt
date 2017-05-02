package PlanIt.monthSuggestion.resources;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import PlanIt.monthSuggestion.sources.Month;
import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.machineLearning.util.MLUtility;
import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import edu.toronto.cs.se.ci.utils.searchEngine.GoogleCSESearchJSON;
import openEval.MultithreadSimpleOpenEval;

/**
 * A singleton class designed to hold 12 OpenEval objects, one for each month of
 * the year. When asked if an event belongs to a specific month, it uses the
 * OpenEval to classify each of the keywords as to whether or not they belong to
 * the month. If most of them do, the month is returned. Else, Unknown is
 * returned.
 * 
 * Search results are memoized for future use. Link Contents are memoized for
 * future use. Training data in the form of .arff files (containing word bags
 * and whether or not they relate to the month) must be provided, currently
 * their location is hard-coded for the sake of simplicity.
 * 
 * @author ikba
 *
 */
public class OpenEvalMonthController {

	/**
	 * The instance of this class
	 */
	private static OpenEvalMonthController instance;
	/**
	 * The open eval object classifying whether keyword/country pairs belong to
	 * January
	 */
	MultithreadSimpleOpenEval jan;
	MultithreadSimpleOpenEval feb;
	MultithreadSimpleOpenEval mar;
	MultithreadSimpleOpenEval apr;
	MultithreadSimpleOpenEval may;
	MultithreadSimpleOpenEval jun;
	MultithreadSimpleOpenEval jul;
	MultithreadSimpleOpenEval aug;
	MultithreadSimpleOpenEval sep;
	MultithreadSimpleOpenEval oct;
	MultithreadSimpleOpenEval nov;
	MultithreadSimpleOpenEval dec;
	
	/**
	 * Map from Event name and keyword to an integer, which is the filename
	 * of the file containing the memoized website contents found when searching
	 * for said event and keyword.
	 */
	HashMap<String, Integer> completedLinks;

	/**
	 * Default number of keywords positively classified by the given month's
	 * openEval required for an event to be classified as related to the given
	 * month.
	 */
	private static final int DEFAULT_THRESHOLD = 1;
	/**
	 * Map from a given month, to it's threshold. Default value is
	 * {@link DEFAULT_THRESHOLD}
	 */
	private Map<Month, Integer> thresholds;
	/**
	 * location of January .arff training data (word bags and true/false)
	 */
	private static final String janFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalJan.arff";
	private static final String febFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalFeb.arff";
	private static final String marFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalMar.arff";
	private static final String aprFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalApr.arff";
	private static final String mayFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalMay.arff";
	private static final String junFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalJun.arff";
	private static final String julFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalJul.arff";
	private static final String augFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalAug.arff";
	private static final String sepFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalSep.arff";
	private static final String octFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalOct.arff";
	private static final String novFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalNov.arff";
	private static final String decFile = "./src/main/resources/data/monthData/OpenEval/YandexTraining/OpenEvalDec.arff";
	/**
	 * Folder at which memoized link contents are to be stored
	 */
	private static final String memoizationFolder = "./src/main/resources/data/monthData/OpenEval/YandexMemoization/";
	/**
	 * File extension for serialized link contents.
	 */
	private static final String fileExtension = ".ser";
	/**
	 * Maps from event name and keyword to filename. The filename is that of a
	 * serialized hashmap from String link name to String link contents. This
	 * map can be used by the openEval objects.
	 */
	private static final String linkToFileNameMapPath = "./src/main/resources/data/monthData/OpenEval/YandexMemoization/linkToFile.ser";
	/**
	 * The search engine used. Cannot be changed after an instance of the
	 * controller exists.
	 */
	static GenericSearchEngine search;
	static boolean verbose = true;
	static boolean verboseOpenEval=false;
	/**
	 * The next available filename. These filenames are used to store memoized
	 * link contents.
	 */
	int currFile;

	private OpenEvalMonthController() {
		try {
			// If no search engine has been set by the user, use the default
			if (search == null) {
				search = new GoogleCSESearchJSON();
			}

			// Set all thresholds to default
			thresholds = new HashMap<Month, Integer>();
			for (Month m : Month.values()) {
				thresholds.put(m, DEFAULT_THRESHOLD);
			}

			if (verbose)
				System.out.println("Training Jan");
			// Train the January open eval
			jan = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(janFile), "January");
			// Give the open eval a unique suffix to it's name
			jan.setNameSuffix("Janthreshold");
			// Set the search engine of the Jan OpenEval
			jan.setSearch(search);
			if (verbose)
				System.out.println("Training Feb");
			feb = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(febFile), "February");
			feb.setNameSuffix("Febthreshold");
			feb.setSearch(search);
			if (verbose)
				System.out.println("Training Mar");
			mar = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(marFile), "March");
			mar.setNameSuffix("Marthreshold");
			mar.setSearch(search);
			if (verbose)
				System.out.println("Training Apr");
			apr = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(aprFile), "April");
			apr.setNameSuffix("Aprthreshold");
			apr.setSearch(search);
			if (verbose)
				System.out.println("Training May");
			may = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(mayFile), "May");
			may.setNameSuffix("Maythreshold");
			may.setSearch(search);
			if (verbose)
				System.out.println("Training Jun");
			jun = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(junFile), "June");
			jun.setNameSuffix("Junthreshold");
			jun.setSearch(search);
			if (verbose)
				System.out.println("Training Jul");
			jul = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(julFile), "July");
			jul.setNameSuffix("Julthreshold");
			jul.setSearch(search);
			if (verbose)
				System.out.println("Training Aug");
			aug = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(augFile), "August");
			aug.setNameSuffix("Augthreshold");
			aug.setSearch(search);
			if (verbose)
				System.out.println("Training Sep");
			sep = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(sepFile), "September");
			sep.setNameSuffix("Septhreshold");
			sep.setSearch(search);
			if (verbose)
				System.out.println("Training Oct");
			oct = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(octFile), "October");
			oct.setNameSuffix("Octthreshold");
			oct.setSearch(search);
			if (verbose)
				System.out.println("Training Nov");
			nov = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(novFile), "November");
			nov.setNameSuffix("Novthreshold");
			nov.setSearch(search);
			if (verbose)
				System.out.println("Training Dec");
			dec = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(decFile), "December");
			dec.setNameSuffix("Decthreshold");
			dec.setSearch(search);
			if (verbose)
				System.out.println("Loading links map");
			completedLinks = loadLinkToFilename();
			currFile = completedLinks.size();
			

			jan.setVerbose(verboseOpenEval);
			feb.setVerbose(verboseOpenEval);
			mar.setVerbose(verboseOpenEval);
			apr.setVerbose(verboseOpenEval);
			may.setVerbose(verboseOpenEval);
			jun.setVerbose(verboseOpenEval);
			jul.setVerbose(verboseOpenEval);
			aug.setVerbose(verboseOpenEval);
			sep.setVerbose(verboseOpenEval);
			oct.setVerbose(verboseOpenEval);
			nov.setVerbose(verboseOpenEval);
			dec.setVerbose(verboseOpenEval);


			System.out.println("Controller setup done");

		} catch (Exception e) {
			throw new RuntimeException(e); //WEKA Exception
		}

	}

	/**
	 * Changes the search engine used from the default to {@code searchEngine}.
	 * Will throw an IllegalStateException if the instance of
	 * OpenEvalMonthController already exists.
	 * 
	 * @param searchEngine
	 */
	public static synchronized void setSearchEngine(GenericSearchEngine searchEngine) {
		if (instance != null) {
			throw new IllegalStateException("Cannot change search after an instance has been created");
		}
		search = searchEngine;
	}

	/**
	 * Enables/disables debugging messages.
	 * 
	 * @param verb
	 */
	public static synchronized void setVerbose(boolean verb) {
		verbose = verb;
	}

	/**
	 * Creates the singleton instance of OpenEvalMonthController. Once called,
	 * the search engine used cannot be changed.
	 * 
	 * @return
	 */
	public static synchronized OpenEvalMonthController getInstance() {
		if (instance == null) {
			instance = new OpenEvalMonthController();
		}
		return instance;
	}

	/**
	 * Sets a new threshold for {@code m}.
	 * @param m
	 * @param threshold
	 */
	public synchronized void setCustomThreshold(Month m, int threshold) {
		assert (m != null);
		thresholds.put(m, threshold);
	}

	public synchronized Month getResponse(Event input, Month month) throws UnknownException {
		if (verbose)
			System.out.println(
					"Running month " + month + " on event: " + input.getTitle() + " Thres: " + thresholds.get(month));

		MultithreadSimpleOpenEval openEval = getEvalForMonth(month);
		int filename;
		if (completedLinks.containsKey(input.getTitle() + openEval.getKeyword())) {
			filename = completedLinks.get(input.getTitle() + openEval.getKeyword());
			if (verbose)
				System.out.println("load from existing: " + memoizationFolder + filename + fileExtension);
		} else {
			filename = currFile;
			completedLinks.put(input.getTitle() + openEval.getKeyword(), currFile);
			//saveLinksMap();
			currFile++;
			if (verbose)
				System.out.println("***Create new: " + memoizationFolder + filename + fileExtension);
		}

		try {
			openEval.setMemoizeLinkContentsOn(memoizationFolder + filename + fileExtension);
		} catch (IOException e1) {
			// should not trigger
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}

		// List<String> keywords = new ArrayList<String>(new
		// HashSet<String>(input.getWords()));
		// String[] definedKeywords = input.getKeyWords();
		// if (definedKeywords != null) {
		// keywords.addAll(Arrays.asList(definedKeywords));
		// definedKeywords = null;
		// }
		List<String> keywords = new ArrayList<String>(Arrays.asList(input.getKeyWords()));

		String country = input.getVenue().getAddress().getCountry();
		if (country == null) {
			throw new UnknownException();
		}
		if (keywords.size() == 0) {
			throw new UnknownException("No Keywords");
		}

		keywords.replaceAll((String a) -> country + " " + a);

		if (verbose) {
			System.out.println(keywords);
		}

		int positiveBags = 0;
		int negativeBags = 0;
		int unkBags = 0;

		List<Opinion<Boolean, Double>> opinions = openEval.getOpinions(keywords);
		// TODO: use weighted vote based on SimpleOpenEval confidences?
		for (int x = 0; x < keywords.size(); x++) {
			String keyword = keywords.get(x);
			Opinion<Boolean, Double> op = opinions.get(x);

			if (op == null) {
				unkBags++;

				if (verbose) {
					System.out.println("Unknown: " + keyword);
				}
			} else {
				if (verbose) {
					System.out.println(keyword + ": " + op);
				}
				if (op.getValue()) {
					positiveBags++;
				} else {
					negativeBags++;
				}
			}
		}

		try {
			if (verbose)
				System.out.println("Saving Link Contents");
			openEval.saveMemoizedContents();
			this.saveLinksMap();
		} catch (Exception e) {
			System.err.println("Failed to save link contents.");
			e.printStackTrace();
		}
		openEval.setMemoizeLinkContentsOff();

		if (verbose) {
			System.out.println(
					"P Keywords: " + positiveBags + " N Keywords: " + negativeBags + " U Keywords: " + unkBags);
		}

		// assert ((positiveBags + negativeBags + unkBags) != 0);
		if (positiveBags >= thresholds.get(month)) {
			if (verbose)
				System.out.println("Classified true");

			return month;
		} else {
			if (verbose)
				System.out.println("Classified unknown");

			throw new UnknownException();
		}
	}

	private MultithreadSimpleOpenEval getEvalForMonth(Month month) {
		switch (month) {
		case January:
			return jan;
		case February:
			return feb;
		case March:
			return mar;
		case April:
			return apr;
		case May:
			return may;
		case June:
			return jun;
		case July:
			return jul;
		case August:
			return aug;
		case September:
			return sep;
		case October:
			return oct;
		case November:
			return nov;
		case December:
			return dec;
		default:
			throw new IllegalArgumentException(month + " is not a month.");
		}
	}

	private HashMap<String, Integer> loadLinkToFilename() throws IOException, ClassNotFoundException {
		File f = new File(linkToFileNameMapPath);
		if (!f.exists()) {
			f.createNewFile();
			return new HashMap<String, Integer>();
		}

		try (FileInputStream fis = new FileInputStream(linkToFileNameMapPath)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> result = (HashMap<String, Integer>) ois.readObject();
			ois.close();
			return result;
		} catch (EOFException e) {
			return new HashMap<String, Integer>();
		}
	}

	private void saveLinksMap() {
		try (FileOutputStream fos = new FileOutputStream(linkToFileNameMapPath)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.completedLinks);
			oos.close();
		} catch (IOException e) {
			// Should not happen
			throw new RuntimeException(e);
		}
	}

	public Expenditure[] getCost(Event args, Month month) throws Exception {
		// int numOfKeywords = args.getWords().size();
		// TODO: Add method for integer multiplication of expenditures
		return getEvalForMonth(month).getCost("country word");
	}

	public String getName(Month month) {
		return getEvalForMonth(month).getName();
	}
}
