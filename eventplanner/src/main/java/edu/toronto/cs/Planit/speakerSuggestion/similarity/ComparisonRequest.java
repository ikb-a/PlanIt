package edu.toronto.cs.Planit.speakerSuggestion.similarity;

/**
 * An instance of ComparisonRequest holds two Comparable objects, with the intent that they will be compared to each other.
 * @author wginsberg
 *
 */
public class ComparisonRequest {

	private Comparable first;
	private Comparable second;
	
	public ComparisonRequest (Comparable first, Comparable second){
		this.first = first;
		this.second = second;
	}

	public Comparable getFirst() {
		return first;
	}

	public Comparable getSecond() {
		return second;
	}
	
}
