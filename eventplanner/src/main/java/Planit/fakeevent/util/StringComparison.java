package Planit.fakeevent.util;

/**
 * A class with different functionality for comparing strings.
 * These are intended to help with different matching tasks with 
 * internet and local data.
 * @author wginsberg
 *
 */
public class StringComparison {

	/**
	 * Returns true if all of the words in the supplied array are contained in the supplied string
	 * @param checkInside - The string to check inside of
	 * @param checkFor - Words to check for
	 */
	public static boolean containsAllWords(String checkInside, String [] checkFor){
		
		for (int i = 0; i < checkFor.length; i++){
			if (!checkInside.contains(checkFor[i])) return false;
		}
		return true;
	}
	
}
