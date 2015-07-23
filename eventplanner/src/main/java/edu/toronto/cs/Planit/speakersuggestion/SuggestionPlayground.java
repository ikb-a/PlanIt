package edu.toronto.cs.Planit.speakersuggestion;

import java.util.concurrent.TimeUnit;

import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.speakersuggestion.similarity.util.SuggestedSpeakers;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class SuggestionPlayground {

	final static String arffLocation = "src/main/resources/speaker suggestion/dataset/_dataset.arff";
	
	public static void main(String[] args) throws Exception {

		SpeakerSuggestor suggestor = SpeakerSuggestor.getInstance();
		
		Event event = Event.createEvent("UC Coffee house").setDescription("University college is hosting another coffee house! Come bring a guitar and play music or recite poetry. All University of Toronto students and friends are welcome for a night a of coffee, music, and good times.");
		Allowance [] budget = new Allowance [] {new Time(10, TimeUnit.SECONDS), new Time(1, TimeUnit.MINUTES)};
		SuggestedSpeakers speakers = suggestor.suggestSpeakers(event, 5, 10, budget);
		
		System.out.println(speakers);
		
		event = Event.createEvent("Afghan cultural night").setDescription("Afghan Students association presents a night of Afghan culture.");
		budget = new Allowance [] {new Time(10, TimeUnit.SECONDS), new Time(1, TimeUnit.MINUTES)};
		speakers = suggestor.suggestSpeakers(event, 5, 10, budget);
		
		System.out.println(speakers);
		
	}

}
