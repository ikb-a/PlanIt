package Planit.fakeevent.randomization;

/**
 * An implementation of the Randomization class to be used in EventObjectRandomizer.
 * Each implemented method will be random characters, but subject to some constraints.
 * URLs, phone numbers, email addresses, postal codes, and dates will be in the expected
 * format but may be invalid. The intent is that the fields would be filled in a way 
 * technically possible for an event-planner user to do.
 * @author wginsberg
 *
 */
public class GibberishEventGenerator extends Randomization {

	
	public String randomTitle(){
		return string(10, 32);
	}
	
	public String randomDescription(){
		return string(140, 1000);
	}
	
	public String randomName(){
		return string(5, 18);
	}
	
	public String randomTwitterHandle(){
		return "@" + string(3, 14);
	}
	
	public String randomURL() {
		return String.format("http://%s/", domain());
	}

	public String randomEmail(){
		return emailAddress(domain());
	}
	
	public String randomPhoneNumber(){
		return phoneNumber();
	}
	
	public String randomVenueName(){
		return string(6,20);
	}
	
	@Override
	public String randomTwitterURL() {
		return randomURL();
	}

	@Override
	public String randomFaceBookURL() {
		return randomURL();
	}

	@Override
	public String randomCity() {
		return string(4, 10);
	}

	@Override
	public String randomStreetNumber() {
		return String.valueOf(integer(1, 7500));
	}

	@Override
	public String randomCountry() {
		return string(4, 10);
	}

	@Override
	public String randomProvince() {
		return string(4, 10);
	}

	@Override
	public String randomPostalCode() {
		return String.format("%s%d%s %d%s%d",
				character(UPPER_CASE),
				integer(9),
				character(UPPER_CASE),
				integer(9),
				character(UPPER_CASE),
				integer(9));
	}

	@Override
	public String randomStreetName() {
		return string(7,14);
	}

	/**
	 * Returns a random email address in the format xxx@<domain>
	 * @param domain - A domain like "gmail.com" or "yahoo.co.uk"
	 */
	public String emailAddress(String domain){
		String peronsal = string(MIN_EMAIL_ADDRESS_LENGTH, MAX_EMAIL_ADDRESS_LENGTH);
		return peronsal + "@" + domain;
	}
	
	/**
	 * Returns a random domain of the format xxx.yyy
	 * The first part of the domain will have a length bounded by MIN_DOMAIN_LENGTH, MAX_DOMAIN_LENGTH.
	 * The last part will have length 2 or 3.
	 * @return
	 */
	public String domain(){
		return string(MIN_DOMAIN_LENGTH, MAX_DOMAIN_LENGTH) + 
				string(2, 3);
	}
	
	/**
	 * Returns a random string of the format xxx xxx
	 * @return
	 */
	public String postalCode(){
		return string(3,3) + " " + string(3,3);
	}

}
