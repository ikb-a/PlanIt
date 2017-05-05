package Planit.fakeevent.sources;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.Contract;

/**
 * This contract signifies that the source will accept an Event, and return
 * either 0 (the event is fake), 1 (the event is real), or -1 (Unknown).
 */
public interface EventCheckContract extends Contract<Event, Integer, Void> {

}
