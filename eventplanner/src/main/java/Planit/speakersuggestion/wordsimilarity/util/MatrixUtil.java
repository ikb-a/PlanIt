package Planit.speakersuggestion.wordsimilarity.util;

import java.util.Arrays;

/**
 * A class with methods which provide some utility when working with word similarity matrices.
 * @author wginsberg
 *
 */
public class MatrixUtil {

	/**
	 * Returns the largest value in the matrix
	 * @param matrix
	 * @return
	 */
	static public double max(double [][] matrix){
		
		if (matrix == null){
			return 0d;
		}
		
		double maxValue = Double.MIN_VALUE;
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				if (matrix[i][j] > maxValue){
					maxValue = matrix[i][j];
				}
			}
		}
		return maxValue;
	}
	
	/**
	 * Returns the average value of the matrix's entries, normalizing values to the range [0, 1]
	 * Ignores values -1.0
	 * @param matrix
	 * @return
	 */
	static public double mean(double [][] matrix){
		
		if (matrix == null){
			return 0d;
		}
		
		double totalValue = 0.0;
		int totalEntries = 0;
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				if (matrix[i][j] != 1.0d){
					totalValue += matrix[i][j];
					totalEntries ++;
				}
			}
		}
		
		if (totalEntries == 0){
			return 0;
		}
		
		return totalValue / totalEntries;
	}
	
	static public String toString(double [][] matrix){
		if (matrix == null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < matrix.length; i++){
			sb.append(Arrays.toString(matrix[i]));
			if (i < matrix.length - 1){
				sb.append(",\n");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Given a word similarity matrix, returns the index of the row which has the greatest total similarity of all rows.
	 * @param matrix A word similarity matrix, presumably obtained from the similarity() method
	 * @return The index of the maximal row, or -1 if no rows are present
	 */
	public static int maximalRow(double [][] matrix){
		
		if (matrix == null){
			return -1;
		}
		
		int maxRowI = -1;
		double maxRowSum = Integer.MIN_VALUE;
		
		for (int i = 0; i < matrix.length; i++){
			if (matrix[i] == null){
				continue;
			}
			double rowSum = 0;
			for (int j = 0; j < matrix[i].length; j++){
				rowSum += matrix[i][j];
			}
			if (rowSum > maxRowSum){
				maxRowSum = rowSum;
				maxRowI = i;
			}
		}
		
		return maxRowI;
		
	}
}
