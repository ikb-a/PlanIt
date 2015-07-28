package edu.toronto.cs.Planit.speakersuggestion.keywordextraction.sources;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import Planit.speakersuggestion.keywordextraction.sources.UncommonEnglishWords;

public class UncommonEnglishWordsTest {

	@Test
	public void test() {
		String text = "There are many uncommon words in  the English language, but antidisestablishmentarianism is not one of them if it is spelled incorrrrrrrrectly.";
		List<String> parsed = Arrays.asList(text.split("\\s+"));
		UncommonEnglishWords source = new UncommonEnglishWords(3);
		List<String> response = source.getUncommonWords(parsed, 3);
		assertTrue(response != null);
		//make sure some words from the original list made it in
		assertTrue(response.removeAll(parsed));
	}

}
