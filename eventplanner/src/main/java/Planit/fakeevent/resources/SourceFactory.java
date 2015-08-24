package Planit.fakeevent.resources;

import java.util.HashMap;

import edu.toronto.cs.se.ci.Source;

/**
 * Use the SourceFactory to get instances of sources, so
 * that each source only gets instantiated once and can be shared
 * across the entire program without manually passing the references.
 * @author wginsberg
 *
 */
public class SourceFactory {

	protected static HashMap<Class<?>, Source<?, ?, ?>> sources;
	
	/**
	 * Returns an instance of the given class
	 * @param sourceType
	 * @return
	 */
	public static Source<?, ?, ?> getSource(Class<?> sourceType){
		if (sources == null){
			sources = new HashMap<Class<?>, Source<?, ?, ?>>();
		}
		if (sources.get(sourceType) == null){
			try{
				sources.put(sourceType, (Source<?, ?, ?>) sourceType.getConstructor().newInstance());
			}
			catch(Exception ex){
				System.err.printf("Warning : SourceFactory failed to instantiate %s. Returning null.\n", sourceType.toGenericString());
			}
		}
		return sources.get(sourceType);
	}
	
	/**
	 * Returns an array of instances of the given classes
	 * @param sourceTypes
	 * @return
	 */
	public static Source<?, ?, ?> [] getSources(Class<?> sourceTypes []){
		
		Source<?, ?, ?> [] sources = new Source<?, ?, ?> [sourceTypes.length];
		
		for (int i = 0; i < sourceTypes.length; i++){
			sources[i] = getSource(sourceTypes[i]);
		}
		
		return sources;
		
	}
	
}
