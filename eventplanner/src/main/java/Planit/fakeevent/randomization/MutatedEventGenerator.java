package Planit.fakeevent.randomization;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Planit.fakeevent.util.ReadJson;

public class MutatedEventGenerator extends Randomization {

	protected JSONArray events;
	
	public MutatedEventGenerator(File dataSource) throws IOException{
		events = (JSONArray) ReadJson.read(dataSource).get("events");
	}
	
	protected JSONObject randomEvent(){
		int index = integer(events.length());
		return (JSONObject) events.get(index);
	}
	
	@Override
	public String randomTitle() {
		try{
			return randomEvent().getString("title");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomDescription() {
		try{
			return randomEvent().getString("description");
		}
		catch (JSONException ex){
			return "";
		}

	}

	@Override
	public String randomName() {
		try{
			return randomEvent().getJSONObject("organizer").getString("name");
		}
		catch (JSONException ex){
			return "";
		}

	}

	@Override
	public String randomTwitterHandle() {
		try{
			return randomEvent().getJSONObject("organizer").getJSONObject("contact").getString("twitter_handle");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomTwitterURL() {
		try{
			return randomEvent().getJSONObject("organizer").getJSONObject("contact").getString("twitter_url");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomFaceBookURL() {
		try{
			return randomEvent().getJSONObject("organizer").getJSONObject("contact").getString("facebook_url");
		}
		catch (JSONException ex){
			return "";
		}

	}

	@Override
	public String randomURL() {
		try{
			return randomEvent().getJSONObject("organizer").getJSONObject("contact").getString("website");
		}
		catch (JSONException ex){
			return "";
		}

	}

	@Override
	public String randomEmail() {
		try{
			return randomEvent().getJSONObject("organizer").getJSONObject("contact").getString("email");
	}
	catch (JSONException ex){
		return "";
	}

	}

	@Override
	public String randomPhoneNumber() {
		try{
			return randomEvent().getJSONObject("organizer").getJSONObject("contact").getString("phone");
		}
		catch (JSONException ex){
			return "";
		}

	}

	@Override
	public String randomVenueName() {
		try{
			return randomEvent().getJSONObject("venue").getString("name");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomCity() {
		try{
			return randomEvent().getJSONObject("venue").getJSONObject("address").getString("locality");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomStreetNumber() {
		try{
			return randomEvent().getJSONObject("venue").getJSONObject("address").getString("street_number");
			
		}
		catch (JSONException ex){
			return "";
		}
	}
	
	@Override
	public String randomCountry() {
		try{
			return randomEvent().getJSONObject("venue").getJSONObject("address").getString("country");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomProvince() {
		try{
			return randomEvent().getJSONObject("venue").getJSONObject("address").getString("administrative_area_level_1");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomPostalCode() {
		try{
			return randomEvent().getJSONObject("venue").getJSONObject("address").getString("postal_code");
		}
		catch (JSONException ex){
			return "";
		}
	}

	@Override
	public String randomStreetName() {
		try{
			return randomEvent().getJSONObject("venue").getJSONObject("address").getString("route");
		}
		catch (JSONException ex){
			return "";
		}
	}

}
