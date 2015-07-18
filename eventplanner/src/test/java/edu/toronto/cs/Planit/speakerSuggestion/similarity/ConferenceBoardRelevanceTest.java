package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.sources.Word2VecMeanSimilarity;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class ConferenceBoardRelevanceTest {

	static String eventsLocation = "main/resources/scrape/event and speakers/conferenceboardcanada.json";
	static EventAndSpeakerRelevance rel;
	static Event [] events;
	
	public ConferenceBoardRelevanceTest() {
		Gson gson = new Gson();
		try {
			events = gson.fromJson(new FileReader(eventsLocation), Event[].class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		Contracts.register(new Word2VecMeanSimilarity());
	}

	/**
	 * Tests that the speakers in the event file are all classified exactly as being relevant
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void test() throws InterruptedException, ExecutionException {

		for (int i = 0; i < events.length; i++){
			
			rel = new EventAndSpeakerRelevance();
;
			Allowance [] budget = new Allowance [] {new Time(1, TimeUnit.SECONDS)};
			
			List<Speaker> actualSpeakers = events[i].getConfirmedSpeakers();
			rel.compare(events[i], actualSpeakers, budget);
			
			//assertEquals(actualSpeakers.size(), rel.getMostRelevant().size());
			assertEquals(0, rel.getSomeWhatRelevent().size());
			assertEquals(0, rel.getLeastRelevant().size());
		}
	}


}
