package Planit.speakersuggestion.scrapespeakers.util;

import java.util.List;

/**
 * An object used to pass a query to sources for grabbing speakers from the internet.
 * As input it requires a list of keywords to search for, as well as the desired number of speakers to return with.
 * @author wginsberg
 *
 */
public class SpeakersQuery {

	protected List<String> keywords;
	protected int minSpeakers;
	protected int maxSpeakers;
	
	/**
	 * Create a new naive query where all speakers encountered on the internet will be returned.
	 * @param min
	 * @param max
	 */
	@Deprecated
	public SpeakersQuery(int min, int max){
		this(null, min, max);
	}
	
	/**
	 * Create a new query where the event is known
	 * @param event
	 * @param minSpeakers
	 * @param maxSpeakers
	 */
	public SpeakersQuery(List<String> keywords, int minSpeakers, int maxSpeakers) {
		super();
		this.keywords = keywords;
		this.minSpeakers = minSpeakers;
		this.maxSpeakers = maxSpeakers;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public int getMinSpeakers() {
		return minSpeakers;
	}

	public int getMaxSpeakers() {
		return maxSpeakers;
	}

	public void setMinSpeakers(int minSpeakers) {
		this.minSpeakers = minSpeakers;
	}

	public void setMaxSpeakers(int maxSpeakers) {
		this.maxSpeakers = maxSpeakers;
	}
	
	/**
	 * Returns the min divided by the number of keywords
	 * @return
	 */
	public int minPerKeyword(){
		int returnValue = 1;
		if (keywords.size() != 0){
			returnValue = minSpeakers / keywords.size();
		}
		return Math.min(1, returnValue);
	}
	
	/**
	 * Returns the max divided by the number of keywords
	 * @return
	 */
	public int maxPerKeyword(){
		int returnValue = 1;
		if (keywords.size() != 0){
			returnValue = maxSpeakers / keywords.size();
		}
		return Math.max(1, returnValue);
	}
}
