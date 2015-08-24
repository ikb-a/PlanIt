package Planit.speakersuggestion.comparison;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;

public class SpeakerComparisonRequest {

	private Event event;
	private Speaker s1;
	private Speaker s2;
	
	public SpeakerComparisonRequest(Event event, Speaker s1, Speaker s2) {
		super();
		this.event = event;
		this.s1 = s1;
		this.s2 = s2;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Speaker getS1() {
		return s1;
	}

	public void setS1(Speaker s1) {
		this.s1 = s1;
	}

	public Speaker getS2() {
		return s2;
	}

	public void setS2(Speaker s2) {
		this.s2 = s2;
	}
	
}
