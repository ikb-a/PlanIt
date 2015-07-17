package edu.toronto.cs.Planit.speakerSuggestion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;


/**
 * A class which facilitates word similarity powered by Word2Vec.
 * The Word2Vec functionality is accessed via interaction with the Word2Vec gensim client, and relies on a running Word2Vec gensim server.
 * @author wginsberg
 *
 */
public class Word2VecSimilarity implements Closeable{

	static Process clientProcess;
	static BufferedWriter clientRequestWriter;
	static BufferedReader clientResponseReader;
	
	public Word2VecSimilarity(){}
	
	/**
	 * Returns a matrix representing the pairwise similarities from two lists of words.
	 * A similarity of -1 indicates words are outside of the model's vocabulary
	 * @return A matrix of word similarities where each row is a word from words1, each column is a word from words2, and each element is the similarity of the two corresponding words
	 */
	public double [][] similarity(List<String> words1, List<String> words2) throws IOException{
		return Word2VecSimilarity.requestSimilarityMatrix(words1, words2);
	}

	/**
	 * Closes the Word2Vec client if it is open.
	 */
	@Override
	public void close(){
		try {
			doneWithClientProcess();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a client process which is ready to take requests.
	 * @return
	 * @throws IOException
	 */
	static private Process getClientProcess() throws IOException{
		
		if (clientProcess == null){
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
	 * @param process
	 * @throws IOException
	 */
	static private void doneWithClientProcess() throws IOException{
		if (clientProcess == null){
			return;
		}
		clientProcess.getInputStream().close();
		clientProcess.getInputStream().close();
		clientProcess.getErrorStream().close();
	}
	
	/**
	 * Requests and returns a word similarity matrix from the client.
	 */
	static private double [][] requestSimilarityMatrix (List<String> words1, List<String> words2) throws IOException{
		
		if (getClientProcess().isAlive() == false){
			//if we have a dead client, setting it to null forces a new one to be executed
			clientProcess = null;
		}
		
		sendRequest(words1, words2);
		
		if (getClientProcess().isAlive() == false){
			throw new IOException("Word2Vec client process died during interaction");
		}
		
		return recieveResponse(words1.size());
	}	
	
	/**
	 * Parses a word similarity request one line at a time and writes it to the client.
	 * @param requestStream The input stream of the client process
	 * @throws IOException 
	 */
	static private void sendRequest(List<String> words1, List<String> words2) throws IOException{
		
		BufferedWriter writer = getClientRequestWriter();
		
		String requestLine;
		requestLine = String.join(",", words1);
		writer.write(requestLine);
		writer.write("\n");
		requestLine = String.join(",", words2);
		writer.write(requestLine);
		writer.write("\n\n");
		
		writer.flush();
	}
	
	/**
	 * Parses a single line of the client process' response.
	 * @param line A single line of comma separated string representations of doubles with no newline character
	 * @return
	 */
	static private double [] parseResponseLine(String line){
		if (line == null){
			return new double [0];
		}
		String [] rawValues = line.split(",");
		double [] parsed = new double [rawValues.length];
		for (int i = 0; i < rawValues.length; i++){
			parsed[i] = Double.parseDouble(rawValues[i]);
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
	private static double [][] recieveResponse(int expectedLines) throws IOException {
		
		BufferedReader errReader = new BufferedReader(new InputStreamReader(clientProcess.getErrorStream()));
		
		for (int i = 0; i < 10; i ++){
			System.out.println(errReader.readLine());
		}
		
		BufferedReader reader = getClientResponseReader();
		double [][] parsed = new double [expectedLines][];
		for (int i = 0; i < expectedLines; i++){
			String line = reader.readLine();
			parsed[i] = parseResponseLine(line);
		}
		return parsed;
	}

	private static BufferedWriter getClientRequestWriter() throws IOException {
		if (clientRequestWriter == null){
			clientRequestWriter = new BufferedWriter(new OutputStreamWriter(getClientProcess().getOutputStream()));
		}
		return clientRequestWriter;
	}

	private static BufferedReader getClientResponseReader() throws IOException {
		if (clientResponseReader == null){
			clientResponseReader = new BufferedReader(new InputStreamReader(getClientProcess().getInputStream()));
		}
		return clientResponseReader;
	}

	
}
