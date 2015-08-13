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
}
