package Planit.fakeevent.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.fakeevent.util.GoogleSearchJSON;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class GoogleCoocurrence extends BasicSource<String [], Integer, Void> {

	static protected int MAX_RESULTS_TO_CHECK = 10;
	
	/**
	 * Checks if the supplied strings co-occur in a google search.
	 * The supplied array must contain at least two strings.
	 * The first string will be searched for on Google, and the subsequent strings
	 * will be searched for within the search results.
	 * This method returns 1 if there is a search result containing all of the strings,
	 * except possibly the first one.
	 */
	@Override
	public Integer getResponse(String[] input) throws UnknownException{
		
		assert(input.length > 1);
		
		//search for the first term
		JSONObject json;
		try {
			json = GoogleSearchJSON.search(input[0]);
		} catch (UnknownException e1) {
			return -1;
		}
		
		//get the results
		int numResults;
		JSONArray results;
		try{
			numResults = json.getJSONObject("searchInformation").getInt("totalResults");
			if (numResults == 0){
				return 0;
			}
			else{
				results = json.getJSONArray("items");
			}
		}
		catch (JSONException e){
			throw new UnknownException(e);
		}
			
		//check the results
		int numResultsToCheck = Math.min(MAX_RESULTS_TO_CHECK, results.length());
		for (int i = 0; i < numResultsToCheck; i++){
			JSONObject result = results.getJSONObject(i);
			//check the title, link, and snippet
			String title = result.getString("title");
			String snippet = result.getString("snippet");
			String link = result.getString("link");
			//check that it contains the strings 
			boolean containsAll = true;
			for (int j = 1; j < input.length; j++){
				if (!title.contains(input[j]) && !snippet.contains(input[j]) && !link.contains(input[j])){
					containsAll = false;
					break;
				}
			}
			if (containsAll){
				return 1;
			}
		}

		return 0;
		
	}

	@Override
	public Expenditure[] getCost(String[] args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(String[] args, Optional<Integer> value) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
