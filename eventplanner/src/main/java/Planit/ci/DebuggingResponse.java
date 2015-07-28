package Planit.ci;

import edu.toronto.cs.se.ci.Source;

/**
 * An instance implementing DebuggingResponse can be used when it is necessary
 * to keep track of which source produced an opinion, and what arguments lead to the response.
 */
public interface DebuggingResponse <I>{

	/**
	 * Returns the source which produced this opinion.
	 * @return
	 */
	public Source<I, ?, ?> getSource();
	
	/**
	 * Returns the arguments which lead the source to produce this opinion
	 * @return
	 */
	public I getArgs();

}
