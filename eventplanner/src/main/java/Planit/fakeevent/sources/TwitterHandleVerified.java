package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.GoogleCoocurrence;
import Planit.fakeevent.resources.SourceFactory;

public class TwitterHandleVerified extends EventSource {

	@Override
	public String getName(){
		return "Twitter-handle-matches-twitter-url";
	}
	
	/**
	 * Does a search for the organizer's twitter handle and then
	 * checks if the organizer's twitter URL appears in the search results.
	 */
	@Override
	protected Integer getResponseOnline(Event e) throws UnknownException{
		String searchFor = e.getOrganizer().getContactInfo().getTwitterHandle();
		String [] terms = {searchFor, e.getOrganizer().getContactInfo().getTwitterURL()};
		GoogleCoocurrence coocur = (GoogleCoocurrence) SourceFactory.getSource(GoogleCoocurrence.class);
		return coocur.getResponse(terms);
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
