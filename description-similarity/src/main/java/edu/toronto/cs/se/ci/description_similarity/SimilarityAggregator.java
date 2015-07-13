package edu.toronto.cs.se.ci.description_similarity;

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
 * Does an averaging of the opinions, and dumps the opinion results to a log file for further consideration.
 * @author wginsberg
 *
 */
public class SimilarityAggregator implements Aggregator<Double, SimilarityTrust, Double> {

	BufferedWriter log;
	
	public SimilarityAggregator(){
		super();
	}
	
	public SimilarityAggregator(OutputStream logStream){
		super();
		setLogStream(logStream);
	}
	
	public void setLogStream(OutputStream stream){
		log = new BufferedWriter(new OutputStreamWriter(stream));
	}
	
	/**
	 * Logs the results from each opinion, such that they are comma separated, with a newline at the end
	 */
	public Optional<Result<Double, Double>> aggregate(
			List<Opinion<Double, SimilarityTrust>> opinions) {

		double total = 0, average = 0;
		for (int i = 0; i < opinions.size(); i++){
			double value = opinions.get(i).getValue();
			try {
				write(Double.toString(value));
				if (i < opinions.size() - 1){
					write(",");
				}
				else{
					write("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			total += opinions.get(i).getValue();
		}
		average = total / opinions.size();
		
		Optional<Result<Double, Double>> result = Optional.of(new Result<Double, Double>(average, 1d));
		
		try{
			log.flush();
			return result;
		}
		catch (IOException e){
			return result;
		}

	}
	
	private void write(String s) throws IOException{
		if (log == null){
			return;
		}
		log.write(s);
	}
	
}
