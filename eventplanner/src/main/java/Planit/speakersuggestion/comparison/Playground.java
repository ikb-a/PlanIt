package Planit.speakersuggestion.comparison;

import java.util.Arrays;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;
import Planit.speakersuggestion.comparison.sources.CoOccurrenceComparison;

public class Playground {

	public static void main(String[] args) throws UnknownException {
		
		CoOccurrenceComparison source = new CoOccurrenceComparison();
		
		Event event;
		Speaker s1;
		Speaker s2;
		SpeakerComparisonRequest input;
		Opinion<Double, Void> opinion;
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("gooderham","watercolours","sculptors","gallery","hamilton","Hamilton"));
		s1 = Speaker.createSpeaker("Allison Massari");
		s2 = Speaker.createSpeaker("Harry Balzer");
		input = new SpeakerComparisonRequest(event, s1, s2);
		
		opinion = source.getOpinion(input);
		
		System.out.println(opinion.getValue());
	}

}
