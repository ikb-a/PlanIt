package Planit.fakeevent.resources;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Address;
import edu.toronto.cs.se.ci.utils.BasicSource;
import edu.toronto.cs.se.ci.playground.sources.GMapsGeocode;

/**
 * A source which tells you if a list of different addresses have
 * global coordinates which are within some range of one another.
 * @author wginsberg
 *
 */
public class AddressesAreWithinRange extends BasicSource<ArrayList<Address>, Integer, Void> {

	private int minDistance = 0;
	private int maxDistance = 50000;
	
	private LengthUnit lengthUnit = LengthUnit.METER;
	
	private GMapsGeocode googleMaps;
	
	public AddressesAreWithinRange(int minDistance, int maxDistance){
		googleMaps = new GMapsGeocode();
		setMaxDistance(maxDistance);
		setMinDistance(minDistance);
	}
	
	/**
	 * Returns 1 if all addresses are within the range specified in this object.
	 * Returns 0 if one address is not in the range of one address.
	 * Returns -1 if coordinates of one address could not be found.
	 */
	@Override
	public Integer getResponse(ArrayList<Address> addresses){
		
		//store the addresses' coordinates in a 2d array
		LatLng [] coordinates = new LatLng [addresses.size()];
		
		for (int i = 0; i < addresses.size(); i++){
			
			//get the json results of the maps search
			JSONObject json;
			try {
				json = googleMaps.getResponse(addresses.get(i));
			} catch (UnknownException e) {
				return -1;
			}		
			
			try{
				//only consider the first result of the search
				JSONObject geometry =
						json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry");
				//get the coordinates for this address
				coordinates[i] = new LatLng(geometry.getDouble("lat"), geometry.getDouble("lng"));
			}
			catch (JSONException e){
				return -1;
			}
		}

		//check that all of the coordinates are within the range
		for (int i = 0; i + 1 < coordinates.length; i++){
			for (int j = i + 1; j < coordinates.length; j++){
				
				//check that the distance between i and j is within the range
				double distance = LatLngTool.distance(coordinates[i], coordinates[j], lengthUnit);
				if (distance < minDistance || distance > maxDistance){
					return 0;
				}
			}
		}
		
		//all coordinates were within range
		return 1;
	}

	@Override
	public Expenditure[] getCost(ArrayList<Address> args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(ArrayList<Address> args, Optional<Integer> value) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	public int getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	public LengthUnit getLengthUnit() {
		return lengthUnit;
	}

	public void setLengthUnit(LengthUnit lengthUnit) {
		this.lengthUnit = lengthUnit;
	}
}
