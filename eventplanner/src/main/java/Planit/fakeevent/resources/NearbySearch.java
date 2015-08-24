package Planit.fakeevent.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

/**
 * This source is used to get a JSONArray of Google Maps results.
 * The address is specified as an argument for the getResponse 
 * method, however the search query can also be changed between calls.
 * The way that the search key is used is in the parameter 'name' in
 * the API call. It could also be possible to use it as 'keyword' or 'types'.
 * @author wginsberg
 */
public class NearbySearch extends BasicSource<List<Double>, JSONObject, Void> {

	private static String API_KEY;
	
	/**
	 * The radius (in metres) to search around.
	 * the accuracy of latitude and longitude on Google Maps is not ideal
	 * so this number does have to be significantly large.
	 */
	private int searchRadius = 500;

	private String searchKey;
	
	/**
	 * 
	 * @param searchFor
	 * @param searchRadius - in metres
	 */
	public NearbySearch(String searchFor) {
		
		this.searchKey = searchFor;
		
		if (API_KEY == null){
			API_KEY = System.getenv("GOOGLE_API_KEY");
		}
	}

	@Override
	/**
	 * Returns the json from google places on the supplied coordinates
	 */
	public JSONObject getResponse(List<Double> coordinates) throws UnknownException {
		
		//get the coordinates
		if (coordinates.size() != 2){
			throw new UnknownException("Invalid coordinates");
		}
		Double latitude = coordinates.get(0);
		Double longitude = coordinates.get(1);

		
		try {
			URL url = new URL(buildGooglePlacesURL(latitude, longitude, searchKey, searchRadius));
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			
			// Read in the entire file
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
		
			// Parse the JSON
			JSONObject obj = new JSONObject(sb.toString());
			
			//return the "results" part of the json data
			return obj;
			
		} catch (IOException e) {
			throw new UnknownException(e);
		}
	}
	
	/**
	 * Returns a url which will get google places results from the specified parameters
	 * https://developers.google.com/places/webservice/search#PlaceSearchRequests
	 */
	private String buildGooglePlacesURL(Double latitude, Double longitude, String searchFor, int searchRadius){
		
		//change spaces into "+"
		searchFor = searchFor.replaceAll(" ", "+");
		
		String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + 
					"key=" + API_KEY + "&" +
					"location=" + latitude.toString() + "," + longitude.toString() + "&" +
					"name=" + searchFor + "&" +
					"radius=" + String.valueOf(searchRadius);
		
		return url;
		
	}
	
	/**
	 * Search radius in metres
	 * @return
	 */
	public int getSearchRadius() {
		return searchRadius;
	}
	
	/**
	 * Search radius in metres
	 * @return
	 */
	public void setSearchRadius(int searchRadius) {
		this.searchRadius = searchRadius;
	}
	
	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchFor) {
		this.searchKey = searchFor;
	}

	@Override
	public Expenditure[] getCost(List<Double> args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(List<Double> args, Optional<JSONObject> value) {
		// TODO Auto-generated method stub
		return null;
	}
}
