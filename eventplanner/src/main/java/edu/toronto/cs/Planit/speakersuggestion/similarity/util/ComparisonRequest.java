package edu.toronto.cs.Planit.speakersuggestion.similarity.util;

import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;

/**
 * An instance of ComparisonRequest holds two Comparable objects, with the intent that they will be compared to each other.
 * @author wginsberg
 *
 */
public class ComparisonRequest{

	private Event first;
	private Speaker second;
	
	public ComparisonRequest (Event first, Speaker second){
		this.first = first;
		this.second = second;
	}

	/* (non-Javadoc)
	 * @see edu.toronto.cs.Planit.speakerSuggestion.similarity.ComparisonRequest#getFirst()
	 */
	public Event getEvent() {
		return first;
	}

	/* (non-Javadoc)
	 * @see edu.toronto.cs.Planit.speakerSuggestion.similarity.ComparisonRequest#getSecond()
	 */
	public Speaker getSpeaker() {
		return second;
	}
	
	@Override
	public String toString(){
		return String.format("<%s, %s>", getEvent().toString(), getSpeaker().toString());
	}
	
}
