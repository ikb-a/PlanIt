package Planit.fakeevent.resources;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.toronto.cs.se.ci.UnknownException;
import Planit.fakeevent.util.GoogleSearchJSON;

public class FBProfileExactlyExists extends FBProfileLooselyExists{

	/**
	 * Returns true if a google search for "<Name> Facebook" returned
	 * results and one result was a Facebook profile page with the
	 * exact <Name> as the page title.
	 */
	@Override
	public Integer getResponse(String name) {
		
		//get the json
		JSONObject json;
		try {
			json = GoogleSearchJSON.search(name + " " + "Facebook");
		} catch (UnknownException e1) {
			return -1;
		}
		JSONArray results;
		
		//try to get the results out of the json
		try{
			results = json.getJSONArray("items");
		}
		catch (Exception e){
			return -1;
		}

		
		//check the search results
		for (int i = 0; i < Math.min(numResultsToCheck, results.length()); i++){
			JSONObject result = results.getJSONObject(i);
			String title = result.getString("title");
			String content = result.getString("snippet");
			
			/*
			 * Check title of page is exactly
			 * "<Name> | Facebook"
			 * or
			 * "<Name> Profiles | Facebook"
			 */
			if (title.equals(name + " | Facebook") || title.equals(name + " Profiles | Facebook")){
				
				if (allowNonPeople){
					return 1;
				}
				else{
					if (isNonPerson(content)){
						return 0;
					}
					else{
						return 1;
					}
				}
			}
			
		}
		return 0;
	}	

}
