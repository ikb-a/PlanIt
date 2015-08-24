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
		
		//event = Event.createEvent("Music Mondays in the Park").setDescription("Meet friends and neighbours in the part to listen to great music on late summer afternoons. Bring your picnic blanket or lawn chair and come ready to be entertained! ");
		//event = Event.createEvent("Heavenly Food Trucks at South Gate Church").setDescription("It's food truck frenzy at South Gate Church every Monday night this July and August (except Aug. 3) from 4:30 - 8 pm. Join us for this divine event! ");
		//event = Event.createEvent("CONNECT GRAD SHOW SUMMER 2015").setDescription("Learn more: connectgradshow.com HUMBER COLLEGE, (LAKE SHORE CAMPUS) 19 Colonel Samuel Smith Park Dr  Etobicoke, ON M8V 4B6 August 10th, 2015 @ 4:30PM–8PM Join us for our semi-annual Graphic Design for Print & Web and Web Design, Development & Maintenance Portfolio Show on Monday, August 10th 2015 at Humber College’s Lake Shore campus in room L1017 located in the L-Building between 4:30pm–8pm. This event is open to industry guests, alumni, faculty and potential students of the GDPW and WDDM programs. This is a great opportunity for our graduates to make connections with industry, alumni and faculty as they as they embark on their careers. We would love for you to join us in making this an epic day for our recent graduates. ");
		//event = Event.createEvent("Annual Messy Church Vacation Bible Club: Everest - Conquering Challenges").setDescription("2nd Annual Messy Church (For the whole family!)Vacation Bible Club: Everest - Conquering Challenges August 10-14, 2015 * 5-7pm (Supper included!) Christ Church St. James, 194 Park Lawn Road, Etobicoke Each day, kids participate in small groups, called Climbing Crews, and discover practical ways to hold on to God’s mighty power! Not only will they experience exciting Bible adventures but they’ll also watch for God in everyday life through something called God Sightings™. You and your kids will discover that God is active in our lives and that His fingerprints are everywhere! Everest is filled with epic Bible-learning experiences kids will see, hear, touch, and even taste! KidVid Cinema shares real stories of real kids who rely on God’s power to face life’s challenges! Plus, team-building games, cool Bible songs, and tasty treats keep everyone on the move.To Register : Call the church office at 416 251 8711 or go online at http://www.christchurchstjames.ca/ ");		
		event = Event.createEvent("15th annual Canadian Summer School on Quantum Information: Aug 10-14").setDescription("A week prior to the 6th conference for Quantum Information and Quantum Control (QIQC), the Fields Institute will be running the 15th annual Canadian Summer School on Quantum Information. Further information is available here. ");
		//event = Event.createEvent("Equinox Run Club").setDescription("JOIN US FOR EQUINOX RUN CLUB No matter your goal, this workout inspires more from your run. Great for the marathoner or the purist who just enjoys to run, improve your stride, cardiovascular endurance, and deliver the physical and inner strength necessary to achieve your personal goals! Created by Wes Perdesen.EVERY MONDAY'S STARTING @ 5:00pm AUGUST 10TH THROUGH SEPTEMBER 28TH ");
		//event = Event.createEvent("Leisurely Walk and Wiener Roast at Burgoyne Woods").setDescription("Walk around Burgoyne Woods.Than have our Wiener Roast .I will get there early to find a nice spot with a B.B.Q.Hot dogs are supplied with condiments and snack.Anything else you would like to bring would be welcomed.Bring own beverage.Should be fun.Weather permittingPlease let me know if you are coming Need A count for hot dogs ");
		//event = Event.createEvent("Diwan Restaurant Patio Now Open: Overlooking the Serene Aga Khan Park").setDescription("This summer, Toronto’s premiere destination for art and culture invites visitorsto enjoy lunch on the patio while taking in the tranquility of the new AgaKhan Park, featuring walking paths and a four-part garden oasis withreflective pools and more than 500 trees. Visitors can indulge in a deliciousgrilled menu inspired by Iranian, East African, and Asian at Diwan (Persianword for “meeting place”), the Aga Khan Museum’s restaurant. ");
		//event = Event.createEvent("Andy Warhol: Revisited").setDescription("  Andy Warhol: Revisited is a Warhol exhibition in Toronto that will provide an engaging and educational walkthrough of the development of Warhol’s artistic language and its greater effect on culture – an effect, which has echoed for decades. More than just an art exhibit, this Warhol exhibition in Toronto is curated in a way that is both thematic, and chronological in order to contextualize the artist’s trajectory and societal impact through educational materials, activated spaces, audio tours, interactive mobile apps, lectures, and special events. Andy Warhol: Revisited brings Toronto Warhol’s most recognizable pieces including portraits of Marilyn Monroe, Mao, Mickey Mouse, as well as the artist’s iconic Campbell’s Soup Cans. With over 120 historic prints and paintings on view from the Revolver Gallery collection, the exhibition includes a selection of artwork from the Andy Warhol Foundation, Christie’s, and distinguished museum and private collections. ------------------------------- HOURS AND ADMISSION ▹ Canada Day – December 31, 2015 Tuesday – Sunday, 10am – 8pm Closed on Mondays, and Christmas Day ------------------------------- GENERAL ADMISSION ▹ $10 | Adults $8 | Seniors (65+) $5 | Students with valid ID, and Youth 6 – 17 Free | Children (5 and under) 20% group discounts (10+) ------------------------------- LOCATION ▹ 77 Bloor St W Toronto, ON M5S 1M2 ------------------------------- PARKING ▹ Underground parking is available at the Manulife Center located at 55 Bloor Street W. ------------------------------- DETAILS ▹ Information ‣ Warhol Revisited Closer look ‣ Andy Warhol: Revisited ------------------------------- FOLLOW US ▹ WEBSITE ‣ http://www.revolverwarholgallery.com FACEBOOK ‣ http://facebook.com/revolvergallery INSTAGRAM ‣ http://instagram.com/revolvergallery TWITTER ‣ http://twitter.com/revolvergallery YOUTUBE ‣ https://www.youtube.com/user/RevolverGalleryBH PINTEREST ‣ http://pinterest.com/revolvergallery GOOGLE+ ‣ http://plus.google.com/10616883064039 SNAPCHAT ‣ RevolverGallery ");
	
		/*event = Event.createEvent("Kids in the Kitchen - Litterless Lunches!").
				setVenue(new Venue("The Casual Gourmet", new Address())).
				setTime(new EventTime("12/08/2015", "12/08/2015", "10:00 AM", "1:00 PM")).
				setDescription("At the Casual Gourmet we want to foster a love for health and cooking early in our Jr. Chefs! We welcome Catherine Switzer, R.H.N to teach and instill the following:   • how to easily incorporate healthy food everyday  • how to make nutritious choices by creating simple substitutions  • develop an understanding of what local food is and why nutrition is essential for energy and    learning  • how to appropriately and safely use  tools   • develop confidence and independence in the kitchen     Your Jr. Chef will leave with a new sense of responsibilty in the kitchen as well as some of the tools needed to create their very own litterless lunch!   *Children 4-11   ");
	*/
/*
		event = Event.createEvent("Outdoor Workout for all fitness levels. Let's have fun and shape up!").
				setVenue(new Venue("The Casual Gourmet", new Address())).
				setTime(new EventTime("12/08/2015", "12/08/2015", "10:00 AM", "1:00 PM")).
				setDescription("As we run along the beautiful scenic trails of the Lakeshore, we will make use of the natural surroundings to get a full body workout!  This workout is one hour. I am a RHN, Registered Holistic Nutitionist and PTS, Personal Training Specialist so you are in good capable hands. After our workout, stick around for a nutrition chat.  I can offer suggestions for post workout meals, healthy weight loss, enegy boosting, etc.  Bring your questions and we will discuss. Contact me at lucy@simplyhealthyliving.ca for more information No obligation, try a class for free.  Mondays and Wednesdays or Tuesday and Thursdays, various time slots available. ");
*/
		
		System.out.println(event.getSynopsis());
		System.out.println();
		
		
		budget = new Allowance [] {new Time(1, TimeUnit.SECONDS), new Time(10, TimeUnit.SECONDS), new Time(1, TimeUnit.MINUTES)};
		speakers = suggestor.suggestSpeakers(event, 1, 4, budget);
		
		System.out.println(speakers.prettyPrintSuggestion(10));
				
	}

}
