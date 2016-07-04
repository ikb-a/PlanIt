package Planit.fakeevent.resources;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.fakeevent.util.GoogleSearchJSON;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class WikipediaNameisNotFictional extends BasicSource<String, Integer, Void> {

	public int maxResultsToCheck = 3;
	private static String resultTitleSuffix = "- Wikipedia, the free encyclopedia";
	
	@Override
	public Integer getResponse(String input) throws UnknownException {
	
		try {
			//get the search results
			JSONObject json = GoogleSearchJSON.search(input + " Wikipedia");
			JSONArray results = json.getJSONArray("items");
			
			//check results
			int numToCheck = Math.min(maxResultsToCheck, results.length());
			for (int i = 0; i < numToCheck; i++){
				JSONObject result = results.getJSONObject(i);
				//check if the result is a wikipedia page
				String title = result.getString("title");
				if (title.equals(input + resultTitleSuffix)){
					//check if the page is for a fictional character
					String content = result.getString("snippet");
					if (isDescriptionOfFictionalCharacter(content)){
						//return false because the page is not for a not fictional person
						return 0;
					}
				}
			}
			//no result was fictional
			return 1;
		} catch (Exception e) {
			return -1;
		}		
		
	}

	public boolean isDescriptionOfFictionalCharacter(String descrption){
		return descrption.contains("fiction") || descrption.contains("character");
	}
	
	@Override
	public Expenditure[] getCost(String args) throws Exception {
		
		return null;
	}

	@Override
	public Void getTrust(String args, Optional<Integer> value) {
		
		return null;
	}

	
	
}
