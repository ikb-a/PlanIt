package edu.toronto.cs.Planit.ci.ml;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.Planit.ci.DebuggingResponse;

/**
 * A response type for a numeric attribute in a weka data set.
 * @author wginsberg
 */
public class NumericResponse <I, T> extends DebuggingResponse <I, Double, T>{

	public NumericResponse(I args, Source<I, Double, T> source, Double value) {
		super(args, source, value);
	}

}
