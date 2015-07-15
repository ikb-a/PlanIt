package edu.toronto.cs.se.ci.description_similarity;

import java.util.Comparator;

import edu.toronto.cs.se.ci.Source;

/**
 * Compares sources by their names lexographically
 * @author wginsberg
 *
 */
public class SourceOrdering implements Comparator<Source<?, ?, ?>> {

	private SourceOrdering(){
		super();
	}
	
	static private Comparator<Source<?, ?, ?>> instance = null;
	
	public static Comparator<Source<?, ?, ?>> getComparator(){
		if (instance == null){
			instance = new SourceOrdering();
		}
		return instance;
	}
	
	@Override
	public int compare(Source<?, ?, ?> o1, Source<?, ?, ?> o2) {
		int comparison = o1.getName().compareTo(o2.getName());
		return comparison;
	}

}
