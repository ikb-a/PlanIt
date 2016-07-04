package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.DoesURLExist;
import Planit.fakeevent.resources.SourceFactory;

/**
 * Checks that the Facebook URL supplied as the organizer's facebook page
 * exists.
 * 
 * @author wginsberg
 *
 */
public class OrganizerFaceBookExists extends EventSource {

	DoesURLExist urlChecker;

	public OrganizerFaceBookExists() {
		// urlChecker = new DoesURLExist();
		urlChecker = (DoesURLExist) SourceFactory.getSource(DoesURLExist.class);
	}

	@Override
	protected Integer getResponseOnline(Event e) {
		return urlChecker.getResponse(e.getOrganizer().getContactInfo().getFacebookURL());
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
