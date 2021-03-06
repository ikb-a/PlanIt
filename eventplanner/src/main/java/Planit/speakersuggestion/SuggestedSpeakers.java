package Planit.speakersuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Planit.dataObjects.Speaker;

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
		bestSpeakers = new ArrayList<Speaker>();
		goodSpeakers = new ArrayList<Speaker>();
		badSpeakers = new ArrayList<Speaker>();
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
			addSpeakerToList(bestSpeakers, speaker);
		}
		else if (qualityOfSuggestion[1] >= qualityOfSuggestion[0] && qualityOfSuggestion[1] > qualityOfSuggestion[2]){
			addSpeakerToList(goodSpeakers, speaker);
		}
		else{
			addSpeakerToList(badSpeakers, speaker);
		}
	}

	/**
	 * Add a speaker to the supplied list such that the quality of the suggestion will determine its position in the list.
	 * This assumes the speaker, and all speakers in the list are in the suggestion quality map.
	 * @param list
	 * @param speaker
	 * @return false if the assumptions did not hold
	 */
	private boolean addSpeakerToList(List<Speaker> list, Speaker speaker){
		
		double [] quality = suggestionQuality.get(speaker);
		
		if (quality == null) return false;
		
		int listClass = -1;
		if (list == bestSpeakers) listClass = 0;
		else if (list == goodSpeakers) listClass = 1;
		else listClass = 2;
		
		if (listClass == -1) return false;
		
		boolean greaterThanSome = false;
		int i = 0;
		for (; i < list.size(); i++){
			
			double [] otherQualityDist = suggestionQuality.get(list.get(i));
			if (otherQualityDist == null){
				return false;
			}
			double otherQ = otherQualityDist[listClass];
			
			if (quality[listClass] > otherQ){
				greaterThanSome = true;
				break;
			}
		}
		
		if (greaterThanSome){
			list.add(i, speaker);
		}
		else{
			list.add(speaker);
		}
		
		return true;
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
		return String.format("SuggestedSpeakers:\n		Best : %s\n		Good : %s\n		Bad : %s\n", bestSpeakers.toString(), goodSpeakers.toString(), badSpeakers.toString());
	}

	/**
	 * Returns a string of the n best suggested speakers with their details with readable formatting.
	 * @param n The number of speakers to print
	 */
	public String prettyPrintSuggestion(int n ){
		
		StringBuilder sb = new StringBuilder();
		sb.append("The best speakers to suggest :\n\n");
		
		//print the best speakers
		for (int i = 0; i < Math.min(n, bestSpeakers.size()); i++){
			Speaker s = bestSpeakers.get(i);
			sb.append(String.format("%s\n%s\n%s\n\n", s.getName(), s.getProfessionalTitle(), s.getTruncatedBio(140)));
		}
		
		//print the less good speakers if we ran out of the best ones
		if (bestSpeakers.size() < n){
			sb.append("\nSome other speakers to suggest :\n");
			int remaining = Math.min(goodSpeakers.size(), n - bestSpeakers.size());
			for (int i = 0; i < remaining; i++){
				Speaker s = goodSpeakers.get(i);
				sb.append(String.format("%s\n%s\n%s\n\n", s.getName(), s.getProfessionalTitle(), s.getTruncatedBio(140)));
			}	
		}
		
		return sb.toString();
	}
}
