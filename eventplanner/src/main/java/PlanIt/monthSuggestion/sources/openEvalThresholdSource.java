package PlanIt.monthSuggestion.sources;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.machineLearning.MLBasicSource;
import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import openEval.SimpleOpenEval;
import weka.core.converters.JSONLoader;

public class openEvalThresholdSource extends MLBasicSource<Event, Month> implements MLMonthSuggestionContract {
	double threshold;
	Month month;
	SimpleOpenEval openEval;
	public static final String KEYWORD = "month";

	/**
	 * Acts as a wrapper for an openEval object. This source trains an OpenEval
	 * object using {@code trainingDataJSON} to determine if a word and country
	 * is related to {@code thisMonth}.
	 * 
	 * @param thisMonth
	 *            The month that this source is checking for similarity with
	 * @param trainingDataJSON
	 *            Training Data created by a SimpleOpenEval which was training
	 *            to determine if "country word" pairs relate to
	 *            {@code thisMonth}.
	 * @param threshold
	 *            The percentage of keywords in the event that must be related
	 *            to {@code thisMonth} for this source to return
	 *            {@code thisMonth} as it's answer.
	 * @throws IOException
	 *             If there is a problem reading the {@code trainingDataJSON}
	 *             File
	 * @throws Exception
	 *             If WEKA has a problem converting the {@code trainingDataJSON}
	 *             JSON into an Instances object.
	 */
	public openEvalThresholdSource(Month thisMonth, String trainingDataJSON, double threshold)
			throws IOException, Exception {
		if (thisMonth == null) {
			throw new IllegalArgumentException("monthName is null");
		} else if (trainingDataJSON == null) {
			throw new IllegalArgumentException("training Data is null");
		} else if (threshold > 1 || threshold < 0) {
			throw new IllegalArgumentException("threshold must be a percentage between 1 and 0");
		}
		this.month = thisMonth;
		this.threshold = threshold;

		JSONLoader jl = new JSONLoader();
		jl.setSource(new File(trainingDataJSON));

		DecimalFormat df = new DecimalFormat("#.000");
		openEval = new SimpleOpenEval(jl.getDataSet(), KEYWORD);
		openEval.setNameSuffix(month + "thresholdOf" + df.format(threshold));
	}

	/**
	 * Acts as a wrapper for an openEval object. This source trains an OpenEval
	 * object using {@code trainingDataJSON} to determine if a word and country
	 * is related to {@code thisMonth}.
	 * 
	 * @param thisMonth
	 *            The month that this source is checking for similarity with
	 * @param trainingDataJSON
	 *            Training Data created by a SimpleOpenEval which was training
	 *            to determine if "country word" pairs relate to
	 *            {@code thisMonth}.
	 * @param threshold
	 *            The percentage of keywords in the event that must be related
	 *            to {@code thisMonth} for this source to return
	 *            {@code thisMonth} as it's answer.
	 * @param search
	 *            The search engine for the SimpleOpenEval to use. NOTE THAT
	 *            THIS OBJECT IS NOT COPIED! If the same search is used for
	 *            multiple of these sources, and the search object is NOT
	 *            threadsafe, then there may be unpredictable behaviour using
	 *            this source in a CI.
	 * @throws IOException
	 *             If there is a problem reading the {@code trainingDataJSON}
	 *             File
	 * @throws Exception
	 *             If WEKA has a problem converting the {@code trainingDataJSON}
	 *             JSON into an Instances object.
	 */
	public openEvalThresholdSource(Month thisMonth, String trainingDataJSON, double threshold,
			GenericSearchEngine search) throws IOException, Exception {
		if (thisMonth == null) {
			throw new IllegalArgumentException("monthName is null");
		} else if (trainingDataJSON == null) {
			throw new IllegalArgumentException("training Data is null");
		} else if (threshold > 1 || threshold < 0) {
			throw new IllegalArgumentException("threshold must be a percentage between 1 and 0");
		}
		this.month = thisMonth;
		this.threshold = threshold;

		JSONLoader jl = new JSONLoader();
		jl.setSource(new File(trainingDataJSON));

		DecimalFormat df = new DecimalFormat("#.000");
		openEval = new SimpleOpenEval(jl.getDataSet(), KEYWORD);
		openEval.setSearch(search);
		openEval.setNameSuffix(month + "WthresholdOf" + df.format(threshold));
	}

	@Override
	public Month getResponse(Event input) throws UnknownException {
		List<String> keywords = input.getWords();
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
		int positiveBags = 0;
		int negativeBags = 0;
		// TODO: use weighted vote based on SimpleOpenEval confidences?
		for (String keyword : keywords) {
			try {
				Opinion<Boolean, Double> op = openEval.getOpinion(country + " " + keyword);
				if (op.getValue()) {
					positiveBags++;
				} else {
					negativeBags++;
				}
			} catch (UnknownException e) {
				negativeBags++;
			}
		}
		assert ((positiveBags + negativeBags) != 0);
		if ((positiveBags / (positiveBags + negativeBags)) >= threshold) {
			return month;
		} else {
			throw new UnknownException();
		}
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		// int numOfKeywords = args.getWords().size();
		// TODO: Add method for integer multiplication of expenditures
		return openEval.getCost("country word");
	}

	@Override
	public String getName() {
		return openEval.getName();
	}

}
