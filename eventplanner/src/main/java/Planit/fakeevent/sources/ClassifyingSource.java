package Planit.fakeevent.sources;

import java.util.HashMap;
import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;

/**
 * This a source to be used for the classifying attribute of a relation. Before
 * invoking the source, instances can be classified, and then when they are
 * invoked on, the response will be their classification.
 * 
 * @author wginsberg
 *
 */
public class ClassifyingSource extends EventSource {

	private HashMap<Event, Integer> classification;

	public ClassifyingSource(String classifyingAttributeName) {
		classification = new HashMap<Event, Integer>();
	}

	public void classify(List<Event> instances, int category) {
		for (int i = 0; i < instances.size(); i++) {
			classification.put(instances.get(i), category);
		}
	}

	public void classify(Event event, int category) {
		classification.put(event, category);
	}

	/**
	 * Return the event's classification, or -1 if it is unknown
	 */
	@Override
	public Integer getResponseOnline(Event input) {
		if (classification != null) {
			if (classification.containsKey(input)) {
				return classification.get(input);
			}
		}
		return -1;
	}

	/**
	 * Since this is a source for classifying events, we use the same response
	 * as if we were online.
	 */
	@Override
	public Integer getResponseOffline(Event e) {
		return getResponseOnline(e);
	}

	@Override
	public String getName() {
		return "class";
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {

		return new Expenditure[] {};
	}

	@Override
	public Void getTrust(Event args, Optional<Integer> value) {

		return null;
	}

}
