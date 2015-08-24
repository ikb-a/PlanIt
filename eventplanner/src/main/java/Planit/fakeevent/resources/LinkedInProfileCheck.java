package Planit.fakeevent.resources;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.fakeevent.util.GoogleSearchJSON;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class LinkedInProfileCheck extends BasicSource<String, Integer, Void> {

	private int numResultsToCheck = 5;
	
	@Override
	public Integer getResponse(String name){
	
		try{
			//get the json
			JSONObject json;
			json = GoogleSearchJSON.search(name + "LinkedIn");
			
			//get the results
			JSONArray results;
			results = json.getJSONObject("responseData").getJSONArray("results");
			
			//check the search results
			for (int i = 0; i < Math.min(numResultsToCheck, results.length()); i++){
				JSONObject result = results.getJSONObject(i);
				String title = result.getString("titleNoFormatting");
				/*
				 * Check that the page title is the LinkedIn profile we are seeking
				 * It could either be "<Name> | LinkedIn" or "<Name> Profiles | LinkedIn"
				 */
				if (title.equals(name + " | LinkedIn") || title.equals(name + " Profiles | LinkedIn")){
					return 1;
				}
			}
		}
		catch (Exception e){
			return -1;
		}
		return 0;
		
	}

	@Override
	public Expenditure[] getCost(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(String arg0, Optional<Integer> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumresultsToCheck() {
		return numResultsToCheck;
	}

	public void setNumresultsToCheck(int numresultsToCheck) {
		this.numResultsToCheck = numresultsToCheck;
	}

}
