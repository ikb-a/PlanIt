package Planit.dataObjects;

import java.util.ArrayList;
import java.util.List;

import Planit.speakersuggestion.similarity.util.ComparableImp;

/**
 * An event which can be created in PlanIt, or a representation of an event from another source.
 * There is no public constructor, instead the static method createEvent should be used, and all details can be set in one line with the setter methods.
 * @author wginsberg
 *
 */
public class Event extends ComparableImp{

	private String title;
	private String url;
	private String description;
	private EventTime time;
	private EventOrganizer organizer;
	private Venue venue;
	private List<Speaker> confirmedSpeakers;
	
	private List<String> keyWords;
	
	/**
	 * Creates and returns a new event.
	 * @param title The title of the event.
	 * @return A new event with the supplied title.
	 */
	public static Event createEvent(String title){
		Event event = new Event(title);
		return event;
	}

	private Event(String title){
		this.title = title;
	}
	
	@Override
	public String toString(){
		return getTitle();
	}
	
	/**
	 * Returns words from the event title and description.
	 */
	@Override
	public synchronized List<String> getWords() {
		if (keyWords == null){
			keyWords = new ArrayList<String>();
			keyWords.addAll(extractKeywords(getTitle()));
			keyWords.addAll(extractKeywords(getDescription()));
		}
		return keyWords;
	}
	
	/**
	 * Returns a string to uniquely identify the event.
	 * (Currently the event's title)
	 */
	public String getID(){
		return getTitle();
	}
	
	public String getSynopsis(){
		return String.format("\"%s\"\n%s - %s\n%s - %s\n	%s...\n",
				getTitle(),
				getStartTime(),
				getEndTime(),
				getVenueName(),
				getVenueAddress(),
				getTruncatedDescription(70));
	}
	
	public String getTruncatedDescription(int characterLimit){
		if (characterLimit > description.length()){
			characterLimit = description.length();
		}
		return description.substring(0, characterLimit);
	}
	
	public String getVenueName(){
		if (venue == null){
			return "";
		}
		return venue.getName();
	}
	
	public String getVenueAddress(){
		return venue.getTruncatedAddress();
	}
	
	/**
	 * The start time and date returned as one string
	 * according to the pattern in the EventTime's parser.
	 */
	public String getStartTime(){
		if (time == null){
			return "";
		}
		if(time.getStartDate() == null){
			return "";
		}
		return time.getStartDate().toString();
	}
	
	/**
	 * The end time and date returned as one string
	 * according to the pattern in the EventTime's parser.
	 */
	public String getEndTime(){
		if (time == null){
			return "";
		}
		if(time.getEndDate() == null){
			return "";
		}
		return time.getEndDate().toString();
	}
	
	public String getTitle() {
		return title;
	}
	public String getUrl() {
		return url;
	}
	public String getDescription() {
		return description;
	}
	public EventTime getTime() {
		return time;
	}
	public EventOrganizer getOrganizer() {
		return organizer;
	}
	public Venue getVenue(){
		return venue;
	}
	
	/**
	 * @return This event object, for chaining.
	 */
	public Event setVenue(Venue v){
		venue = v;
		return this;
	}
	
	/**
	 * @return This event object, for chaining.
	 */
	public Event setTime(EventTime t){
		time = t;
		return this;
	}
	
	/**
	 * @return This event object, for chaining.
	 */
	public Event setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * @return This event object, for chaining.
	 */
	public Event setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * @return This event object, for chaining.
	 */
	public Event setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @return This event object, for chaining.
	 */
	public Event setOrganizer(EventOrganizer o) {
		organizer = o;
		return this;
	}

	public List<Speaker> getConfirmedSpeakers() {
		if (confirmedSpeakers == null){
			confirmedSpeakers = new ArrayList<Speaker>();
		}
		return confirmedSpeakers;
	}

	public void setConfirmedSpeakers(List<Speaker> confirmedSpeakers) {
		this.confirmedSpeakers = confirmedSpeakers;
	}

}
