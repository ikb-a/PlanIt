package edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.ci;

import edu.toronto.cs.Planit.dataObjects.Event;

/**
 * An object used to pass a query to sources for grabbing speakers from the internet.
 * @author wginsberg
 *
 */
public class SpeakersQuery {

	protected Event event;
	protected int minSpeakers;
	protected int maxSpeakers;
	
	/**
	 * Create a new naive query where all speakers encountered on the internet will be returned.
	 * @param min
	 * @param max
	 */
	public SpeakersQuery(int min, int max){
		this(null, min, max);
	}
	
	/**
	 * Create a new query where the event is known
	 * @param event
	 * @param minSpeakers
	 * @param maxSpeakers
	 */
	public SpeakersQuery(Event event, int minSpeakers, int maxSpeakers) {
		super();
		this.event = event;
		this.minSpeakers = minSpeakers;
		this.maxSpeakers = maxSpeakers;
	}

	public Event getEvent() {
		return event;
	}

	public int getMinSpeakers() {
		return minSpeakers;
	}

	public int getMaxSpeakers() {
		return maxSpeakers;
	}
	
}
