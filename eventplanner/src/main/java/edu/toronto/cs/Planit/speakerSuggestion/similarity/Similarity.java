package edu.toronto.cs.Planit.speakerSuggestion.similarity;

/**
 * Result of a comparison between two Comparables.
 * @author wginsberg
 *
 */
public class Similarity {

	public enum nominal {LOW, MEDIUM, HIGH};
	
	private Double asNumeric;
	private nominal asNominal;
	
	public Similarity(Double value){
		this(value, null);
	}
	
	public Similarity(nominal value){
		this(null, value);
	}
	
	public Similarity(Double valueAsNumeric, nominal valueAsNominal){
		asNumeric = valueAsNumeric;
		asNominal = valueAsNominal;
	}
	
	/**
	 * Compares two Similarities only by their nominal value
	 */
	@Override
	public boolean equals(Object o){
		if (Similarity.class.isInstance(o)){
			Similarity other = (Similarity) o;
			return (other.getNominal() == getNominal());
		}
		else{
			return false;
		}
	}

	/**
	 * Returns the comparison as a double, or null if no such comparison is defined.
	 */
	public Double getNumeric() {
		return asNumeric;
	}

	/**
	 * Returns the nominal value of the comparison, or null if no nominal comparison is defined.
	 */
	public nominal getNominal() {
		return asNominal;
	}

}
