package edu.toronto.cs.Planit.data;

public class Event {

	private String title;
	private String url;
	private String description;
	private EventTime time;
	private EventOrganizer organizer;
	private Venue venue;
	
	public Event(String title, String description){
		this.title = title;
		this.description = description;
		url = "";
		time = null;
		organizer = null;
		venue = null;
	}
	
	public Event(){
		title = "";
		url = "";
		description = "";
		time = null;
		organizer = null;
		venue = null;
	}
	
	public Event(String title, String url, String description, Venue venue, EventTime time, EventOrganizer organizer){
		this.title = title;
		this.url = url;
		this.description = description;
		this.venue = venue;
		this.time = time;
		this.organizer = organizer;
	}
	
	public String toString(){
		return getTitle();
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
	public void setVenue(Venue v){
		venue = v;
	}
	public void setTime(EventTime t){
		time = t;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOrganizer(EventOrganizer o) {
		organizer = o;
	}
}
