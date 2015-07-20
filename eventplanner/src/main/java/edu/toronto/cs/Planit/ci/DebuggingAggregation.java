package edu.toronto.cs.Planit.ci;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Aggregator;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;

/**
 * This classifier is used to intercept opinions and log them to a stream.
 * 
 * Optionally another aggregator can be supplied such that this aggregator will write each source's opinion to a stream
 * for debugging, and delegate to the supplied aggregator to do a "real" aggregation as well, which will be returned.
 * @author wginsberg
 */
public class DebuggingAggregation <I, O, T, Q> extends AggregatorWrapper<DebuggingResponse<I, O, T>, T, Q>{

	OutputStream outStream;
	BufferedWriter writer;
	Aggregator<O, T, Q> wrappingAround;

	/**
	 * @param wrapAround This aggregator will do the aggregation that is returned by DebuggingAggregation.aggregate()
	 * @param outStream
	 */
	public DebuggingAggregation(Aggregator<DebuggingResponse<I, O, T>, T, Q> wrapAround, OutputStream outStream) {
		super(wrapAround);
		setOutStream(outStream);
	}

	/**
	 * No actual aggregation will be done if this constructor is used; Optional.absent() will always be the result.
	 * @param outStream
	 */
	public DebuggingAggregation(OutputStream outStream) {
		this(null, outStream);
	}

	/**
	 * Write each opinion to the output stream along with the name of its source.
	 */
	@Override
	public void passiveAggregation(
			List<Opinion<DebuggingResponse<I, O, T>, T>> opinions,
			Optional<Result<DebuggingResponse<I, O, T>, Q>> wrappedAggregationResult) {
		
		//prepare to write the opinions
		getWriter();
		
		//write each opinion to the output stream
		for (Opinion<DebuggingResponse<I, O, T>, T> opinion : opinions){
			try {
				writer.write(String.format("(%s, %s)\n",
						opinion.getValue().getSource().getName(),
						opinion.getValue().get().toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void setOutStream(OutputStream outStream) {
		this.outStream = outStream;
		writer = new BufferedWriter(new OutputStreamWriter(outStream));
	}

	public OutputStream getOutStream(){
		if (outStream == null){
			outStream = System.out;
		}
		return outStream;
	}
	
	BufferedWriter getWriter(){
		if (writer == null){
			writer = new BufferedWriter(new OutputStreamWriter(getOutStream()));
		}
		return writer;
	}
}
