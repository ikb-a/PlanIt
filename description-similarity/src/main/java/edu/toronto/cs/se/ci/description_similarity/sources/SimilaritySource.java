package edu.toronto.cs.se.ci.description_similarity.sources;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
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

	static final String defaultModelPath = "/home/wginsberg/Downloads/GoogleNews-vectors-negative300.bin";
	
	static WordVectorsImpl wordVecModel;
	
	static Time similarityCost;
	
	abstract public Double getResponse(SimilarityQuestion input) throws UnknownException;
	
	public Opinion<Double, SimilarityTrust> getOpinion(SimilarityQuestion input) throws UnknownException {
		
		System.out.println("Hallo sourceies");
		
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
			catch (IOException e){
				e.printStackTrace();
				return null;
			}
		}
		return wordVecModel;
	}
	
	/**
	 * Loads and returns the pre-trained Google News model.
	 * @return
	 * @throws IOException
	 */
	static Word2Vec getWord2VecGoogleModel() throws IOException{
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
		System.err.printf("DEBUG : %s %s %f", token1, token2, sim);
		return sim;
	}

	/**
	 * Returns a matrix of the similarities of words in two sets.
	 * Each row is a word in words1, each column is a word in words2
	 * @return
	 */
	public double [][] similarityMatrix(List<String> words1, List<String> words2) throws UnknownException{

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
	
	static double max(double [][] values){
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
	
	static double max(double [] values){
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
	
	static double sum(double [] values){
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
	
	static double sum (double [][] values){
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
