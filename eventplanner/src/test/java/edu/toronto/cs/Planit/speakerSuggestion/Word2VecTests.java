package edu.toronto.cs.Planit.speakerSuggestion;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.toronto.cs.Planit.speakerSuggestion.similarity.Word2Vec;

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
	
}
