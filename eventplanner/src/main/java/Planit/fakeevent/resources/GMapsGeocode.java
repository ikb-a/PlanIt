package Planit.fakeevent.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.google.common.base.Optional;

import Planit.dataObjects.Address;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class GMapsGeocode extends BasicSource<Address, JSONObject, Void> {
	private String API_KEY = System.getenv("GOOGLE_KEY");

	@Override
	public JSONObject getResponse(Address input) throws UnknownException {
		if (API_KEY == null) {
			throw new RuntimeException("Google API Key missing. Please create environment variable GOOGLE_KEY");
		}

		String components = "street_number:" + input.getStreetNumber() + "|route:" + input.getRoute() + "|locality:"
				+ input.getCity() + "|administrative_area_level_1:" + input.getProvince() + "|country:"
				+ input.getCountry() + "|postal_code:" + input.getPostalCode();

		try {
			URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?components="
					+ URLEncoder.encode(components, "UTF-8") + "&key=" + API_KEY);
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
			return obj;
		} catch (IOException e) {
			throw new UnknownException(e);
		}
	}

	@Override
	public Expenditure[] getCost(Address args) throws Exception {
		return new Expenditure[] { new Time(500, TimeUnit.MILLISECONDS) };
	}

	@Override
	public Void getTrust(Address args, Optional<JSONObject> value) {
		return null;
	}

}
