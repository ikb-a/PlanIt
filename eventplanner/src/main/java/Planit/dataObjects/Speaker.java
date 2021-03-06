package Planit.dataObjects;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Planit.speakersuggestion.similarity.util.ComparableImp;

/**
 * A public speaker who might speak at an event.
 * There is no public constructor, instead the static method createEvent should be used, and all details can be set in one line with the setter methods.
 * @author wginsberg
 */
public class Speaker extends ComparableImp{

	protected String name;
	protected String professionalTitle;
	protected List<String> topics;
	protected String bio;
	protected ArrayList<URL> pages;
	private transient List<String> words;
	private transient List<String> topicKeywords;
	
	/**
	 * Creates and returns a new Speaker object
	 * @param name The name of the speaker.
	 * @return 
	 */
	static public Speaker createSpeaker(String name){
		return new Speaker(name);
	}

	private Speaker(String name){
		this.name = name;
	}

	/**
	 * Compares based on names.
	 */
	@Override
	public boolean equals(Object other){
		if (other.getClass().equals(Speaker.class)){
			return getName().equals(((Speaker)other).getName());
		}
		return false;
	}
	
	public String toString(){
		return getName();
	}


	public String getSynopsis(){
		return String.format("%s\n		%s - %s\n", getName(), getProfessionalTitle(), getTopics().toString());
	}
	
	/**
	 * The title that describes this person on their profile page.
	 * e.g. "Award-Winning Talk Show Host"
	 * @return
	 */
	public String getProfessionalTitle() {
		return professionalTitle;
	}

	/**
	 * Returns this object for chaining.
	 * @param bio
	 */
	public Speaker setBio(String bio) {
		this.bio = bio;
		return this;
	}

	/**
	 * Returns this object for chaining.
	 * @param bio
	 */
	public Speaker setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Returns this object for chaining.
	 * @param bio
	 */
	public Speaker setProfessionalTitle(String professionalTitle) {
		this.professionalTitle = professionalTitle;
		return this;
	}

	/**
	 * Returns this object for chaining.
	 * @param bio
	 */
	public Speaker setTopics(List<String> topics){
		if (ArrayList.class.isInstance(topics)){
			this.topics = (ArrayList<String>)topics;
		}
		else{
			this.topics = new ArrayList<String>(topics);
		}
		return this;
	}
	
	public String getName() {
		if (name == null){
			name = "";
		}
		return name;
	}

	public String getBio() {
		if (bio == null){
			bio = "";
		}
		return bio;
	}
	
	/**
	 * Returns up to the first n characters of the speakers biography.
	 * @param n Number of characters to return
	 * @return A truncated bio for the speaker
	 */
	public String getTruncatedBio(int n){
		
		return getBio().substring(0, Math.min(n, getBio().length()));
		
	}
	
	public List<String> getTopics(){
		if(topics == null){
			topics = new ArrayList<String>();
		}
		return topics;
	}
	
	/**
	 * Returns the webpages associated with this speaker
	 * @return
	 */
	public ArrayList<URL> getPages(){
		if (pages == null){
			pages = new ArrayList<URL>();
		}
		return pages;
	}
	
	/**
	 * Indicate that a new webpage is associated with the speaker
	 * @param page
	 * @return This speaker, for chaining
	 */
	public Speaker addPage(URL page){
		if (pages == null){
			getPages();
		}
		pages.add(page);
		return this;
	}
	
	public void addPages(Collection<URL> page){
		if (pages == null){
			pages = new ArrayList<URL>();
		}
		pages.addAll(pages);
	}

	/**
	 * Returns words from this speakers list of topics, professional title, and bio.
	 */
	@Override
	public synchronized List<String> getWords() {
		if (words == null){
			words = new ArrayList<String>();
			words.addAll(getTopics());
			words.addAll(extractKeywords(getProfessionalTitle()));
			words.addAll(extractKeywords(getBio()));			
		}
		return words;
	}
	
	/**
	 * List of topics as keywords
	 * @return All topics split on spaces and with punctuation removed
	 */
	public synchronized List<String> getTopicKeywords(){
		if (topicKeywords == null){
			topicKeywords = new ArrayList<String>();
			for (String topic : getTopics()){
				topicKeywords.addAll(extractKeywords(topic));
			}
		}
		return topicKeywords;
	}
}
