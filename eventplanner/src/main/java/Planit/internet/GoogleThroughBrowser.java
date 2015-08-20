package Planit.internet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.toronto.cs.se.ci.UnknownException;
import Planit.scraping.RandomThrottler;
import Planit.scraping.Throttler;

/**
 * Connects to Google Search through the standard browser interface. Only reports on the number of hits.
 * To obtain full results then Google should be reached in the legitimate way, through the Custom Search API.
 * This class has its access to the internet throttled with a randomized interval, so that the program will not be
 * blocked with captchas.
 * @author wginsberg
 *
 */
public class GoogleThroughBrowser {

	static String baseURL = "http://www.google.com/search?q=";
	static String charset = "UTF-8";
	static String userAgent = "Research for CI";

	static String resultStatsRegexPre = "About";
	static String resultStatsRegexMiddle = "[, 0-9]*";
	static String resultStatsRegexPost = "results";
	static Pattern resultStatsPattern;

	private static Throttler throttler;
	
	static String cacheFileLocation = "src/main/resources/source caches/google-hits";
	private static Map<String, Integer> cache;
	
	/**
	 * Returns the number of google hits for the query.
	 * @param query
	 * @return 
	 */
	public static Integer getNumberOfHits(String query) throws UnknownException{
		
		if (cache == null){
			getCache();
		}
		
		if (!cache.containsKey(query)){
			cache.put(query, getResponseOnline(query));
			try {
				flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!cache.containsKey(query)){
			throw new UnknownException();
		}
		
		return cache.get(query);
	}
	
	@SuppressWarnings("unchecked")
	private static synchronized Map<String, Integer> getCache(){
		
		if (cache == null){
			try{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(cacheFileLocation));
				cache = (Map<String, Integer>) in.readObject();
				in.close();
			}
			catch (IOException | ClassNotFoundException e){
				System.err.println("Warning: could not load the google-hits (browser-interface) cache");
			}
		}
		
		if (cache == null){
			cache = new HashMap<String, Integer>();
		}
		
		return cache;
	}
	
	/**
	 * Throttle the execution of the class to one call per second
	 */
	synchronized private static void throttle(){
		if (throttler == null){
			throttler = new RandomThrottler(1, TimeUnit.SECONDS);
		}
		throttler.next();
	}
	
	private static Integer getResponseOnline(String input) throws UnknownException {

		throttle();

		URLConnection connection = getConnection(input);
		if (connection == null){
			throw new UnknownException();
		}

		Scanner reader;
		try {
			reader = new Scanner(connection.getInputStream(), charset);
		} catch (IOException e) {
			throw new UnknownException(e);
		}

		int resultStats = 0;

		while(reader.hasNextLine()){

			String line = reader.nextLine();

			resultStats = getResultCount(line);
			if (resultStats == -1){
				continue;
			}
			else{
				break;
			}

		}    
		reader.close();

		return resultStats;
	}

	/**
	 * Returns a URL connection to google search, or null if a connection could not be established.
	 * @param query
	 * @return
	 */
	static private URLConnection getConnection(String query){

		URLConnection connection;
		try{
			URL url = new URL(baseURL + URLEncoder.encode(query, charset));
			connection = url.openConnection();
		}
		catch(IOException e){
			return null;
		}
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);
		connection.addRequestProperty("User-Agent", "Mozilla/5.0");

		return connection;
	}

	/**
	 * Extracts the result count from a line of hypertext, if it exists
	 * @param html
	 * @return the result count, or -1 if it does not exist
	 */
	private static int getResultCount(String input){

		Matcher matcher = getResultStatsPattern().matcher(input);

		while (matcher.find()){
			String match = matcher.group();
			//isolate out the contents of the div
			String isolated = match.substring(resultStatsRegexPre.length(), match.length() - resultStatsRegexPost.length());
			//removed all non number characters
			String number = isolated.replaceAll("[^0-9]", "");
			//parse into an int
			try{
				return Integer.parseInt(number);
			}
			catch (NumberFormatException e){
				break;
			}
		}
		return -1;
	}

	private static Pattern getResultStatsPattern(){
		if (resultStatsPattern == null){
			resultStatsPattern = Pattern.compile(resultStatsRegexPre + resultStatsRegexMiddle + resultStatsRegexPost);
		}
		return resultStatsPattern;
	}

	public static void flush() throws IOException {
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFileLocation));
			out.writeObject(cache);
			out.close();
		}
		catch (IOException e){
			System.err.println("Warning: caching failed for the google-hits (browser interface) cache");
		}
	}


}
