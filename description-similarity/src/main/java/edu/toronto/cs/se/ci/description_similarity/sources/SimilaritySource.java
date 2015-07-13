package edu.toronto.cs.se.ci.description_similarity.sources;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectorsImpl;
import org.deeplearning4j.models.word2vec.Word2Vec;

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

	static final String googleModelPath = "/home/wginsberg/Downloads/GoogleNews-vectors-negative300.bin";
	//static final String wikiModelPath= "/home/wginsberg/Downloads/word2vec/vectors.bin";
	static final String defaultModelPath = googleModelPath;
	
	static WordVectorsImpl wordVecModel;
	
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
			similarityCost = new Time(1, TimeUnit.SECONDS);
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
	 * Returns a model which is used for word vector functions.
	 * @return
	 * @throws IOException
	 */
	static WordVectorsImpl getModel(){
		if (wordVecModel == null){
			try{
				wordVecModel = getWord2VecGoogleModel();
			}
			catch (IOException | InterruptedException e){
				e.printStackTrace();
				return null;
			}
		}
		return wordVecModel;
	}
	
	/**
	 * Loads the model into memory so that it can be immediately accessed by sources in a CI.
	 * @return 
	 */
	static public void prepare(){
		getModel();
	}
	
	static public void close(){
		wordVecModel = null;
	}
	
	/**
	 * Loads and returns the pre-trained Google News model.
	 * @return
	 * @throws IOException
	 */
	static Word2Vec getWord2VecGoogleModel() throws IOException, InterruptedException{
		return WordVectorSerializer.loadGoogleModel(new File(defaultModelPath), true);
	}
	
	/**
	 * Returns a measure of the similarity between two tokens (e.g. words)
	 * @param token1
	 * @param token2
	 * @return
 	 * @throws UnknownException If one word is not found in the vocabulary
	 */
	static public double similarity(String token1, String token2) throws UnknownException{
		double sim = getModel().similarity(token1, token2);
		return sim;
	}

	/**
	 * Returns a matrix of the similarities of words in two sets.
	 * Each row is a word in words1, each column is a word in words2
	 * @return
	 */
	public static double [][] similarityMatrix(List<String> words1, List<String> words2) throws UnknownException{

		double [][] matrix = new double[words1.size()][words2.size()];
		
		double value;
		int i,j;
		for (i = 0; i < words1.size(); i++){
			for (j = 0; j < words2.size(); j++){
				try{
					value = similarity(words1.get(i), words2.get(j));
				}
				catch (UnknownException e){
					value = -1;
				}
				matrix[i][j] = value;
			}
		}
		return matrix;
	}
	
	/**
	 * Returns the top n most related words to a given word.
	 * @param word
	 * @param n
	 * @return
	 */
	public Collection<String> relatedWords(String word, int n) throws UnknownException{
		return getModel().wordsNearest(word, n);
	}

	/**
	 * Returns a matrix of word similarities between the event and speaker keywords in the question object.
	 * @param q
	 * @return
	 */
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
	
	@Override
	public SimilarityTrust getTrust(SimilarityQuestion args,
			Optional<Double> value) {
		return new SimilarityTrust(this);
	}
}
