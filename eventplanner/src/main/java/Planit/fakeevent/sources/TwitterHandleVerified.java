package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.GoogleCoocurrence;
import Planit.fakeevent.resources.SourceFactory;

public class TwitterHandleVerified extends EventSource {

	/**
	 * Does a search for the organizer's twitter handle and then checks if the
	 * organizer's twitter URL appears in the search results.
	 */
	@Override
	protected Integer getResponseOnline(Event e) throws UnknownException {
		if (e.getOrganizer() == null || e.getOrganizer().getContactInfo() == null
				|| e.getOrganizer().getContactInfo().getTwitterHandle() == null
				|| e.getOrganizer().getContactInfo().getTwitterURL() == null) {
			return -1;
		}

		String searchFor = e.getOrganizer().getContactInfo().getTwitterHandle();
		String[] terms = { searchFor, e.getOrganizer().getContactInfo().getTwitterURL() };
		GoogleCoocurrence coocur = (GoogleCoocurrence) SourceFactory.getSource(GoogleCoocurrence.class);
		return coocur.getResponse(terms);
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
