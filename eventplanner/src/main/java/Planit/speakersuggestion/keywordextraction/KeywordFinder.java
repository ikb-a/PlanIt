package Planit.speakersuggestion.keywordextraction;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.toronto.cs.se.ci.UnknownException;
import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
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

	/**
	 * Runs keyword finding on one event and prints the result to standard output.
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws UnknownException 
	 */
	public static void main (String [] args) throws InterruptedException, ExecutionException, UnknownException{
		
		/*
		 * Create an event
		 */
		Event event = Event
				.createEvent("Hiroshima Memorial Day")
				.setDescription("1 pm - 2 pm, Nembutsu to pray for world peace and all the victims of "
						+ "the atomic bombing of Hiroshima (70 years ago)  at Tao Sangha Healing Centre. "
						+ "4:30 pm - 8:45 pm, We will join the Hiroshima Nagasaki Day Coalition at the "
						+ "Church of the Holy Trinity. http://www.hiroshimadaycoalition.ca (from "
						+ "4:30 to 6:30 we will provide Charity Shiatsu and donate all proceeds to the "
						+ "Hiroshima Nagasaki Day Coalition for the abolishment of nuclear weapons. ")
						.setVenue(new Venue("The Church of the Holy Trinity (beside Eaton Centre), Toronto",
								new Address("10", "Trinity Square", "Toronto", "ON", "Canada", "")));

		/*
		 * Set up for keyword finding
		 */
		KeywordFinder keywords = new KeywordFinder();

		/*
		 * Show the event in console
		 */
		System.out.println("Extracting keywords for event:\n");
		System.out.println("	" + event.toString());
		System.out.println();
		
		List<String> result = keywords.getKeywords(event);
		System.out.println("Keywords for the event are:\n");
		System.out.println("	" + result.toString());
	}
	
	
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
