package Planit.fakeevent.ci;

import java.util.ArrayList;

import Planit.fakeevent.sources.EventSource;
import Planit.dataObjects.*;

/**
 * A class which can invoke a set of EventSources on a set of event objects in
 * order to get opinion data on a set of events.
 * 
 * @author wginsberg
 *
 */
public class EventSourceInvoker extends Invoker<EventSource, Event, Integer> {

	private ArrayList<Event> events;

	/**
	 * Create a new EventSourceInvoker with initial events and sources
	 * 
	 * @param sources
	 *            - possibly null collection of event sources
	 * @param events
	 *            - possibly null collection of events
	 */
	public EventSourceInvoker(String name, ArrayList<EventSource> sources, ArrayList<Event> events) {
		super(name);
		if (sources != null) {
			this.sources = sources;
		}
		if (events != null) {
			this.events = events;
		}
	}

	public EventSourceInvoker(String name, ArrayList<EventSource> sources, Event[] events) {
		super(name);
		if (sources != null) {
			this.sources = sources;
		}
		if (events != null) {
			setEvents(events);
		}
	}

	/**
	 * Invoke the sources which have been set in this instance
	 */
	public void invoke() {
		invoke(sources, events);
	}

	/**
	 * Invoke sources on a single event with pre-set sources
	 * 
	 * @param e
	 *            - Event object
	 */
	public Integer[] invoke(Event e) {

		return invoke(sources, e);

	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public void setEvents(Event[] events) {
		ArrayList<Event> eventList = new ArrayList<Event>(events.length);
		for (int i = 0; i < events.length; i++) {
			eventList.add(events[i]);
		}
		this.events = eventList;
	}

	/**
	 * Returns the string "{-1, 0, 1}"
	 */
	@Override
	protected String getSourceArffType() {
		/*
		 * We are using a ternary opinion system where True, False, Unknown are
		 * 1, 0, -1, respectively
		 */
		return "{-1, 0, 1}";
	}

}
