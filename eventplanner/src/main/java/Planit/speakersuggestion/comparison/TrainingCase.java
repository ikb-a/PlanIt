package Planit.speakersuggestion.comparison;

import java.util.List;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;

public class TrainingCase {

	private Event event;
	private List<Speaker> speakers;
	public TrainingCase(Event event, List<Speaker> speakers) {
		super();
		this.event = event;
		this.speakers = speakers;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public List<Speaker> getSpeakers() {
		return speakers;
	}
	public void setSpeakers(List<Speaker> speakers) {
		this.speakers = speakers;
	}
	
	
}
