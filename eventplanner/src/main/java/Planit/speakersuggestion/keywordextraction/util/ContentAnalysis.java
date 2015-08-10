package Planit.speakersuggestion.keywordextraction.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
	Collection<String> entities;
	Collection<String> allExtractedTerms;
	
	public ContentAnalysis(JSONObject json) {
		this.json = json;
		categories = getCategories(json);
		entities = getEntities(json);
		allExtractedTerms = new ArrayList<String>(categories.size() + entities.size());
		allExtractedTerms.addAll(categories.keySet());
		allExtractedTerms.addAll(entities);
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
	 * Extracts the names of the entities from a YQL response
	 */
	static private Collection<String> getEntities(JSONObject json){
		
		Collection<String> entities = new LinkedList<String>();
		try{
			//if there is only one entity this will work
			String entitiy = json.
					getJSONObject("query").
					getJSONObject("results").
					getJSONObject("entities").
					getJSONObject("entity").
					getJSONObject("text").
					getString("content");
			entities.add(entitiy);
		}
		catch (JSONException e){
			//if there is more than one entity this will work
			try{
				JSONArray entityArray = json.
						getJSONObject("query").
						getJSONObject("results").
						getJSONObject("entities").
						getJSONArray("entity");
				for (int i = 0; i < entityArray.length(); i++){
					String entity = entityArray.
							getJSONObject(i).
							getJSONObject("text").
							getString("content");
					entities.add(entity);
				}

			}
			catch (JSONException e1){
				//nested exception
			}
		}
		return entities;
	}
	
	/**
	 * Returns a collection of all categories.
	 * @return
	 */
	public Collection<String> getCategories(){
		return categories.keySet();
	}
	
	/**
	 * Returns a collection of all entities.
	 * @return
	 */
	public Collection<String> getEntities(){
		return entities;
	}
	
	/**
	 * Returns all of the terms Yahoo content analysis provided
	 * @return
	 */
	public Collection<String> getAllExtractedTerms() {
		return allExtractedTerms;
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
		for (String word : getAllExtractedTerms()){
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
