package edu.toronto.cs.Planit.dataObjects;

public class EventOrganizer {

	private String name;
	private EventContact contact;
	
	public EventOrganizer(){
		name = "";
		contact = null;
	}
	
	public void setContact(EventContact contact) {
		this.contact = contact;
	}
	
	public String getName(){
		return name;
	}
	
	public EventContact getContactInfo(){
		return contact;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
