package edu.toronto.cs.se.ci.description_similarity.sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.description_similarity.SimilarityContract;
import edu.toronto.cs.se.ci.description_similarity.SimilarityQuestion;
import edu.toronto.cs.se.ci.description_similarity.SimilarityTrust;

public abstract class SimilaritySource extends Source<SimilarityQuestion, Double, SimilarityTrust> implements SimilarityContract{
	
	static Time similarityCost;
	
	private static HashMap<SimilarityQuestion, double [][]> cache;
	
	abstract public Double getResponse(SimilarityQuestion input) throws UnknownException;
	
	public Opinion<Double, SimilarityTrust> getOpinion(SimilarityQuestion input) throws UnknownException {
		return new Opinion<Double, SimilarityTrust>(getResponse(input), new SimilarityTrust(this));
	}
	
	/**
	 * The expected cost to find the similarity of two words
	 * @return
	 */
	static Time getSimilarityCost(){
		if (similarityCost == null){
			similarityCost = new Time(1, TimeUnit.MILLISECONDS);
		}
		return similarityCost;
	}
	
	static TimeUnit getSimilarityCostTimeUnit(){
		return TimeUnit.SECONDS;
	}
	
	/**
	 * Returns a cost for executing a given number of similarity queries
	 * @param numQueries
	 * @return
	 */
	static Expenditure [] getSimilarityCost(int numQueries){
		TimeUnit unit = getSimilarityCostTimeUnit();
		long unitCost = getSimilarityCost().getDuration(unit);
		return new Expenditure [] {new Time(unitCost * numQueries, unit)};
	}

	/**
	 * Returns a matrix of word similarities based on the words returned from input.getEventWords() and input.getSpeakerWords())
	 * The rows correspond to event words, the columns to speaker words
	 * @param input
	 * @return
	 */
	static public double [][] similarity(SimilarityQuestion input){
		return similarity(input.getEventWords(), input.getSpeakerWords());
	}
	
	static public double similarity(String token1, String token2){
		try{
			return similarity(Arrays.asList(token1), Arrays.asList(token2))[0][0];
		}
		catch (IndexOutOfBoundsException | NullPointerException e){
			return -1;
		}
	}
	
	/**
	 * Returns a measure of the similarity between two lists of words.
	 * A similarity of -1 means one of the words is not in the vocabulary
	 * @param token1
	 * @param token2
	 * @return A matrix of word similarities where each row is a word from words1, each column is a word from words2, and each element is the similarity of the two corresponding words
	 */
	static public double [][] similarity(List<String> words1, List<String> words2){
		//execute the process and get I/O objects
		Process process = null;
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(Arrays.asList(System.getenv("GENSIM_SERVER") + "/client.py"));
			pb.redirectOutput(Redirect.PIPE);
			pb.redirectInput(Redirect.PIPE);
			
			process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		double [][] toReturn = similarity(words1, words2, process.getOutputStream(), process.getInputStream());
		try {
			process.getInputStream().close();
			process.getInputStream().close();
			process.getErrorStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return toReturn;
	}	
	
	/**
 	 * @param requestStream The stream to write the request to
	 * @param responseStream The stream to read the response from
	 * @return
	 */
	static public double [][] similarity(List<String> words1, List<String> words2, OutputStream requestStream, InputStream responseStream){
		
		double [][] matrix = new double [words1.size()][words2.size()];
		for (int i = 0; i < matrix.length; i++){
			Arrays.fill(matrix[i], -1);
		}
		
		try {
	
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(requestStream));
			BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
			
			//send the request
			String request = String.format("%s\n%s\n\n", String.join(",", words1), String.join(",",  words2));			
			writer.write(request);
			writer.close();
			
			//get the response
			String [] lines = new String [words1.size()];
			for (int i = 0; i< lines.length; i++){
				try{
					lines[i] = reader.readLine();
				}
				catch (IOException e){
					break;
				}
			}
			
			//parse the response
			for (int i = 0; i < lines.length; i++){
				String [] values = lines[i].split(",");
				for (int j = 0; j < values.length; j++){
						try{
							matrix[i][j] = Double.parseDouble(values[j]);
						}
						catch (NumberFormatException e){
							matrix[i][j] = -1;
						}
				}
			}
			return matrix;
			
		} catch (IOException | NullPointerException e) {
			return matrix;
		}
	}

	
	/**
	 * Returns a matrix of the similarities of words in two sets.
	 * Each row is a word in words1, each column is a word in words2
	 * @return
	 */
	@Deprecated
	public static double [][] similarityMatrix(List<String> words1, List<String> words2) throws UnknownException{

		double [][] matrix = new double[words1.size()][words2.size()];
		
		double value;
		int i,j;
		for (i = 0; i < words1.size(); i++){
			for (j = 0; j < words2.size(); j++){
				value = similarity(words1.get(i), words2.get(j));
				matrix[i][j] = value;
			}
		}
		return matrix;
	}

	/**
	 * Returns a matrix of word similarities between the event and speaker keywords in the question object.
	 * @param q
	 * @return
	 */
	@Deprecated
	public static double [][] similarityMatrix(SimilarityQuestion q) throws UnknownException{
		if (cache == null){
			cache = new HashMap<SimilarityQuestion, double[][]>();
		}
		if (!cache.containsKey(q)){
			cache.put(q, similarityMatrix(q.getEventWords(), q.getSpeakerWords()));
		}
		return cache.get(q);
	}
	
	static public double max(double [][] values){
		if (values.length < 1){
			return Double.MIN_VALUE;
		}
		double maxValue = max(values[0]);
		if (values.length > 1){
			for (int i = 1; i < values.length; i++){
				double x = max(values[i]);
				if (x > maxValue){
					maxValue = x;
				}
			}
		}
		return maxValue;
	}
	
	static public double max(double [] values){
		if (values.length < 1){
			return Double.MIN_VALUE;
		}
		double maxValue = values[0];
		if (values.length > 1){
			for (int i = 1; i < values.length; i++){
				if (values[i] > maxValue){
					maxValue = values[i];
				}
			}
		}
		return maxValue;
	}
	
	static public double sum(double [] values){
		if (values.length < 1){
			return Double.MIN_VALUE;
		}
		double x = values[0];
		if (values.length > 1){
			for (int i = 1; i < values.length; i++){
				x += values[i];
			}
		}
		return x;
	}
	
	static public double sum (double [][] values){
		if (values.length < 1){
			return Double.MIN_VALUE;
		}
		double x = sum(values[0]);
		if (values.length > 1){
			for (int i = 1; i < values.length; i++){
				x += sum(values[i]);
			}
		}
		return x;
	}
	
	/**
	 * Returns the arithmetic average, ignoring -1 values
	 * @param values
	 * @return
	 */
	static public double mean(double [][] values){
		double total = 0;
		int n = 0;
		for (int i = 0; i < values.length; i++){
			for (int j = 0; j < values[i].length; j++){
				if (values[i][j] != -1d){
					total += values[i][j];
					n++;
				}
			}
		}
		if (total == 0){
			return 0;
		}
		else{
			return total / n;
		}
	}
	
	/**
	 * Returns the arithmetic average, ignoring -1 values
	 * @param values
	 * @return
	 */
	static public double mean(double [] values){
		double total = 0;
		int n = 0;
		for (int i = 0; i < values.length; i++){
			if (values[i] != -1d){
				total += values[i];
				n++;
			}
		}
		if (total == 0){
			return 0;
		}
		else{
			return total / n;
		}
	}
	
	static public double min(double [] values){
		double m = Double.MAX_VALUE;
		for (int i = 0; i < values.length; i++){
			if (values[i] < m){
				m = values[i];
			}
		}
		return m;
	}
	
	@Override
	public SimilarityTrust getTrust(SimilarityQuestion args,
			Optional<Double> value) {
		return new SimilarityTrust(this);
	}
}
