package Planit.speakersuggestion.wordsimilarity;

import java.net.*;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import edu.toronto.cs.se.ci.UnknownException;

/* 

For json support please download last version of json.simple from    http://json-simple.googlecode.com/
  tested with version http://json-simple.googlecode.com/files/json-simple-1.1.1.jar

For compiling and running example, please use following example  
javac  -cp json-simple-1.1.1.jar: DocumentSimilarity.java ; java  -cp json-simple-1.1.1.jar:   DocumentSimilarity

Author: Vitalie Scurtu
www.scurtu.it
 */

public class DocumentSimilarity {

	static public Double similarity(String document1, String document2){
		try {
			JSONObject response = queryForDocumentSimilarity(document1, document2);
			return response.getDouble("result");
		} catch (NullPointerException | JSONException | IOException e) {
			return null;
		}
	}
	
	/**
	 * Queries www.scurtu.it for document similarity
	 * @throws UnknownException If the result could not be returned
	 * @throws IOException 
	 */
	static private JSONObject queryForDocumentSimilarity(String document1, String document2) throws IOException{
		
		//send the request
		String urlToCall = "http://www.scurtu.it/apis/documentSimilarity";
		String content = "doc1=" + URLEncoder.encode(document1, "UTF-8") +
					"&doc2=" + URLEncoder.encode(document2, "UTF-8");
		URL url = new URL(urlToCall);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();                       
		connection.setDoOutput(true); 
		connection.setDoInput (true);
		connection.setUseCaches (false);        
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("Content-Type", "text/plain"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.connect();
		//send documents
		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		output.writeBytes(content);
		output.flush();
		output.close();

		//read the response
		JSONTokener tokener = new JSONTokener(connection.getInputStream());
		JSONObject response = new JSONObject(tokener);
		
		return response;
	}
	
}