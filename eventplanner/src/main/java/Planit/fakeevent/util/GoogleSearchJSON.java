package Planit.fakeevent.util;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import edu.toronto.cs.se.ci.UnknownException;

public class GoogleSearchJSON {

	/**
	 * Use this key on a json search result to get the title
	 */
	public static String RESULT_TITLE_KEY = "title";
	/**
	 * Use this key on a json search result to get the snippet
	 */
	public static String RESULT_SNIPPET_KEY = "snippet";
	
	private static String prefix =
			"https://www.googleapis.com/customsearch/v1?";
	
	private static String APP_ID;
	private static String API_KEY;
	
	//for keeping track between calls
	private static JSONObject json;
	private static int resultIndex;
	
	/**
	 * Returns the JSONObject of search results for a given search
	 * @param searchFor - a string to search for
	 * @return
	 * @throws UnknownException
	 */
	public static JSONObject search(String searchFor) throws UnknownException{
		
		/*
		 * Set the API access if it has not been done already
		 */
		if (APP_ID == null){
			APP_ID = System.getenv("GOOGLE_CSE_ID");
		}
		if (API_KEY == null){
			API_KEY = System.getenv("GOOGLE_API_KEY");
		}
		
		//format the search
		String parameters = String.format("q=%s&cx=%s&key=%s", searchFor.replace(" ", "-"), APP_ID, API_KEY);
		
		try {
			//create the url
			URL url = new URL(prefix + parameters);
			//grab the json
			json = URLtoJSON.getJSONfromURL(url);
			resultIndex = 0;
			
			return json;
		} catch (Exception e) {
			throw new UnknownException();
		} 
	}
	
	/**
	 * After a call to search(), this method will return the search results
	 * one at a time, eventually returning null when there are no more results.
	 * @return
	 */
	public static JSONObject nextResult(){
		try{
			return getResult(json, resultIndex++);			
		}
		catch(JSONException ex){
			return null;
		}
		
	}

	/**
	 * After a call to search() this method returns the number of hits for the search
	 * @return
	 */
	public static long numberOfHits() throws JSONException{
		return numberOfHits(json);
	}
	
	private static long numberOfHits(JSONObject searchJson) throws JSONException{
		if (searchJson == null){
			return -1;
		}
		return searchJson.getJSONObject("queries").getJSONArray("request").getJSONObject(0).getLong("totalResults");

	}
	
	private static JSONObject getResult(JSONObject searchJson, int i) throws JSONException{
		if (searchJson == null){
			return null;
		}
		return searchJson.getJSONArray("items").getJSONObject(i);			
	}
}
