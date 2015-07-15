package edu.toronto.cs.se.ci.description_similarity.sources;

import java.util.Arrays;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.description_similarity.SimilarityQuestion;

/**
 * Uses a word similarity matrix to find the set of n words from the speaker which have the highest similarity with some word of the event.
 * These top n having an ordering in terms of their maximum similarity to some word from the event,
 * this returns the word with the lowest rank in the ordering.
 * 
 * The n is defaulted to 5, and can be changed with the setter method.
 * 
 * When there are less than n words to work with, n will be the maximum possible within this constraint.
 * 
 * @author wginsberg
 *
 */
public class MinOfBestN extends SimilaritySource {

	private int n;
	
	public MinOfBestN(){
		n = 5;
	}
	
	public MinOfBestN(int n){
		this.n = n;
	}
	
	@Override
	public String getName(){
		return String.format("n_th-highest-word-similarity(n=%d)", n);
	}
	
	@Override
	public Double getResponse(SimilarityQuestion input) throws UnknownException {
		
		double [][] matrix = similarity(input);
		
		double [] topN = new double [n];
		Arrays.fill(topN, Double.MIN_VALUE);
		
		//get the top n similarities w.r.t the columns; the speaker words
		for (int col = 0; col < matrix[0].length; col++){
			double max = Double.MIN_VALUE;
			for (int row = 0; row < matrix.length; row++){
				if (matrix[row][col] > max){
					max = matrix[row][col];
				}
			}
			
			addIfMax(topN, max);
		}
		
		if (topN.length < 1){
			throw new UnknownException();
		}
		
		return min(topN);
	}

	/**
	 * Adds d to v if d is greater than some element in v, replacing the minimum of v
	 * @param v
	 * @param d
	 * @return
	 */
	private static double [] addIfMax(double [] v, double d){
		boolean isMax = false;
		int minimalIndex = 0;
		for (int i = 0; i< v.length; i++){
			if (v[i] < d){
				isMax = true;
			}
			if (v[i] < v[minimalIndex]){
				minimalIndex = i;
			}
		}
		if (isMax){
			v[minimalIndex] = d;
		}
		return v;
	}
	
	@Override
	public Expenditure[] getCost(SimilarityQuestion args) throws Exception {
		return getSimilarityCost(args.getEventWords().size() * args.getSpeakerWords().size());
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

}
