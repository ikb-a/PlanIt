package Planit.speakersuggestion.keywordextraction;

import java.util.List;
import java.util.concurrent.ExecutionException;

import Planit.speakersuggestion.keywordextraction.sources.MostRepeatedWords;
import Planit.speakersuggestion.keywordextraction.sources.UncommonEnglishWords;
import Planit.speakersuggestion.keywordextraction.sources.YahooKeywordGeneration;
import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * A class which finds keywords in text using a contributional implementation.
 * @author wginsberg
 *
 */
public class KeywordFinder {

	/*
	public static void main(String [] args){
		
		String text = "The instant the gun sounded to start the women’s 800-metre final at the Pan Am Games last Wednesday, Cuba’s Rose Mary Almanza accelerated and kept going. And why not? Her best time this season put her two seconds ahead of the next-fastest runner in the field, so she had reason to trust her speed. The rest of the field could burn themselves out keeping up or hang back hoping she would fade. They waited. Almanza faded. American Alysia Montano passed her first, then eventual gold medallist Melissa Bishop of Canada. And, if you were looking for a moment that symbolizes the shift of Olympic sport dominance in the Pan Am region, it happened here, as Brazil’s Flavia De Lima passed Almanza for the bronze. Until this year, Cuba routinely occupied a spot just below the U.S. on the Pan Am Games medal table, with a large gap to the next-most successful teams. Over the past four Pan Am cycles, Cuba has averaged 165.25 medals per games, 75.25 of them gold. Both totals trail only the U.S. This time around, Cuba won 36 gold among 97 medals, fourth in both categories, behind the U.S., Canada and Brazil. Toronto 2015 was the first time since 1967 that Cuba failed to win more than 100 medals. Cuba continues to punch above its weight — with 11.27 million residents, it is by far the least populous of the top five medal-winning nations.";
		List<String> parsed = Arrays.asList(text.split("\\s+"));
		Allowance [] budget = new Allowance [] {new Time(1, TimeUnit.SECONDS)};
		
		KeywordFinder k = new KeywordFinder();
		try{
			System.out.println(k.getKeywords(parsed, budget));
		}
		catch (Exception e){
			System.err.println("Could not extract keywords");
		}
	}
	*/
	
	private CI<List<String>, List<String>, Void, Void> ci;
	private Aggregator<List<String>, Void, Void> agg;
	
	public KeywordFinder(){
		
		if (Contracts.discover(KeyWordExtractionContract.class).isEmpty()){
			Contracts.register(new YahooKeywordGeneration());
			Contracts.register(new MostRepeatedWords(3));
			Contracts.register(new UncommonEnglishWords(3));
		}
		
		agg = new KeywordAggregator();
		
		ci = new CI<List<String>, List<String>, Void, Void>
		(KeyWordExtractionContract.class, agg, new AllSelector<List<String>, List<String>, Void>());

	}
	
	public List<String> getKeywords(List<String> words, Allowance [] budget) throws ExecutionException, InterruptedException{
		Result<List<String>, Void> result = ci.apply(words, budget).get();
		if (result != null){
			return result.getValue();
		}
		return null;
	}
}
