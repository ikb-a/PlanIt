package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.selectors.AllSelector;
import edu.toronto.cs.Planit.ci.ml.Trust;

/**
 * For this implementation to be up to date it will need source adapters to turn numeric sources into nominal sources
 */

/**
 * Holds the contributional implementation of the similarity part of the speaker suggestion feature
 * @author wginsberg
 *
 */
public class EventAndSpeakerRelevance {
	
	private CI<ComparisonRequest, Similarity, Trust<ComparisonRequest, Similarity>, Double> ci;
	
	private Collection<Speaker> low;
	private Collection<Speaker> medium;
	private Collection<Speaker> high;
	
	/**
	 * Does comparisons for each speaker on the event and returns the most similar speakers.
	 */
	public Collection<Speaker> compare(Event event, List<Speaker> speakers, Allowance [] budget) throws InterruptedException, ExecutionException{
		
		getCI();
		low = new ArrayList<Speaker>();
		medium = new ArrayList<Speaker>();
		high = new ArrayList<Speaker>();
		
		for (Speaker speaker : speakers){
			ComparisonRequest request = new ComparisonRequest(event, speaker);
			Similarity result;
			try {
				result = ci.applySync(request, budget).getValue();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
				return null;
			}
			Similarity classification = result;
			switch (classification.getNominal()){
			case "LOW":
				low.add(speaker);
				break;
			case "MEDIUM":
				medium.add(speaker);
				break;
			case "HIGH":
				high.add(speaker);
				break;
			}
		}
		
		return high;
	}
	
	private CI<ComparisonRequest, Similarity, Trust<ComparisonRequest, Similarity>, Double> getCI(){
		if (ci == null){
			ci = new CI<ComparisonRequest, Similarity, Trust<ComparisonRequest, Similarity>, Double>
			(NumericSimilarityContract.class, new HardCodedAggregator(), new AllSelector<ComparisonRequest, Similarity, Trust<ComparisonRequest, Similarity>>());
		}
		return ci;
	}
	
	/**
	 * Returns the least relevant speakers to the event.
	 * @throws IllegalStateException if no comparison has been made
	 */
	public Collection<Speaker> getLeastRelevant() throws IllegalStateException{
		if (low == null){
			throw new IllegalStateException();
		}
		return low;
	}
	
	/**
	 * Returns speakers who are only somewhat relevant to the event.
	 * @throws IllegalStateException if no comparison has been made
	 */
	public Collection<Speaker> getSomeWhatRelevent() throws IllegalStateException{
		if (medium == null){
			throw new IllegalStateException();
		}
		return medium;
	}
	
	/**
	 * Returns the most relevant speakers to the event, the same as the return value of compare().
	 * @throws IllegalStateException if no comparison has been made
	 */
	public Collection<Speaker> getMostRelevant() throws IllegalStateException{
		if (high == null){
			throw new IllegalStateException();
		}
		return high;
	}
	
}
