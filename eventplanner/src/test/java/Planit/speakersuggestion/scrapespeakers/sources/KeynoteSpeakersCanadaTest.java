package Planit.speakersuggestion.scrapespeakers.sources;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class KeynoteSpeakersCanadaTest {

	@Test
	public void testRawCategoryMap(){
		
		String c = "adventure";
		String [] corresponding = KeynoteSpeakersCanada.getRawCategories(c);
		
		c = "science";
		corresponding = KeynoteSpeakersCanada.getRawCategories(c);
		assertTrue(corresponding.length == 1 && corresponding[0].equals("Health and Science"));
		
		c = "environment";
		corresponding = KeynoteSpeakersCanada.getRawCategories(c);
		assertTrue(corresponding.length == 1 && corresponding[0].equals("Environment"));
		
		c = "french";
		corresponding = KeynoteSpeakersCanada.getRawCategories(c);
		assertTrue(corresponding.length == 1 && corresponding[0].equals("Bilingual Speakers"));
		
		c = "sports";
		corresponding = KeynoteSpeakersCanada.getRawCategories(c);
		assertTrue(corresponding.length == 2 && corresponding[0].equals("Sport Commentary") && corresponding[1].equals("Sport Stars"));
	}
	
	@Test
	public void testCategoryChoice() throws IOException {
		
		List<String> keywords = Arrays.asList(new String [] {"basketball", "soccer", "hockey"});
		List<String> expected = Arrays.asList(new String [] {"Sport Commentary", "Sport Stars"});
		
		String [] categories = KeynoteSpeakersCanada.getMostRelevantCategories(keywords);
		
		assertTrue(categories.length == 2 && expected.contains(categories[0]) && expected.contains(categories[1]));
	}
	
	@Test
	public void testCategoryChoice2() throws IOException {
		
		List<String> keywords = Arrays.asList(new String [] {"culinary", "arts", "food", "health"});
		List<String> expected = Arrays.asList(new String [] {"Education", "Health and Science"});
		
		String [] categories = KeynoteSpeakersCanada.getMostRelevantCategories(keywords);
		
		assertTrue(categories.length == 1 && expected.contains(categories[0]));
	}

}
