package edu.toronto.cs.Planit.speakersuggestion.similarity.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.toronto.cs.se.ci.budget.basic.Time;


/**
 * A class which facilitates word similarity powered by Word2Vec.
 * The Word2Vec functionality is accessed via interaction with the Word2Vec gensim client, and relies on a running Word2Vec gensim server.
 * The public methods in this class which interact with the Word2Vec client are synchronized, so they will be thread safe.
 * 
 * @author wginsberg
 *
 */
public class Word2Vec implements Closeable{

	private Process clientProcess;
	private BufferedWriter clientRequestWriter;
	private BufferedReader clientResponseReader;
	
	static private Word2Vec singletonInstance = null;
	
	/**
	 * Returns a singleton instance of Word2VecSimilarity.
	 * @return
	 */
	static public synchronized  Word2Vec getInstance(){
		if (singletonInstance == null){
			singletonInstance = new Word2Vec();
		}
		return singletonInstance;
	}
	
	/**
	 * Returns the amount of time needed to perform a single similarity calculation.
	 */
	public Time getComputationTimeNeeded(){
		return new Time(100 ,TimeUnit.NANOSECONDS);
	}
	
	/**
	 * Returns the amount of time needed to perform a given number of similarity calculations.
	 */
	public Time getComputationTimeNeeded(int n){
		long atomic = getComputationTimeNeeded().getDuration(getComputationTimeNeeded().getTimeUnit());
		return new Time(n * atomic, getComputationTimeNeeded().getTimeUnit());
	}
	
	/**
	 * Returns a matrix representing the pairwise similarities from two lists of words.
	 * A similarity of -1 indicates words are outside of the model's vocabulary
	 * @return A matrix of word similarities where each row is a word from words1, each column is a word from words2, and each element is the similarity of the two corresponding words
	 */
	public synchronized double [][] similarity(List<String> words1, List<String> words2) throws IOException{
		return requestSimilarityMatrix(words1, words2);
	}

	/**
	 * Closes the Word2Vec client if it is open.
	 */
	@Override
	public synchronized void close(){
		try {
			doneWithClientProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Requests and returns a word similarity matrix from the client.
	 */
	private double [][] requestSimilarityMatrix (List<String> words1, List<String> words2) throws IOException{
		sendRequest(words1, words2);
		double [][] response = recieveResponse(words1.size());
		close();
		return response;
	}	
	
	/**
	 * Returns a client process which is ready to take requests.
	 * @throws IOException
	 */
	private Process getClientProcess() throws IOException{
		
		if (clientProcess == null || !clientProcess.isAlive()){
			
			clientRequestWriter = null;
			clientResponseReader = null;
			
			ProcessBuilder pb;
			pb = new ProcessBuilder();
			pb.command(Arrays.asList(System.getenv("GENSIM_SERVER") + "/client.py"));
			
			pb.redirectOutput(Redirect.PIPE);
			pb.redirectInput(Redirect.PIPE);
			
			clientProcess = pb.start();
		}
		
		return clientProcess;
	}
	
	/**
	 * Call when the client process is no longer needed to close and clean up the process.
	 */
	private void doneWithClientProcess() throws IOException{
		if (clientProcess == null){
			return;
		}
		clientProcess.getInputStream().close();
		clientProcess.getInputStream().close();
		clientProcess.getErrorStream().close();
		clientProcess = null;
	}
	

	
	/**
	 * Parses a word similarity request one line at a time and writes it to the client.
	 * @param requestStream The input stream of the client process
	 * @throws IOException 
	 */
	private void sendRequest(List<String> words1, List<String> words2) throws IOException{
		
		BufferedWriter writer = getClientRequestWriter();
		
		String requestLine;
		requestLine = String.join(",", words1);
		
		writer.write(requestLine);
		writer.write("\n");
		requestLine = String.join(",", words2);

		writer.write(requestLine);
		writer.write("\n\n");
		
		writer.close();
	}
	
	/**
	 * Parses a single line of the client process' response.
	 * @param line A single line of comma separated string representations of doubles with no newline character
	 * @return
	 */
	private double [] parseResponseLine(String line){
		if (line == null){
			return new double [0];
		}
		String [] rawValues = line.split(",");
		double [] parsed = new double [rawValues.length];
		for (int i = 0; i < rawValues.length; i++){
			try{
				parsed[i] = Double.parseDouble(rawValues[i]);
			}
			catch (NumberFormatException e){
				continue;
			}
		}
		return parsed;
	}
	
	/**
	 * Parses the entire response of the client process one line at a time.
	 * @param responseStream The output stream of the client.
	 * @param expectedLines The number of lines expected as output. No more than this number of lines will be parsed.
	 * @return
	 * @throws IOException 
	 */
	private double [][] recieveResponse(int expectedLines) throws IOException {
		
		BufferedReader reader = getClientResponseReader();
		double [][] parsed = new double [expectedLines][];
		for (int i = 0; i < expectedLines; i++){
			String line = reader.readLine();
			parsed[i] = parseResponseLine(line);
		}
		return parsed;
	}

	private BufferedWriter getClientRequestWriter() throws IOException {
		getClientProcess();
		if (clientRequestWriter == null){
			clientRequestWriter = new BufferedWriter(new OutputStreamWriter(getClientProcess().getOutputStream()));
		}
		return clientRequestWriter;
	}

	private BufferedReader getClientResponseReader() throws IOException {
		getClientProcess();
		if (clientResponseReader == null){
			clientResponseReader = new BufferedReader(new InputStreamReader(getClientProcess().getInputStream()));
		}
		return clientResponseReader;
	}

	
}
