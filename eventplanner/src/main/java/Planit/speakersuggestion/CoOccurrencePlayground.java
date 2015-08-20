package Planit.speakersuggestion;

import java.util.Arrays;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.similarity.sources.CoOccurrenceSource;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.data.Opinion;

public class CoOccurrencePlayground {

	static public void main (String [] args) throws UnknownException{
		
		CoOccurrenceSource source;
		source = new CoOccurrenceSource();
		
		Event event;
		Speaker speaker;
		ComparisonRequest cr;
		Opinion<Double, Void> opinion;
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("gooderham","watercolours","sculptors","gallery","hamilton","Hamilton"));
		
		speaker = Speaker.createSpeaker("Allison Massari");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		speaker = Speaker.createSpeaker("GA Gardner, Ph.D.");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		speaker = Speaker.createSpeaker("Tom Varano");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		speaker = Speaker.createSpeaker("Erik Wahl");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		
		
		speaker = Speaker.createSpeaker("Alan November");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		speaker = Speaker.createSpeaker("George Couros");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		speaker = Speaker.createSpeaker("Marc Prensky");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		speaker = Speaker.createSpeaker("Mark Jaccard");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();

		/*
		Opinion<Double, Void> opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("soccer","sports","world", "cup"));
		speaker = Speaker.createSpeaker("Trevor Linden");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("soccer","sports","world", "cup"));
		speaker = Speaker.createSpeaker("Allison Massari");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();

		event = Event.createEvent("").setKeyWords(Arrays.asList("soccer","sports","world", "cup"));
		speaker = Speaker.createSpeaker("Sadio Mane");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("soccer","sports","world", "cup"));
		speaker = Speaker.createSpeaker("Cristiano Ronaldo");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("soccer","sports","world", "cup"));
		speaker = Speaker.createSpeaker("David Beckham");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		
		event = Event.createEvent("").setKeyWords(Arrays.asList("soccer","sports","world", "cup"));
		speaker = Speaker.createSpeaker("Louis van Gaal");
		cr = new ComparisonRequest(event, speaker);
		opinion = source.getOpinion(cr);
		System.out.println(speaker.getName());
		System.out.println(event.getKeyWords());
		System.out.println(opinion.getValue());
		System.out.println();
		*/
	}
	
}
