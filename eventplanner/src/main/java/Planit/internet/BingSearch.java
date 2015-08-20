package Planit.internet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import edu.toronto.cs.se.ci.UnknownException;

public class BingSearch {

	static private Map<String, Integer> cache;
	static private String cacheFileLocation = "src/main/resources/source caches/bing-hits";
	
	String accountKeyEnc = null;
	
	static private BingSearch instance;
	static public synchronized BingSearch getInstance(){
		if (instance == null){
			instance = new BingSearch();
		}
		return instance;
	}
	@SuppressWarnings("unchecked")
	private BingSearch(){
		
		String accountKey = "PGRmwHNrZr7hd+0dvN+GSv/AarPuloehTFR5ahR5Mq0=";
		byte [] accountKeyEncBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());	
		accountKeyEnc = new String(accountKeyEncBytes);
		
		if (cache == null){
			try{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(cacheFileLocation));
				cache = (Map<String, Integer>) in.readObject();
				in.close();
			}
			catch (IOException | ClassNotFoundException e){
				System.err.println("Warning: could not load the bing-hits cache");
			}
		}
		
		if (cache == null){
			cache = new HashMap<String, Integer>();
		}
		
	}
	
	public synchronized Integer getHitCount(String query) throws UnknownException{
		if (!cache.containsKey(query)){
			try {
				Integer hits = getHitCount(getSearchResults(constructUrl(query)));
				if (hits == null){
					throw new UnknownException();
				}
				cache.put(query, hits);
			} catch (IOException e) {
				throw new UnknownException(e);
			}
		}
		flush();
		return cache.get(query);
	}
	
	private static Integer getHitCount (JSONObject resultSet){
		
		try{
			return resultSet.getJSONObject("d").getJSONArray("results").getJSONObject(0).getInt("WebTotal");
		}
		catch (JSONException | NullPointerException e){
			return null;
		}
		
	}
	
	private JSONObject getSearchResults(String url) throws IOException{
		
		URL _url = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		
		JSONTokener tokener = new JSONTokener(connection.getInputStream());
		JSONObject json = new JSONObject(tokener);
		
		return json;
		
	}
	
	static String constructUrl(String query) throws UnsupportedEncodingException{
		
		String convertedQuery = URLEncoder.encode(query, "UTF-8");
		
		String url = 
				"https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Composite?Sources=%27web%27&Query=%27" +
						convertedQuery +
						"%27&$top=50&$format=Json";
		return url;
	}
	

	private static void flush(){
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFileLocation));
			out.writeObject(cache);
			out.close();
		}
		catch (IOException e){
			System.err.println("Warning: caching failed for the bing-hits cache");
		}
	}
}
