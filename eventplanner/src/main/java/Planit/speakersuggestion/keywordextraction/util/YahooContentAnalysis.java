package Planit.speakersuggestion.keywordextraction.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Class for accessing the keyword extraction provided by Yahoo Content Analysis
 * @author wginsberg
 *
 */
public class YahooContentAnalysis {
	
	private final static String baseURL = "http://query.yahooapis.com/v1/public/yql?q=";
	
	/**
	 * Given a text to analyze, returns the URL which can be used to get analysis for that text
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 */
	public static URL getRequestURL(String text) throws MalformedURLException, UnsupportedEncodingException{
		String YQLquery = String.format("select * from contentanalysis.analyze where text='%s'", text);
		URL url = new URL(baseURL +
				URLEncoder.encode(YQLquery + "&format=json", "UTF-8")
					.replace("+", "%20")
					.replace("%3D", "=")
					.replace("%27", "'")
					.replace("%26", "&"));
		return url;
	}
	
	/**
	 * Returns a json response from Yahoo Content Analysis on the supplied text.
	 * @throws IOException If there was a problem in getting the response
	 */
	public static JSONObject getTextAnalysis(String text) throws IOException{
		URL url = getRequestURL(text);
		JSONObject json = getResponse(url);
		return json;
	}
	
	/**
	 * Analyze a text document
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public static ContentAnalysis analyzeText (String text) throws IOException{
		return new ContentAnalysis(getTextAnalysis(text));
	}
	
	/**
	 * Analyze a list of words
	 * @param words
	 * @return
	 * @throws IOException
	 */
	public static ContentAnalysis analyze(List<String> words) throws IOException{
		return analyzeText(String.join(" ", words));
	}
	
	/**
	 * Given a query in YQL format, returns the result from the Yahoo Developers API
	 * @param YQLquery
	 * @return
	 * @throws MalformedURLException 
	 */
	private static JSONObject getResponse(URL url) throws IOException{

		InputStream yahooStream = url.openStream();
		JSONTokener tokener = new JSONTokener(yahooStream);
		JSONObject json = new JSONObject(tokener);
		return json;

	}

}
