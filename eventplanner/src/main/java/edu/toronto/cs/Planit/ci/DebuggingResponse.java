package edu.toronto.cs.Planit.ci;

import edu.toronto.cs.se.ci.Source;

/**
 * An instance or sub classed instance of DebuggingResponse can be used when it is necessary
 * to keep track of which source produced an opinion, and what arguments lead to the response.
 *
 * @param <I>
 * @param <O>
 * @param <T>
 */
public class DebuggingResponse <I, O, T>{

	protected I args;
	protected O value;
	protected Source<I, O, T> source;
	
	public DebuggingResponse(I args, Source<I, O, T> source, O value) {
		super();
		this.args = args;
		this.value = value;
		this.source = source;
	}

	/**
	 * Returns the source which produced this opinion.
	 * @return
	 */
	public Source<I, O, T> getSource(){
		return source;
	}
	
	/**
	 * Returns the arguments which lead the source to produce this opinion
	 * @return
	 */
	public I getArgs(){
		return args;
	}
	
	/**
	 * Returns the response that this DebuggingResponse wraps around.
	 * @return
	 */
	public O get(){
		return value;
	}
}
