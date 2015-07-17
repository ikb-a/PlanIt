package edu.toronto.cs.Planit.dataObjects;

public class Address {
	
	private String street_number;
	private String route;
	private String locality;
	private String administrative_area_level_1;
	private String country;
	private String postal_code;
	
	public Address(){
		street_number = "";
		route = "";
		locality = "";
		administrative_area_level_1 = "";
		country = "";
		postal_code = "";
	}
	
	public Address(String streetNumber, String route, String city,
			String province, String country, String postalCode) {
		this.street_number = streetNumber;
		this.route = route;
		this.locality = city;
		this.administrative_area_level_1 = province;
		this.country = country;
		this.postal_code = postalCode;
	}
	
	public String getStreetNumber() {
		return street_number;
	}

	public String getRoute() {
		return route;
	}

	public String getCity() {
		return locality;
	}

	public String getProvince() {
		return administrative_area_level_1;
	}

	public String getCountry() {
		return country;
	}

	public String getPostalCode() {
		return postal_code;
	}

	public void setStreetNumber(String street_number) {
		this.street_number = street_number;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public void setCity(String city) {
		this.locality = city;
	}

	public void setProvince(String province) {
		this.administrative_area_level_1 = province;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPostalCode(String postal_code) {
		this.postal_code = postal_code;
	}
}
