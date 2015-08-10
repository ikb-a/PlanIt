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
		
		event = Event.createEvent("Music Mondays in the Park").setDescription("Meet friends and neighbours in the part to listen to great music on late summer afternoons. Bring your picnic blanket or lawn chair and come ready to be entertained! ");
		//event = Event.createEvent("Heavenly Food Trucks at South Gate Church").setDescription("It's food truck frenzy at South Gate Church every Monday night this July and August (except Aug. 3) from 4:30 - 8 pm. Join us for this divine event! ");
		//event = Event.createEvent("CONNECT GRAD SHOW SUMMER 2015").setDescription("Learn more: connectgradshow.com HUMBER COLLEGE, (LAKE SHORE CAMPUS) 19 Colonel Samuel Smith Park Dr  Etobicoke, ON M8V 4B6 August 10th, 2015 @ 4:30PM–8PM Join us for our semi-annual Graphic Design for Print & Web and Web Design, Development & Maintenance Portfolio Show on Monday, August 10th 2015 at Humber College’s Lake Shore campus in room L1017 located in the L-Building between 4:30pm–8pm. This event is open to industry guests, alumni, faculty and potential students of the GDPW and WDDM programs. This is a great opportunity for our graduates to make connections with industry, alumni and faculty as they as they embark on their careers. We would love for you to join us in making this an epic day for our recent graduates. ");
		//event = Event.createEvent("Annual Messy Church Vacation Bible Club: Everest - Conquering Challenges").setDescription("2nd Annual Messy Church (For the whole family!)Vacation Bible Club: Everest - Conquering Challenges August 10-14, 2015 * 5-7pm (Supper included!) Christ Church St. James, 194 Park Lawn Road, Etobicoke Each day, kids participate in small groups, called Climbing Crews, and discover practical ways to hold on to God’s mighty power! Not only will they experience exciting Bible adventures but they’ll also watch for God in everyday life through something called God Sightings™. You and your kids will discover that God is active in our lives and that His fingerprints are everywhere! Everest is filled with epic Bible-learning experiences kids will see, hear, touch, and even taste! KidVid Cinema shares real stories of real kids who rely on God’s power to face life’s challenges! Plus, team-building games, cool Bible songs, and tasty treats keep everyone on the move.To Register : Call the church office at 416 251 8711 or go online at http://www.christchurchstjames.ca/ ");		
		//event = Event.createEvent("15th annual Canadian Summer School on Quantum Information: Aug 10-14").setDescription("A week prior to the 6th conference for Quantum Information and Quantum Control (QIQC), the Fields Institute will be running the 15th annual Canadian Summer School on Quantum Information. Further information is available here. ");
		//event = Event.createEvent("Equinox Run Club").setDescription("JOIN US FOR EQUINOX RUN CLUBNo matter your goal, this workout inspires more from your run. Great for the marathoner or the purist who just enjoys to run, improve your stride, cardiovascular endurance, and deliver the physical and inner strength necessary to achieve your personal goals! Created by Wes Perdesen.EVERY MONDAY'S STARTING @ 5:00pm AUGUST 10TH THROUGH SEPTEMBER 28TH ");

		
		budget = new Allowance [] {new Time(1, TimeUnit.SECONDS), new Time(10, TimeUnit.SECONDS), new Time(1, TimeUnit.MINUTES)};
		speakers = suggestor.suggestSpeakers(event, 1, 5, budget);
		
		System.out.println(speakers.prettyPrintSuggestion(10));
				
	}

}
