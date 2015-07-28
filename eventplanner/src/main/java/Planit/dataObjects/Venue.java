package Planit.dataObjects;

public class Venue {

	private String name;
	private Address address;
	
	public Venue(){
		name = "";
		address = null;
	}
	
	public Venue(String name, Address address) {
		this.name = name;
		this.address = address;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address a){
		address = a;
	}
	
	public String getTruncatedAddress(){
		if (address.getStreetNumber() != null && address.getRoute() != null){
			return address.getStreetNumber() + ", " + address.getRoute();
		}
		return "";
	}
}
