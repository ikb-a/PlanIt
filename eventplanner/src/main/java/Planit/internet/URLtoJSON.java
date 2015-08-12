package Planit.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import edu.toronto.cs.se.ci.UnknownException;
import org.json.JSONObject;

/**
 * A class used to retrieve the JSON content from a webpage.
 * @author wginsberg
 *
 */
public class URLtoJSON {

	/**
	 * Returns the JSON associated with the given url
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
			reader.close();
			// Parse the JSON
			JSONObject obj = new JSONObject(sb.toString());
			return obj;
			
		} catch (IOException e) {
			throw new UnknownException();
		}
	}
	
}