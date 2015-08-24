package Planit.fakeevent.sources;

import Planit.fakeevent.resources.FBProfileExactlyExists;
import Planit.fakeevent.resources.SourceFactory;

/**
 * Checks that the exact name of the organizer exists on facebook
 * @author wginsberg
 *
 */
public class CheckOrganizerFBExact extends CheckOrganizerFB {

	public CheckOrganizerFBExact(){
		//facebook = new FBProfileExactlyExists();
		facebook = (FBProfileExactlyExists) SourceFactory.getSource(FBProfileExactlyExists.class);
	}
	
	@Override
	public String getName(){
		return "Organization name (exactly) is on facebook";
	}
	
}
