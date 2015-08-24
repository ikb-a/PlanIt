package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.util.MalformedInputException;
import Planit.fakeevent.util.ParsePhoneNumber;
import Planit.fakeevent.resources.GoogleCoocurrence;
import Planit.fakeevent.resources.SourceFactory;

/**
 * Checks that the event organizer's phone number area code matches
 * the location of the event.
 * @author wginsberg
 *
 */
public class AreaCodeValid extends EventSource {

	public String getName(){
		return "Area-code-valid";
	}
	
	@Override
	protected Integer getResponseOnline(Event e) throws UnknownException{
		
		//try to extract an area code
		String phoneNumber = e.getOrganizer().getContactInfo().getPhoneNumber();
		String areaCode;
		try{
			areaCode = ParsePhoneNumber.getAreaCode(phoneNumber);
		}
		catch (MalformedInputException ex){
			return 0;
		}
		
		//do a coocurrence search for the location of the event and the area code
		String searchFor = String.format("%s area code", e.getVenue().getAddress().getCity());
		GoogleCoocurrence coocur = (GoogleCoocurrence) SourceFactory.getSource(GoogleCoocurrence.class);
		String [] input = {searchFor, areaCode};
		return coocur.getResponse(input);
		
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {

		return null;
	}

	@Override
	public Void getTrust(Event args, Optional<Integer> value) {
		// TODO Auto-generated method stub
		return null;
	}

}
