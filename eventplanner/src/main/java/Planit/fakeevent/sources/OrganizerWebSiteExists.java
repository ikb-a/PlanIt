package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.DoesURLExist;
import Planit.fakeevent.resources.SourceFactory;

public class OrganizerWebSiteExists extends EventSource {

	DoesURLExist urlCheck;
	
	public OrganizerWebSiteExists() {
		//urlCheck = new DoesURLExist();
		urlCheck = (DoesURLExist) SourceFactory.getSource(DoesURLExist.class);
	}
	
	@Override
	public String getName(){
		return "Organizer website exists";
	}
	
	@Override
	protected Integer getResponseOnline(Event e) {
		return urlCheck.getResponse(e.getOrganizer().getContactInfo().getWebsiteURL());
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(Event args, Optional<Integer> value) {
		// TODO Auto-generated method stub
		return null;
	}

}
