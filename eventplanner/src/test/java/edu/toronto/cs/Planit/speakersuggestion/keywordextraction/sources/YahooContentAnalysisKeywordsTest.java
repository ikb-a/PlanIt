package edu.toronto.cs.Planit.speakersuggestion.keywordextraction.sources;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import Planit.speakersuggestion.keywordextraction.sources.YahooKeywordGeneration;
import edu.toronto.cs.se.ci.UnknownException;

public class YahooContentAnalysisKeywordsTest {

	@Test
	public void test() throws UnknownException {
		YahooKeywordGeneration source = new YahooKeywordGeneration();
		String text = "The instant the gun sounded to start the women’s 800-metre final at the Pan Am Games last Wednesday, Cuba’s Rose Mary Almanza accelerated and kept going. And why not? Her best time this season put her two seconds ahead of the next-fastest runner in the field, so she had reason to trust her speed. The rest of the field could burn themselves out keeping up or hang back hoping she would fade. They waited. Almanza faded. American Alysia Montano passed her first, then eventual gold medallist Melissa Bishop of Canada. And, if you were looking for a moment that symbolizes the shift of Olympic sport dominance in the Pan Am region, it happened here, as Brazil’s Flavia De Lima passed Almanza for the bronze. Until this year, Cuba routinely occupied a spot just below the U.S. on the Pan Am Games medal table, with a large gap to the next-most successful teams. Over the past four Pan Am cycles, Cuba has averaged 165.25 medals per games, 75.25 of them gold. Both totals trail only the U.S. This time around, Cuba won 36 gold among 97 medals, fourth in both categories, behind the U.S., Canada and Brazil. Toronto 2015 was the first time since 1967 that Cuba failed to win more than 100 medals. Cuba continues to punch above its weight — with 11.27 million residents, it is by far the least populous of the top five medal-winning nations.";
		List<String> parsed = Arrays.asList(text.split("\\s+"));
		List<String> keywords = source.getOpinion(parsed).getValue();
		//check that some words were keywords
		assertTrue(!keywords.isEmpty());
	}

}
