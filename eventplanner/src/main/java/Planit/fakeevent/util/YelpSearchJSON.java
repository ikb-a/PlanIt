package Planit.fakeevent.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Static class for getting json search results from Yelp.
 * @author wginsberg
 *
 */
public class YelpSearchJSON {

	private static String TOKEN;
	private static String TOKEN_SECRET;
	private static String CONSUMER_KEY;
	private static String CONSUMER_SECRET;

	private static String API_HOST = "api.yelp.com";
	private static String SEARCH_PATH = "/v2/search";
	//private static String BUSINESS_PATH = "/v2/business";
	
	private static OAuthService oAuthService;
	private static Token accessToken;
	
	protected static JSONObject lastResult;
	
	public static void init(){
		if (CONSUMER_KEY == null){
			CONSUMER_KEY = System.getenv("YELP_CONSUMER_KEY");
		}
		if (CONSUMER_SECRET == null){
			CONSUMER_SECRET = System.getenv("YELP_CONSUMER_SECRET");
		}
		if (TOKEN == null){
			TOKEN = System.getenv("YELP_TOKEN");
		}
		if (TOKEN_SECRET == null){
			TOKEN_SECRET = System.getenv("YELP_TOKEN_SECRET");
		}
		
		if (oAuthService == null || accessToken == null){
			init(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
		}
	}
	
	/**
	 * Must be called once before attempting to search.
	 * @param apiKey
	 * @param apiSecret
	 * @param token
	 * @param tokenSecret
	 */
	public static void init(String apiKey, String apiSecret, String token, String tokenSecret){
		oAuthService = new ServiceBuilder()
        	.provider(TwoStepOAuth.class)
        	.apiKey(apiKey)
        	.apiSecret(apiSecret)
        	.build();
		accessToken = new Token(token, tokenSecret);
	}
	
	/**
	 *  Return a json object of the search results on Yelp.
	 * @param keywords
	 * @param location
	 * @param resultLimit - limit the number of results of the search
	 * @return
	 */
	public static JSONObject searchBusinessByLocation(String keywords, String location, int resultLimit){
		
		//create the request
		OAuthRequest request = createRequest(SEARCH_PATH);
		request.addQuerystringParameter("term", keywords);
		request.addQuerystringParameter("location", location);
		request.addQuerystringParameter("limit", String.valueOf(resultLimit));
		
		//authorize the request
		oAuthService.signRequest(accessToken, request);
		
		//debug
		//String url = request.getCompleteUrl();
		
		//get the response and return it as json
		Response response = request.send();
		try{
			lastResult = new JSONObject(response.getBody());
		}
		catch (JSONException ex){
			lastResult = null;
		}
		return lastResult;
	}

	/**
	 * After a search is performed, this method with return the category information
	 * of the first result. Returns null if none are present. Throws JSONException if 
	 * something went wrong with parsing
	 * @return
	 */
	public static ArrayList<String> getCategories() throws JSONException{
		if (lastResult == null){
			return null;
		}
		
		ArrayList<String> toReturn = new ArrayList<String>();
		
		JSONArray categoryList;
		try{
			categoryList = lastResult.getJSONArray("businesses").getJSONObject(0).getJSONArray("categories");
		}
		catch (JSONException ex){
			System.err.println("Yelp result could not be parsed.");
			System.err.println(lastResult);
			return null;
		}
		
		if (categoryList == null){
			return null;
		}
		
		JSONArray categories = null;
		for (int i = 0; i < categoryList.length(); i++){
			try{
				categories = categoryList.getJSONArray(i);
			}
			catch (JSONException ex){
				System.err.println("Error while parsing Yelp result.");
				System.err.println(categoryList);
			}
			if (categories != null){
				toReturn.add(categories.getString(0));
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Creates a request for the given path on https and the API host.
	 */
	protected static OAuthRequest createRequest(String path){
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
		return request;
	}
}
