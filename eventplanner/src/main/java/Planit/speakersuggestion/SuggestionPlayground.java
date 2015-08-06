package Planit.speakersuggestion;

import java.util.concurrent.TimeUnit;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class SuggestionPlayground {

	final static String arffLocation = "src/main/resources/speaker suggestion/dataset/_dataset.arff";
	
	public static void main(String[] args) throws Exception {

		SpeakerSuggestor suggestor = SpeakerSuggestor.getInstance();
		Event event;
		Allowance [] budget;
		SuggestedSpeakers speakers;
		
		event = Event.createEvent("New Summer Drop In Classes").setDescription("We make it easy for you to add more dance to your life. Drop-in classes are designed to let you choose when you want to dance and what class you want to take. Drop in to any of these classes using our 5-class card, 10-class card, 20-class card, or monthly unlimited memberships. Take advantage of our great introductory offer for new students where you can attend unlimited classes for two weeks for only $50. No need to sign up, call ahead, or prepay. Just show up and dance. We have added new drop-in classes for summer! They are being added throughout May and June, and are only offered for the summer season. Check them out now! Beginner Pointe w/ Caroline - starts Tues May 5 (3:00pm-4:00pm) Waacking (Beg/Int) w/ Jennalee - starts Tues May 5 (5:00pm-6:00pm) Jazz Choreo (Beg/Int) w/ Kate - starts Thurs May 7 (5:00pm-6:00pm) Tap Technique w/ Jerome - starts Wed May 13 (4:00pm-5:00pm) Tap Technique w/ Dianne - starts Fri May 15 (5:00pm-6:00pm) House w/ Raoul - starts Tues June 9 (9:00pm-10:00pm) Stiletto Burlesque (Beg/Int) w/ Shawn - starts Thurs June 11 (8:30pm-10:00pm) AfroBeats w/ Greg Samba - starts Fri June 12 (7:30pm-8:30pm) Urban Freestyle w/ Jennalee - starts Fri June 12 (7:30pm-8:30pm) Party Moves w/ KJ McKnight - starts Sat June 13 (11:00am-12:00pm) Zumba w/ Dione Mason - starts Sat June 13 (1:45pm-2:45pm) Dancehall w/ Mikhail - starts Sat June 13 (4:00pm-5:00pm) Beginner Pointe w/ Sarah - starts Sat June 21 (4:00pm-5:00pm) Be sure to keep checking our website for schedule updates, and for detailed descriptions of our classes and instructors. See you in the studio! City Dance Corps. 489 Queen St West. Toronto ON. 416-260-2356. info@citydancecorps.com http://www.citydancecorps.com ");
		budget = new Allowance [] {new Time(1, TimeUnit.SECONDS), new Time(10, TimeUnit.SECONDS), new Time(1, TimeUnit.MINUTES)};
		speakers = suggestor.suggestSpeakers(event, 1, 5, budget);
		
		System.out.println(speakers.prettyPrintSuggestion(10));
				
	}

}
