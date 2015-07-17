package edu.toronto.cs.Planit.dataObjects;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Speaker {

	protected String name;
	protected String professionalTitle;
	protected List<String> topics;
	protected String bio;

	protected ArrayList<URL> pages;
	
	public Speaker(){
		
	}
	
	public Speaker(String name){
		this.name = name;
	}
	
	@Deprecated
	public Speaker(String name, String professionalTitle, List<String> topics, URL discoveryURL) {
		super();
		this.name = name;
		this.professionalTitle = professionalTitle;
		this.topics = topics;
		this.pages = new ArrayList<URL>();
		if (discoveryURL != null){
			addPage(discoveryURL);
		}
	}
	
	@Deprecated
	public Speaker(String name, String professionalTitle, List<String> topics, List<URL> discoveryURLs) {
		super();
		this.name = name;
		this.professionalTitle = professionalTitle;
		this.topics = topics;
		this.pages = new ArrayList<URL>();
		if (discoveryURLs != null){
			addPages(discoveryURLs);
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param professionalTitle The short title describing the speaker on their profile
	 * @param city
	 * @param location The city, province, and country of residence
	 */
	public Speaker(String name, String professionalTitle, String bio, List<String> topics, URL discoveryURL) {
		super();
		this.name = name;
		this.professionalTitle = professionalTitle;
		this.topics = topics;
		this.pages = new ArrayList<URL>();
		if (discoveryURL != null){
			addPage(discoveryURL);
		}
	}

	public Speaker(String name, String professionalTitle, String bio, List<String> topics, List<URL> discoveryURLs) {
		super();
		this.name = name;
		this.professionalTitle = professionalTitle;
		this.topics = topics;
		this.pages = new ArrayList<URL>();
		if (discoveryURLs != null){
			addPages(discoveryURLs);
		}
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
	
	public String getName() {
		return name;
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

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProfessionalTitle(String professionalTitle) {
		this.professionalTitle = professionalTitle;
	}

	public List<String> getTopics(){
		return topics;
	}
	
	public void setTopics(List<String> topics){
		if (ArrayList.class.isInstance(topics)){
			this.topics = (ArrayList<String>)topics;
		}
		else{
			this.topics = new ArrayList<String>(topics);
		}
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
	 */
	public void addPage(URL page){
		if (pages == null){
			getPages();
		}
		pages.add(page);
	}
	
	public void addPages(Collection<URL> page){
		if (pages == null){
			pages = new ArrayList<URL>();
		}
		pages.addAll(pages);
	}
}
