package Planit.fakeevent.resources;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.utils.BasicSource;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.fakeevent.util.GoogleSearchJSON;

public class WikipediaPageExists extends BasicSource<String, Integer, Void> {

	private static String resultTitleSuffix = " - Wikipedia, the free encyclopedia";
	
	public int maxResultsToCheck = 3;

	@Override
	public Integer getResponse(String input) {
		
		try {
			//get the search results
			JSONObject json = GoogleSearchJSON.search(input + " Wikipedia");
			JSONArray results = json.getJSONObject("responseData").getJSONArray("results");
			
			//check if <input> returned a wikipedia page
			int numToCheck = Math.min(maxResultsToCheck, results.length());
			for (int i = 0; i < numToCheck; i++){
				if (results.get(i).equals(input + resultTitleSuffix)){
					return 1;
				}
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
		
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
