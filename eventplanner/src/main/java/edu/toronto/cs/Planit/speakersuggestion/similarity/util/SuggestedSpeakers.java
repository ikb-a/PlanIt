package edu.toronto.cs.Planit.speakersuggestion.similarity.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.toronto.cs.Planit.dataObjects.Speaker;

/**
 * An object to hold all information about speakers which will be suggested for an event.
 * @author wginsberg
 *
 */
public class SuggestedSpeakers {

	private Map<Speaker, double []> suggestionQuality;
	
	private List<Speaker> bestSpeakers;
	private List<Speaker> goodSpeakers;
	private List<Speaker> badSpeakers;
	
	public SuggestedSpeakers(){
		suggestionQuality = new HashMap<Speaker, double[]>();
		bestSpeakers = new LinkedList<Speaker>();
		goodSpeakers = new LinkedList<Speaker>();
		badSpeakers = new LinkedList<Speaker>();
	}
	
	/**
	 * Adds a new speaker for suggestion
	 * @param speaker the speaker to suggest
	 * @param qualityOfSuggestion An array of three values denoting the probability that a speaker is "best", "good", or "bad". In the case of a tie, it will be rounded to the next lowest classification
	 * @throws IllegalArgumentException If the supplied array does not have exactly three values
	 */
	public void addSpeaker(Speaker speaker, double [] qualityOfSuggestion) throws IllegalArgumentException{
		
		if (qualityOfSuggestion == null || qualityOfSuggestion.length != 3){
			throw new IllegalArgumentException();
		}
		
		suggestionQuality.put(speaker, qualityOfSuggestion);
		
		if (qualityOfSuggestion[0] > qualityOfSuggestion[1] && qualityOfSuggestion[0] > qualityOfSuggestion[2]){
			bestSpeakers.add(speaker);
		}
		else if (qualityOfSuggestion[1] >= qualityOfSuggestion[0] && qualityOfSuggestion[1] > qualityOfSuggestion[2]){
			goodSpeakers.add(speaker);
		}
		else{
			badSpeakers.add(speaker);
		}
	}
	
	/**
	 * Returns the probability distribution that the speaker is "best", "good", or "bad"
	 */
	public double [] getQualityOfSuggestion(Speaker speaker){
		return suggestionQuality.get(speaker);
	}
	
	public List<Speaker> getBestSpeakers(){
		return bestSpeakers;
	}
	
	public List<Speaker> getGoodSpeakers(){
		return goodSpeakers;
	}
	
	public List<Speaker> getBadSpeakers(){
		return badSpeakers;
	}

	@Override
	public String toString() {
		return "SuggestedSpeakers [bestSpeakers=" + bestSpeakers.toString()
				+ ", goodSpeakers=" + goodSpeakers.toString() + ", badSpeakers="
				+ badSpeakers.toString() + "]";
	}
	
}
