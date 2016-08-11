package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.FBProfileLooselyExists;
import Planit.fakeevent.resources.SourceFactory;

/**
 * Checks that the name of the organizer exists on facebook.
 * 
 * @author wginsberg
 *
 */
public class CheckOrganizerFB extends EventSource {

	protected FBProfileLooselyExists facebook;

	public CheckOrganizerFB() {
		// facebook = new FBProfileLooselyExists();
		facebook = (FBProfileLooselyExists) SourceFactory.getSource(FBProfileLooselyExists.class);
	}

	@Override
	public Integer getResponseOnline(Event e) {
		if (e.getOrganizer() == null || e.getOrganizer().getName() == null) {
			return -1;
		}

		try {
			facebook.allowNonPeople = true;
			return facebook.getResponse(e.getOrganizer().getName());
		} catch (Exception ex) {
			return -1;
		}
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
