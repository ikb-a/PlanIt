package Planit.speakersuggestion.wordsimilarity;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.toronto.cs.se.ci.UnknownException;

public class CoOccurrenceTest {

	CoOccurrence co;
	
	@Before
	public void setUp() throws Exception {
		co = CoOccurrence.getInstance();
	}

	@Test
	public void canGetInstance() {
		assertNotNull(co);
	}

	@Test
	public void cacheNotNull(){
		assertNotNull(co.getCache());
	}
	
	@Test
	public void hitCountNoExceptions(){
		try{
			Integer hits = co.hits("pizza");
			assertNotNull(hits);
			assertTrue(hits > 0);
		}
		catch (Exception e){
			assertTrue(UnknownException.class.isInstance(e));
		}
	}
	
	@Test
	public void cooccurrenceNoExceptions(){
		try{
			Double coOccurrence = co.cooccurrence("Babe Ruth", Arrays.asList("baseball"));
			assertNotNull(coOccurrence);
			assertTrue(coOccurrence > 0d);
		}
		catch (Exception e){
			assertTrue(UnknownException.class.isInstance(e));
		}
	}

}
