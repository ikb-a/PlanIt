package Planit.speakersuggestion.similarity.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * An object which holds the results of a content analysis performed by YAahoo's Content Analysis
 * @author wginsberg
 *
 */
public class ContentAnalysis {
	
	JSONObject json;
	Map<String, Double> categories;
	
	public ContentAnalysis(JSONObject json) {
		this.json = json;
		categories = getCategories(json);
	}
	
	/**
	 * Extract the category information from the json response of YQL
	 * @param json
	 * @return
	 */
	static private Map<String, Double> getCategories(JSONObject json){

		Map<String, Double> categories = new HashMap<String, Double>();
		try{
			//if the response has only one category then this format will work
			JSONObject result = json.getJSONObject("query").getJSONObject("results").getJSONObject("yctCategories").getJSONObject("yctCategory");
			String category = result.getString("content");
			Double score = result.getDouble("score");
			categories.put(category, score);
		}
		catch (JSONException e){
			try{
				//if the response has multiple categories this will work
				JSONArray multiple = json.getJSONObject("query").getJSONObject("results").getJSONObject("yctCategories").getJSONArray("yctCategory");
				for (int i = 0; i < multiple.length(); i++){
					JSONObject result = multiple.getJSONObject(i);
					String category = result.getString("content");
					Double score = result.getDouble("score");
					categories.put(category, score);
				}
			}
			catch (JSONException e1){
				//nested exception
			}
		}
		return categories;
	}

	
	/**
	 * Returns a list of all categories.
	 * @return
	 */
	public Collection<String> getCategories(){
		return categories.keySet();
	}
	
	/**
	 * Returns a map which holds each category, and the score for that category
	 * @return
	 */
	public Map<String, Double> getCategoryInfo(){
		return categories;
	}

	/**
	 * Returns all the keywords reported. This could include categories and entities.
	 * This is the only method which ensures keywords with spaces are split into their component words.
	 * @return
	 */
	public Collection<String> getAllKeywords(){
		List<String> keywords = new ArrayList<String>();
		for (String word : getCategories()){
			if (word.contains(" ")){
				String [] components = word.split("\\s+");
				for (int i = 0; i < components.length; i++){
					if (components[i].length() > 1){
						keywords.add(components[i]);
					}
				}
			}
			else{
				keywords.add(word);
			}
		}
		return keywords;
	}
	
	/**
	 * Returns the Json formatted content analysis. This includes all possible information.
	 * @return
	 */
	public JSONObject getJson(){
		return json;
	}
	
	@Override
	public String toString(){
		return String.format("Yaho Content Analysis\n	categories=%s", categories.toString());
	}
}
