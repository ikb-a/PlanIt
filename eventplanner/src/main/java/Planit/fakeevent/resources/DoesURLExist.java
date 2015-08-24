package Planit.fakeevent.resources;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class DoesURLExist extends BasicSource<String, Integer, Void> {

	protected boolean followRedirects = false;
	
	/**
	 * Returns 0 if the specified URL is malformed or if it results in a 404 code.
	 * Returns -1 if there was any Exception
	 * Returns 1 if there was no exception and a successful response code
	 * @param input - a URL to parse and check
	 */
	@Override
	public Integer getResponse(String input) {
		
		//parse the URL and check it is well formed
		URL url;
		try {
			url = new URL(input);
		} catch (MalformedURLException e) {
			return 0;
		}

		//get a connection to the url
			//keeping track of this as to not break other things
		boolean oldHttpFollowRedirects = HttpURLConnection.getFollowRedirects();
		HttpURLConnection.setFollowRedirects(followRedirects);
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			return -1;
		}

		
		//connect
		try {
			connection.connect();
			//and set it back to what it was before
			HttpURLConnection.setFollowRedirects(oldHttpFollowRedirects);
		}
		catch (UnknownHostException ex){
			//and set it back to what it was before
			HttpURLConnection.setFollowRedirects(oldHttpFollowRedirects);
			return 0;
		}
		catch (IOException ex) {
			//and set it back to what it was before
			HttpURLConnection.setFollowRedirects(oldHttpFollowRedirects);
			return -1;
		}
		
		
		//deal with the response code
		int responseCode = 0;
		try {
			responseCode = connection.getResponseCode();
		} catch (IOException e) {
			return -1;
		}
		if (responseCode == 404){
			return 0;
		}
		else{
			return 1;
		}
	}

	@Override
	public Expenditure[] getCost(String args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(String args, Optional<Integer> value) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

}
