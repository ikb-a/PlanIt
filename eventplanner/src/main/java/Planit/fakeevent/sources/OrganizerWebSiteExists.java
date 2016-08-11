package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.DoesURLExist;
import Planit.fakeevent.resources.SourceFactory;

public class OrganizerWebSiteExists extends EventSource {

	DoesURLExist urlCheck;

	public OrganizerWebSiteExists() {
		// urlCheck = new DoesURLExist();
		urlCheck = (DoesURLExist) SourceFactory.getSource(DoesURLExist.class);
	}

	@Override
	protected Integer getResponseOnline(Event e) {
		if (e.getOrganizer() == null || e.getOrganizer().getContactInfo() == null
				|| e.getOrganizer().getContactInfo().getWebsiteURL() == null) {
			return -1;
		}

		return urlCheck.getResponse(e.getOrganizer().getContactInfo().getWebsiteURL());
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
