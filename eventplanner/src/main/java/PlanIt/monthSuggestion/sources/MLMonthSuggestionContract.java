package PlanIt.monthSuggestion.sources;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.machineLearning.MLContract;

/**
 * Sources implementing this contract must be sources which take an Event
 * object, and recommend the best month for this event.
 * 
 * @author Ian Berlot-Attwell
 *
 */
public interface MLMonthSuggestionContract extends MLContract<Event, Month> {

}
