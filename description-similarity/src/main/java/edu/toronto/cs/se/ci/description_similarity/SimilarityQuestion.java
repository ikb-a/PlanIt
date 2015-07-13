package edu.toronto.cs.se.ci.description_similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.toronto.cs.se.ci.data.Speaker;
import edu.toronto.cs.se.ci.eventObjects.Event;

public class SimilarityQuestion {

	static final List<String> filterWords = Arrays.asList(
			new String [] {"of", "the", "and", "with"});
	
	private Speaker speaker;
	private Event event;

	private List<String> speakerWords;
	private List<String> eventKeywords;
	
	public SimilarityQuestion(Speaker speaker, Event event) {
		this.speaker = speaker;
		this.event = event;
	}
	
	public SimilarityQuestion(String eventTitle, String speakerName, String speakerProfessionalTitle, List<String> speakerTopics){
		speaker = new Speaker(speakerName);
		speaker.setTopics(speakerTopics);
		speaker.setProfessionalTitle(speakerProfessionalTitle);
		event = new Event();
		event.setTitle(eventTitle);
	}
	
	/**
	 * Performs the String manipulations for tokenization of keywords, which normally
	 * would not be carried out until getter methods are called.
	 */
	public void preProcess(){
		getEventWords();
		getSpeakerWords();
	}
	
	/**
	 * Returns a list of keywords for the event.
	 * These keywords are parsed from the event's title.
	 * @return
	 */
	public List<String> getEventWords(){
		if (eventKeywords == null){
			eventKeywords = parseSentence(event.getTitle());
		}
		return eventKeywords;
	}
	
	/**
	 * Returns a list of words which can be used as keywords for the speaker.
	 * These are parsed from the speaker's professional title, as well as their
	 * list of speaking topics
	 * @return
	 */
	public List<String> getSpeakerWords(){
		if (speakerWords == null){
			speakerWords = new ArrayList<String>();
			if (speaker.getTopics() != null){
				speakerWords.addAll(parseTopics(speaker.getTopics()));
			}
			if (speaker.getProfessionalTitle() != null){
				speakerWords.addAll(parseSentence(speaker.getProfessionalTitle()));
			}
		}
		return speakerWords;
	}

	/**
	 * Topics will often have a form like "health & wellness". This method
	 * will remove the ampersand and split on spaces to produce ["health", "wellness"] 
	 * Converts to lowercase.
	 * @param topics
	 * @return
	 */
	static private List<String> parseTopics(Collection<String> topics){
		
		String seperator = " & ";
		
		ArrayList<String> parsed = new ArrayList<String>();
		for (String topic : topics){
			//if it has the separator then substitute this title with its components
			if (topic.contains(seperator)){
				parsed.addAll(Arrays.asList(topic.toLowerCase().split(seperator)));
			}
			//otherwise just take the topic as is
			else{
				parsed.add(topic);
			}
		}
		return parsed;
	}

	/**
	 * Removes all special characters, with the exception of hyphens and apostrophes.
	 * Modifies the sentence to be all lowercase.
	 * Removes "of", "the", "and", "with"
	 * @param sentence The String of words to parse
	 * @param minWordLength All words of less than this length are removed
	 * @return
	 */
	static private List<String> parseSentence(String sentence, int minWordLength){
		//remove punctuation exception hyphen, space, and apostrophe
		sentence = sentence.replaceAll("[\\W&&[^- ']]", "");
		//lower case
		sentence = sentence.toLowerCase();
		//separate into tokens
		List<String> tokens = Arrays.asList(sentence.split(" "));
		//filter the tokens
		tokens = tokens.stream().filter(token -> !filterWords.contains(token) && token.length() >= minWordLength).collect(Collectors.toList());
		return tokens;
	}
	
	/**
	 * Default minimum word length, 2.
	 * @param sentence
	 * @return
	 */
	static private List<String> parseSentence(String sentence){
		return parseSentence(sentence, 2);
	}
	
	public Speaker getSpeaker() {
		return speaker;
	}

	public Event getEvent() {
		return event;
	}

}
