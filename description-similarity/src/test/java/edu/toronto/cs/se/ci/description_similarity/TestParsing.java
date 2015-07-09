package edu.toronto.cs.se.ci.description_similarity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

public class TestParsing {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		String eventTitle = "One two three-four five..";
		String speakerTitle = "Media mogul, marketer-extraordinaire & America's favourite TV personality";
		List<String> speakerTopics = Arrays.asList(new String [] {"Youth & Campus"});
		
		List<String> eventTitleExp = Arrays.asList(new String [] {"one", "two", "three-four", "five"});
		List<String> speakerTitleExp = Arrays.asList(new String [] {"media", "mogul", "marketer-extraordinaire", "america's", "favourite", "tv", "personality"});
		List<String> speakerTopicsExp = Arrays.asList(new String [] {"youth", "campus"});
		List<String> speakerWordsExp = new ArrayList<String>(speakerTopicsExp);
		speakerWordsExp.addAll(speakerTitleExp);
		
		SimilarityQuestion q = new SimilarityQuestion(eventTitle, null, speakerTitle, speakerTopics);
		
		List<String> eventTitleAct = q.getEventWords();
		List<String> speakerWordsAct = q.getSpeakerWords();
		
		assertThat(eventTitleAct, is(eventTitleExp));
		assertThat(speakerWordsAct, is(speakerWordsExp));
	}

}
