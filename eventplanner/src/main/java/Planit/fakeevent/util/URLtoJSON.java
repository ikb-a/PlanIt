package Planit.fakeevent.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import edu.toronto.cs.se.ci.UnknownException;
import org.json.JSONObject;

/**
 * This class can be used any time you have a url and want to get the
 * json on the page it leads to.
 * @author wginsberg
 *
 */
public class URLtoJSON {

	/**
	 * Simply returns the JSON associated with the given url
	 * @param url
	 * @return
	 */
	public static JSONObject getJSONfromURL(URL url) throws UnknownException{
		
		//if we get any IOException just throw an unknown error
		BufferedReader reader;
		try {
			
			//open up a reader
			reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			
			// Read in the entire file
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
			
			// Parse the JSON
			JSONObject obj = new JSONObject(sb.toString());
			return obj;
			
		} catch (IOException e) {
			throw new UnknownException();
		}
	}
	
}
