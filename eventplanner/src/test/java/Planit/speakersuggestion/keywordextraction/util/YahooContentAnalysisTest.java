package Planit.speakersuggestion.keywordextraction.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import Planit.speakersuggestion.keywordextraction.resource.YahooContentAnalysis;

public class YahooContentAnalysisTest {

	@Test
	public void test() {
		
		String text = "The award-winning Waterfront Singing Ambassadors Program presented by The Waterfront BIA returns to Toronto’s Waterfront this summer. Eight lively ambassadors will offer assistance, directions and recommendations to visitors and perform impromptu a cappella renditions of classic summer hits on Thursdays, Fridays, Saturdays, Sundays and Mondays from 11am to 6pm until September 7. Visit www.waterfrontbia.com for more information. The Singing Ambassadors feature Paulo Amor, Alicia Estridge, Reuven Grajner, Olivia Janus, Allister MacDonald, Jessie Rivest, Chris Tanaka-Mann and Sheree Spencer. The Ambassadors are split into quartets that will perform ten a cappella summertime favourites in key locations and busy daytime spots along the Waterfront. Each song has been arranged as full and 1.5 minute songs by internationally-published com¬poser, arranger and per¬former Aaron Jensen. Songs for the summer season include: Happy by Pharrell Williams; Safe and Sound by Capital Cities; Sh Boom Sh Boom by The Chords; It’s All Right by The Impressions; Dancing in the Street by Martha and The Vandellas; Good Day Sunshine by The Beatles; Saturday in the Park by Chicago; I Can See Clearly Now by Johnny Nash; I Get Around by the Beach Boys; Under the Boardwalk by The Drifters. Throughout the summer, the Singing Ambassadors can be seen riding the sponsored City Sightseeing Toronto double-decker tour buses every week using a 'hop-on, hop-off” pass to explore the city and its top attractions. In addition, they support member and resident events along the waterfront and are making special appearances at the Party on the Promenade at the Redpath Waterfront Festival, Purina PawsWay, Canada Day Celebrations, Cityfest, Sail-in Cinema, Taste of Toronto at Fort York, Union Station, Jack Layton Ferry Terminal, Ripley's Aquarium, Centreville, Billy Bishop Toronto City Airport and Ontario Celebration Zone at Harbourfront Centre to name a few. Wearing The Waterfront BIA branded polo shirts, windbreakers, back packs, umbrellas and festive fedoras, the Singing Ambassadors will be easy to spot as they distribute prizes, discounts and information featuring key Waterfront activities to keep both Torontonians and tourists well-informed of all that the Waterfront has to offer. The Singing Ambassadors make the Toronto Waterfront a welcoming destination for tourists and locals. The program is made possible with assistance from Ontario's Summer Jobs Service and Canada Summer Jobs. ";		
		text = text.replaceAll("[^ a-zA-z0-9]", "");
		try {
			ContentAnalysis analysis = YahooContentAnalysis.analyzeText(text);
			
			assertNotNull(analysis.getAllExtractedTerms());
			assertNotNull(analysis.getEntities());
			assertNotNull(analysis.getCategories());
			assertNotNull(analysis.getAllKeywords());
						
			assertTrue(analysis.getAllExtractedTerms().size() > 0);
			
		} catch (IOException e) {
			fail(e.toString());
		}
		
	}

}
