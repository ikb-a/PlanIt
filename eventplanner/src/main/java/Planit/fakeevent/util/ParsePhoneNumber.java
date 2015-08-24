package Planit.fakeevent.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ParsePhoneNumber {

	static Pattern areaCodePattern;

	 /**
	  * Returns the area code of the supplied phone number.
	  * The area code is interpreted as the first occurrence
	  * of three consecutive digits.
	  * @param input - the phone number to extract area code from
	  * @throws - if the supplied string does not contain an area code
	  */
	public static String getAreaCode(String input) throws MalformedInputException{
		
		if (areaCodePattern == null){
			areaCodePattern = getAreaCodePattern();
		}
		
		Matcher matcher = areaCodePattern.matcher(input);
		
		if (matcher.find()){
			return input.substring(matcher.start(), matcher.end());
		}
		else{
			throw new MalformedInputException();
		}
	}
	
	private static Pattern getAreaCodePattern(){
		return Pattern.compile("[0-9][0-9][0-9]");
	}
	
}
