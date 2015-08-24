package edu.toronto.cs.Planit.speakerSuggestion;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Planit.speakersuggestion.wordsimilarity.Word2Vec;
import Planit.speakersuggestion.wordsimilarity.util.MatrixUtil;

public class Word2VecTests {
	
	static Word2Vec word2vec;
	List<String> words1 = Arrays.asList(new String [] {"apple", "banana", "canteloupe"});
	List<String> words2 = Arrays.asList(new String [] {"tree", "tropical", "Hawaii"});
	
	@Before
	public void setup(){
		word2vec = new Word2Vec();
	}
	
	@After
	public void cleanup(){
		word2vec.close();
	}
	
	@Test
	public void noUnhandledExceptions(){
		try{
			word2vec.similarity(words1, words2);
		}
		catch (IOException e){
			e.printStackTrace();
		}
		catch (Exception e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void outputNotNull(){
		try{
			double [][] matrix = word2vec.similarity(words1, words2);
			assertNotNull(matrix);
		}
		catch (Exception e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void outputWellFormed(){
		try{
			double [][] matrix = word2vec.similarity(words1, words2);
			for (int i = 0; i < words1.size(); i++){
				for (int j = 0; j < words2.size(); j++){
					assertNotNull(matrix[i][j]);
				}
			}
		}
		catch (IndexOutOfBoundsException e){
			fail(e.getMessage());
		}
		catch (Exception e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void vocabularyExists(){
		try{
			double [][] matrix = word2vec.similarity(words1, words2);
			boolean someNonNeg1 = false;
			for (int i = 0; i < matrix.length; i++){
				for (int j = 0; j < matrix[i].length; j++){
					if (matrix[i][j] != -1){
						someNonNeg1 = true;
					}
				}
			}
			assertTrue(someNonNeg1);
		}
		catch (Exception e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void maximalRowCorrectnessTest(){
		
		double [][] matrix = new double [3][3];
		matrix[0] = new double [] {1d, 2d ,3d};
		matrix[1] = new double [] {2d, 2d ,5d};
		matrix[2] = new double [] {1d, -1d ,-1d};
		int i = MatrixUtil.maximalRow(matrix);
		assertEquals(1, i);
		matrix[0] = new double [] {-1d, -1d ,-1d};
		matrix[1] = new double [] {1d, -1d ,0.5d};
		matrix[2] = new double [] {0.5d, 0.5d ,0d};
		i = MatrixUtil.maximalRow(matrix);
		assertEquals(2, i);
	}
}
