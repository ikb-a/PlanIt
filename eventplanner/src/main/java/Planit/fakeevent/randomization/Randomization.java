package Planit.fakeevent.randomization;
import java.util.Random;


public abstract class Randomization {

	protected static int MIN_EMAIL_ADDRESS_LENGTH = 5;
	protected static int MAX_EMAIL_ADDRESS_LENGTH = 20;
	protected static int MIN_DOMAIN_LENGTH = 5;
	protected static int MAX_DOMAIN_LENGTH = 20;
	
	protected static String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
	protected static String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	protected static String PUNCTUATION = ",.;:'\"()!?";
	
	protected static String [] emailDomains = {"gmail", "yahoo", "hotmail", "aol", "live", "cs.toronto", "utoronto", "facebook", "google"};
	protected static String [] countryCodes = {"com", "ca", "edu", "org", "gov", "net", "co.uk"};
	
	protected Random random;
	
	public Randomization(){
		random = new Random();
	}
	
	public Randomization(long seed){
		random = new Random(seed);
	}
	
	abstract public String randomTitle();
	abstract public String randomDescription();
	abstract public String randomName();
	abstract public String randomTwitterHandle();
	abstract public String randomTwitterURL();
	abstract public String randomFaceBookURL();
	abstract public String randomURL();
	abstract public String randomEmail();
	abstract public String randomPhoneNumber();
	abstract public String randomVenueName();
	abstract public String randomCity();
	abstract public String randomStreetNumber();
	abstract public String randomCountry();
	abstract public String randomProvince();
	abstract public String randomPostalCode();
	abstract public String randomStreetName();
	
	/**
	 * A random character in the ASCII range 32-126 inclusive.
	 * @return
	 */
	protected char character(){
		int randInt = random.nextInt();
		if (randInt < 0) randInt *= -1;
		int normalized = 32 + (randInt % (126-32));
		if (normalized < 0) normalized *= -1;
		char toRet = (char) normalized;
		return toRet;
	}
	
	/**
	 * Returns a random string from the given alphabet
	 * @param alphabet
	 * @return
	 */
	protected char character(String alphabet){
		return alphabet.charAt(integer(alphabet.length()));
	}
	
	/**
	 * Returns a random string.
	 * @param minLength - non negative
	 * @param maxLength - greater than zero
	 * @param alphabet - the set of characters to draw characters from
	 * @return
	 */
	protected String string(int minLength, int maxLength, String alphabet){
		
		int length;
		if (minLength == maxLength){
			length = minLength;
		}
		else{
			length = minLength + random.nextInt(maxLength - minLength);
		}
		
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++){
			sb.append(character(alphabet));
		}
		return sb.toString();
		
	}
	
	/**
	 * Returns a random string, drawing from lowercase, uppercase, and punctuation.
	 * The distribution of characters is (approximately) 90% lower case, 5% spaces, 3% upper case, and 2% punctuation
	 * @param minLength - must be non-negative
	 * @param maxLength - must be greater than 0
	 * @return
	 */
	protected String string(int minLength, int maxLength){

		//generate a string of only lowercase letter
		StringBuffer s = new StringBuffer(string(minLength, maxLength, LOWER_CASE));
		
		//sprinkle in upper case
		int upperCaseToAdd = (int) Math.ceil(s.length() * 0.03d);
		while (upperCaseToAdd > 0){
			int index = integer(s.length());
			s.setCharAt(index, character(UPPER_CASE));
			upperCaseToAdd--;
		}
		
		//sprinkle in punctuation
		int puncToAdd = (int) Math.ceil(s.length() * 0.02d);
		while (puncToAdd > 0){
			int index = integer(s.length());
			s.setCharAt(index, character(PUNCTUATION));
			puncToAdd--;
		}
		
		//sprinkle in spaces
		int spacesToAdd = (int) Math.ceil(s.length() * 0.02d);
		while (spacesToAdd > 0){
			int index = integer(s.length());
			s.setCharAt(index, ' ');
			spacesToAdd--;
		}
		
		return s.toString();
	}
	
	/**
	 * A random integer.
	 * @param bound - maximum value (inclusive bound)
	 * @return
	 */
	protected int integer(int bound){
		if (bound == 0) return 0;
		return random.nextInt(bound);
	}
	
	/**
	 * A random integer.
	 * @param min - inclusive minimum value
	 * @param max - inclusive maximum value
	 * @return
	 */
	protected int integer(int min, int max){
		return min + integer(max - min);
	}
	
	protected long longNumber(){
		return random.nextLong();
	}
	
	/**
	 * Returns a randomly generated phone number in the format <areaCode>-xxx-xxxx
	 */
	protected String phoneNumber(String areaCode){
		return String.format("%s-%s-%s",
				areaCode,
				String.valueOf(integer(100, 999)),
				String.valueOf(integer(1000, 9999)));
	}
	
	/**
	 * Returns a randomly generated phone number in the format xxx-xxx-xxxx
	 */
	protected String phoneNumber(){
		//randomly create the area code
		return phoneNumber(
				String.valueOf(integer(100, 999)));
	}
	
	protected String emailDomain(){
		return emailDomains[Math.abs(random.nextInt()) % emailDomains.length];
	}
	
	protected String countryCode(){
		return countryCodes[Math.abs(random.nextInt()) % countryCodes.length];
	}
	
	protected static int getMIN_EMAIL_ADDRESS_LENGTH() {
		return MIN_EMAIL_ADDRESS_LENGTH;
	}

	protected static void setMIN_EMAIL_ADDRESS_LENGTH(int mIN_EMAIL_ADDRESS_LENGTH) {
		MIN_EMAIL_ADDRESS_LENGTH = mIN_EMAIL_ADDRESS_LENGTH;
	}

	protected static int getMAX_EMAIL_ADDRESS_LENGTH() {
		return MAX_EMAIL_ADDRESS_LENGTH;
	}

	protected static void setMAX_EMAIL_ADDRESS_LENGTH(int mAX_EMAIL_ADDRESS_LENGTH) {
		MAX_EMAIL_ADDRESS_LENGTH = mAX_EMAIL_ADDRESS_LENGTH;
	}

	protected static int getMIN_DOMAIN_LENGTH() {
		return MIN_DOMAIN_LENGTH;
	}

	protected static void setMIN_DOMAIN_LENGTH(int mIN_DOMAIN_LENGTH) {
		MIN_DOMAIN_LENGTH = mIN_DOMAIN_LENGTH;
	}

	protected static int getMAX_DOMAIN_LENGTH() {
		return MAX_DOMAIN_LENGTH;
	}

	protected static void setMAX_DOMAIN_LENGTH(int mAX_DOMAIN_LENGTH) {
		MAX_DOMAIN_LENGTH = mAX_DOMAIN_LENGTH;
	}

}
