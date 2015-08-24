package Planit.speakersuggestion.wordsimilarity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Planit.internet.BingSearch;
import edu.toronto.cs.se.ci.UnknownException;

/**
 * Class for co-occurrence queries.
 * Uses google search through the browser interface for accurate hit counts.
 * @author wginsberg
 *
 */
public class CoOccurrence {

	private static String cacheFileLocation = "src/main/resources/source caches/co-occurence";
	
	private static Map<String, Integer> cache;
	
	static private CoOccurrence instance;

	@SuppressWarnings("unchecked")
	private CoOccurrence(){
		
		if (cache == null){
			try{
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(cacheFileLocation));
				cache = (Map<String, Integer>) in.readObject();
				in.close();
			}
			catch (IOException | ClassNotFoundException e){
				System.err.println("Warning: could not load the co-occurrence cache");
			}
		}
		
		if (cache == null){
			cache = new HashMap<String, Integer>();
		}

	}
	static public synchronized CoOccurrence getInstance(){
		if (instance == null){
			instance = new CoOccurrence();
		}
		return instance;
	}

	synchronized private static void flush(){
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFileLocation));
			out.writeObject(cache);
			out.close();
		}
		catch (IOException e){
			System.err.println("Warning: caching failed for the co-occurrence cache");
		}
	}
	
	/**
	 * Simply reports the number of hits for a search.
	 */
	synchronized public int hits(String search) throws UnknownException{
		if (!cache.containsKey(search)){
			int hits = BingSearch.getInstance().getHitCount(search);
			cache.put(search, hits);
			flush();
		}
		return cache.get(search);
	}
	
	/**
	 * Reports how much co-occurrence there is between an entity and a list of keywords.
	 */
	synchronized public Double cooccurrence(String entity, List<String> keywords) throws UnknownException{
		
		String entityQuery = entityQuery(entity);
		int entityHits = hits(entityQuery);
		if (entityHits == 0) return 0d;
		
		String coQuery = coQuery(entity, keywords);
		int coHits = hits(coQuery);
		
		String negQuery = negQuery(entity, keywords);
		int negHits = hits(negQuery);
		
		return (double) (coHits - negHits) / (entityHits);
		
	}
	
	static private String entityQuery(String entity){
		String s = "\"" + entity + "\"";
		return s;
	}
	
	/**
	 * Generates a query for the co-occurrence of an entity with keywords
	 */
	static private String coQuery(String entity, List<String> keywords){
		String s = String.format("\"%s\" & (%s)", entity, String.join(" | ", keywords));
		return s;
	}
	
	/**
	 * Generates a query for the negative co-occurrence of an entity with keywords.
	 */
	static private String negQuery(String entity, List<String> keywords){
		String s = String.format("\"%s\" & NOT %s", entity, String.join(" & NOT ", keywords));
		return s;
	}
	
	public Map<String, Integer> getCache(){
		return cache;
	}
}
