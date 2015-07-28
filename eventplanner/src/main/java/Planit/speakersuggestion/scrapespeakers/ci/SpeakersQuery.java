package Planit.speakersuggestion.scrapespeakers.ci;

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
	
}
