package Planit.fakeevent.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;


/**
 * This class makes reading Json easy. and fun.
 * @author wginsberg
 *
 */
public class ReadJson {

	static public JSONObject read(File input) throws IOException{
		
		//open up a reader
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(input));
			
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

	}
	
}
