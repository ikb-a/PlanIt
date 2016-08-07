package PlanIt.monthSuggestion.resources;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

public class OpenEvalMonthController {

	private static OpenEvalMonthController instance;
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
	HashMap<String, Integer> completedLinks;

	private static final double DEFAULT_THRESHOLD = 0.25;
	private Map<Month, Double> thresholds;
	private static final String KEYWORD = "month";
	private static final String janFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalJan.arff";
	private static final String febFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalFeb.arff";
	private static final String marFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalMar.arff";
	private static final String aprFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalApr.arff";
	private static final String mayFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalMay.arff";
	private static final String junFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalJun.arff";
	private static final String julFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalJul.arff";
	private static final String augFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalAug.arff";
	private static final String sepFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalSep.arff";
	private static final String octFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalOct.arff";
	private static final String novFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalNov.arff";
	private static final String decFile = "./src/main/resources/data/monthData/OpenEval/OpenEvalDec.arff";
	private static final String memoizationFolder = "./src/main/resources/data/monthData/OpenEval/memoization/";
	private static final String fileExtension = ".ser";
	private static final String linkToFileNameMapPath = "./src/main/resources/data/monthData/OpenEval/memoization/linkToFile.ser";
	static GenericSearchEngine search;
	boolean verbose = true;
	int currFile;

	private OpenEvalMonthController() {
		try {
			if (search == null) {
				search = new GoogleCSESearchJSON();
			}

			thresholds = new HashMap<Month, Double>();
			for (Month m : Month.values()) {
				thresholds.put(m, DEFAULT_THRESHOLD);
			}

			jan = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(janFile), KEYWORD);
			jan.setNameSuffix("Janthreshold");
			jan.setSearch(search);

			feb = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(febFile), KEYWORD);
			feb.setNameSuffix("Febthreshold");
			feb.setSearch(search);

			mar = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(marFile), KEYWORD);
			mar.setNameSuffix("Marthreshold");
			mar.setSearch(search);

			apr = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(aprFile), KEYWORD);
			apr.setNameSuffix("Aprthreshold");
			apr.setSearch(search);

			may = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(mayFile), KEYWORD);
			may.setNameSuffix("Maythreshold");
			may.setSearch(search);

			jun = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(junFile), KEYWORD);
			jun.setNameSuffix("Junthreshold");
			jun.setSearch(search);

			jul = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(julFile), KEYWORD);
			jul.setNameSuffix("Julthreshold");
			jul.setSearch(search);

			aug = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(augFile), KEYWORD);
			aug.setNameSuffix("Augthreshold");
			aug.setSearch(search);

			sep = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(sepFile), KEYWORD);
			sep.setNameSuffix("Septhreshold");
			sep.setSearch(search);

			oct = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(octFile), KEYWORD);
			oct.setNameSuffix("Octthreshold");
			oct.setSearch(search);

			nov = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(novFile), KEYWORD);
			nov.setNameSuffix("Novthreshold");
			nov.setSearch(search);

			dec = new MultithreadSimpleOpenEval(MLUtility.fileToInstances(decFile), KEYWORD);
			dec.setNameSuffix("Decthreshold");
			dec.setSearch(search);

			completedLinks = loadLinkToFilename();
			currFile = completedLinks.size();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static synchronized void setSearchEngine(GenericSearchEngine searchEngine) {
		if (instance != null) {
			throw new IllegalStateException("Cannot change search after an instance has been created");
		}
		search = searchEngine;
	}

	public static synchronized OpenEvalMonthController getInstance() {
		if (instance == null) {
			instance = new OpenEvalMonthController();
		}
		return instance;
	}

	public synchronized void setCustomThreshold(Month m, double threshold) {
		assert (m != null);
		thresholds.put(m, threshold);
	}

	public synchronized Month getResponse(Event input, Month month) throws UnknownException {
		MultithreadSimpleOpenEval openEval = getEvalForMonth(month);
		int filename;
		if (completedLinks.containsKey(input.getTitle())) {
			filename = completedLinks.get(input.getTitle());
		} else {
			filename = currFile;
			completedLinks.put(input.getTitle(), currFile);
			saveLinksMap();
			currFile++;
		}

		try {
			openEval.setMemoizeLinkContentsOn(memoizationFolder + filename + fileExtension);
		} catch (IOException e1) {
			// should not trigger
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}

		List<String> keywords = new ArrayList<String>(new HashSet<String>(input.getWords()));
		List<String> definedKeywords = input.getKeyWords();
		if (definedKeywords != null) {
			keywords.addAll(definedKeywords);
			definedKeywords = null;
		}
		String country = input.getVenue().getAddress().getCountry();
		if (country == null) {
			throw new UnknownException();
		}
		if (keywords.size() == 0) {
			throw new UnknownException();
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
		} catch (Exception e) {
			System.err.println("Failed to save link contents.");
			e.printStackTrace();
		}
		openEval.setMemoizeLinkContentsOff();

		if (verbose) {
			System.out.println(
					"P Keywords: " + positiveBags + " N Keywords: " + negativeBags + " U Keywords: " + unkBags);
		}

		assert ((positiveBags + negativeBags + unkBags) != 0);
		if ((positiveBags / (positiveBags + negativeBags + unkBags)) >= thresholds.get(month)) {
			return month;
		} else {
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
