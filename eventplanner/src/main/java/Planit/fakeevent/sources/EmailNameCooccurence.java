package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.GoogleCoocurrence;
import Planit.fakeevent.resources.SourceFactory;

/**
 * An event source for checking that an event organizer's email and name
 * co-occur in google searches.
 * 
 * @author wginsberg
 *
 */
public class EmailNameCooccurence extends EventSource {

	static protected int MAX_RESULTS_TO_CHECK = 10;

	/**
	 * Returns 1 if a google search for the event organizer's email address
	 * returns results which contain the event organizer's name.
	 * 
	 * @throws UnknownException
	 */
	@Override
	protected Integer getResponseOnline(Event e) throws UnknownException {

		GoogleCoocurrence coocurrence = (GoogleCoocurrence) SourceFactory.getSource(GoogleCoocurrence.class);

		String email = e.getOrganizer().getContactInfo().getEmailAddress();
		String name = e.getOrganizer().getName();
		String[] emailAndName = { email, name };

		return coocurrence.getResponse(emailAndName);
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
