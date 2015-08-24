package Planit.speakersuggestion.keywordextraction;

import java.util.LinkedList;
import java.util.List;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.UnknownException;
import Planit.speakersuggestion.keywordextraction.sources.EventCitySource;
import Planit.speakersuggestion.keywordextraction.sources.MostRepeatedWords;
import Planit.speakersuggestion.keywordextraction.sources.UncommonEnglishWords;
import Planit.speakersuggestion.keywordextraction.sources.YahooKeywordGeneration;

/**
 * Keyword finder contributional implementation which does not use the CI framework for execution
 * @author wginsberg
 *
 */
public class KeywordFinder {

	YahooKeywordGeneration yahooSource;
	MostRepeatedWords repeatedWordSource;
	UncommonEnglishWords uncommonWordSource;
	EventCitySource eventCitySource;
	
	public KeywordFinder(){
		yahooSource = new YahooKeywordGeneration();
		repeatedWordSource = new MostRepeatedWords();
		uncommonWordSource = new UncommonEnglishWords();
		eventCitySource = new EventCitySource();
	}
	
	/**
	 * Returns a list of keywords for an event.
	 * Yahoo Content Analysis is used first, if it gave no keywords
	 * then there are three sources used: uncommon English words, repeated words, and the city of the event.
	 * @param event
	 * @return
	 */
	public List<String> getKeywords(Event event){
		
		List<String> keywords = new LinkedList<String>();
		
		try {
			keywords.addAll(yahooSource.getOpinion(event.getWords()).getValue());
		} catch (UnknownException e) {}
		
		if (keywords.size() < 1){
			try{
				keywords.addAll(repeatedWordSource.getOpinion(event.getWords()).getValue());
			} catch (UnknownException e) {}
			try{
				keywords.addAll(uncommonWordSource.getOpinion(event.getWords()).getValue());
			} catch (UnknownException e) {}
			try{
				keywords.addAll(eventCitySource.getOpinion(event).getValue());
			} catch (UnknownException e) {}
			
		}
		return keywords;
		
	}
	
}
