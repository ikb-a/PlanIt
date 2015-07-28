package edu.toronto.cs.Planit.speakersuggestion.keywordextraction.sources;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import Planit.speakersuggestion.keywordextraction.sources.MostRepeatedWords;
import edu.toronto.cs.se.ci.UnknownException;

public class MostRepeatedWordsTest {

	@Test
	public void test() throws UnknownException {
		MostRepeatedWords source = new MostRepeatedWords(1);
		String text = "one two three four four      five";
		List<String> processed = Arrays.asList(text.split("\\s+"));
		List<String> keyWords = source.getOpinion(processed).getValue();
		assertTrue(keyWords.size() == 1);
		assertTrue(keyWords.get(0).equals("four"));
	}

}
