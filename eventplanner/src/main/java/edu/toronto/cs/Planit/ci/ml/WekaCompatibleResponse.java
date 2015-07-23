package edu.toronto.cs.Planit.ci.ml;

import edu.toronto.cs.se.ci.Source;

/**
 * A response, meaning the <O> type of some source or ci which is going to be used with weka
 * @author wginsberg
 *
 */
public class WekaCompatibleResponse <I>{
	
	private Double numericValue;
	private String nominalValue;
	private I input;
	private Source<?, ?, ?> originalSource;
	
	/**
	 * Create a new response with a numeric value.
	 * @param source The source which originally lead to this response, possibly after a transformation.
	 * @param input
	 * @param response
	 */
	public WekaCompatibleResponse(Source<?, ?, ?> source, I input, Double response) {
		this.input = input;
		this.originalSource = source;
		this.numericValue = response;
	}

	/**
	 * Create a new response with a nominal value.
	 * @param source
	 * @param input
	 * @param response
	 */
	public WekaCompatibleResponse(Source<?, ?, ?> source, I input, String response) {
		this.input = input;
		this.originalSource = source;
		this.nominalValue = response;
	}
	
	/**
	 * Returns true if this response will be captured in a numeric weka attribute
	 */
	public boolean isNumeric(){
		return numericValue != null;
	}

	/**
	 * Returns true if this response will be captured in a nominal weka attribute
	 */
	public boolean isNominal(){
		return nominalValue != null;
	}
	
	/**
	 * @return The value of this response, or null if it is not defined because this object is a nominal value.
	 */
	public Double getNumeric(){
		return numericValue;
	}
	
	/**
	 * @return The value of this response, or null if it is not defined because this object is a numeric value.
	 */
	public String getNominal(){
		return nominalValue;
	}

	/**
	 * Returns the source that produced this response.
	 */
	public Source<?, ?, ?> getSource() {
		return originalSource;
	}

	/**
	 * Returns the arguments that lead the source to make this response.
	 */
	public I getArgs() {
		return input;
	}

}
