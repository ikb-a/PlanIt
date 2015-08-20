package Planit.internet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.toronto.cs.se.ci.UnknownException;

/**
 * Class for accessing Google Search and getting search results.
 * @author wginsberg
 *
 */
public class GoogleSearchCSE {
 
	public static String RESULT_TITLE_KEY = "title";
	public static String RESULT_SNIPPET_KEY = "snippet";
	public static String RESULT_LINK_KEY = "link";
	
	private static String prefix =
			"https://www.googleapis.com/customsearch/v1?";
	
	private static String APP_ID;
	private static String API_KEY;

	/**
	 * Returns the JSONObject of search results for a given search
	 * @param searchFor - a string to search for
	 * @return The JSON content or an empty JSON object no content could be returned
	 * @throws UnknownException
	 */
	private static JSONObject searchJson(String searchFor){
		
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
		String parameters;
		if (searchFor.contains("site:")){
			String restrictToSite = searchFor.substring(5, searchFor.indexOf(" "));
			searchFor = searchFor.substring(searchFor.indexOf(" ") + 1, searchFor.length());
			parameters = String.format("q=%s&cx=%s&key=%s&siteSearch=%s", searchFor.replace(" ", "-"), APP_ID, API_KEY, restrictToSite);
		}
		else{
			parameters = String.format("q=%s&cx=%s&key=%s", searchFor.replace(" ", "-"), APP_ID, API_KEY);
		}
		
		try {
			//create the url
			URL url = new URL(prefix + parameters);
			//grab the json
			return URLtoJSON.getJSONfromURL(url);
			
		} catch (Exception e) {
			return new JSONObject();
		} 
	}

	/**
	 * Returns a list of search results for a given term
	 * @param searchFor
	 * @return
	 * @throws UnknownException 
	 */
	public static SearchResults search(String searchFor) throws UnknownException{
		
		JSONObject json = searchJson(searchFor);
		
		//Get the number of hits. If we can't get this number then there was not a successful search
		int hits;
		try{
			hits = json.getJSONObject("queries").getJSONArray("request").getJSONObject(0).getInt("totalResults");
		}
		catch (JSONException e){
			throw new UnknownException();
		}
		
		//get the results
		if (json.has("items")){
			
			JSONArray items = json.getJSONArray("items");
			List<SearchResult> results = new ArrayList<SearchResult>(items.length());
			
			for (int i = 0; i < items.length(); i++){
				try{
					JSONObject item = items.getJSONObject(i);
					SearchResult result = new SearchResult();
					result.setTitle(item.getString(RESULT_TITLE_KEY));
					result.setSnippet(item.getString(RESULT_SNIPPET_KEY));
					result.setLink(item.getString(RESULT_LINK_KEY));
					results.add(result);
				}
				catch (JSONException e){
					continue;
				}
			}
			return new SearchResults(hits, results);
		}
		else{
			return new SearchResults(hits, null);
		}
	}
}
