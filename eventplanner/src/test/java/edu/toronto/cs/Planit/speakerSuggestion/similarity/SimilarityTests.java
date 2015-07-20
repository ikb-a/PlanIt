package edu.toronto.cs.Planit.speakerSuggestion.similarity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.toronto.cs.Planit.dataObjects.Event;
import edu.toronto.cs.Planit.dataObjects.Speaker;
import edu.toronto.cs.Planit.speakerSuggestion.similarity.sources.Word2VecMeanSimilarity;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class SimilarityTests {

	static EventAndSpeakerRelevance sim;
	
	static Event event;
	static Speaker relevant;
	static Speaker somewhatRelevant;
	static Speaker notRelevant;
	
	@Before
	public void setUp() throws Exception {
		
		sim = new EventAndSpeakerRelevance();
		
		Contracts.register(new Word2VecMeanSimilarity());
		
		event = Event.createEvent("Coffe house event").setDescription("Come and have coffee and play music and hear poetry");
		List<Speaker> speakers = new ArrayList<Speaker>();
		relevant = Speaker.createSpeaker("Anonymous One").setProfessionalTitle("musician and coffee enthusiast");
		somewhatRelevant = Speaker.createSpeaker("Anonymous Two").setProfessionalTitle("poet");
		notRelevant = Speaker.createSpeaker("Anonymous Three").setProfessionalTitle("construction worker");
		speakers.addAll(Arrays.asList(new Speaker [] {relevant, somewhatRelevant, notRelevant}));
		
		Allowance [] budget = new Allowance [] {new Time(10, TimeUnit.SECONDS)};
		
		sim.compare(event, speakers, budget);
	}

	@After
	public void tearDown(){
		Contracts.deRegister(Word2VecMeanSimilarity.class);
	}
	
	@Test
	public void testGetLeastRelevant() {
		Collection<Speaker> speakers = sim.getLeastRelevant();
		assertEquals(1, speakers.size());
		assertTrue(speakers.contains(notRelevant));
	}

	@Test
	public void testGetSomeWhatRelevent() {
		Collection<Speaker> speakers = sim.getSomeWhatRelevent();
		assertEquals(1, speakers.size());
		assertTrue(speakers.contains(somewhatRelevant));
		}

	@Test
	public void testGetMostRelevant() {
		Collection<Speaker> speakers = sim.getMostRelevant();
		assertEquals(1, speakers.size());
		assertTrue(speakers.contains(relevant));
	}

}
