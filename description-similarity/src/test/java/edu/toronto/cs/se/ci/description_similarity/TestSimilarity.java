package edu.toronto.cs.se.ci.description_similarity;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.toronto.cs.se.ci.description_similarity.sources.SimilaritySource;

public class TestSimilarity {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String s1 = "brick";
		String s2 = "house";
		double a = SimilaritySource.similarity(s1, s2);
		
		System.out.printf("%s and %s have similarity %f\n", s1, s2, a);
		
		assertTrue(a != -1d);

	}
	
	@Test
	public void test2() {
		List<String> words1 = Arrays.asList("pizza", "pasta", "salad");
		List<String> words2 = Arrays.asList("oven", "pot", "bowl");
		double [][] matrix = SimilaritySource.similarity(words1, words2);
		
		if (matrix == null){
			fail();
		}
		
		System.out.printf("Similarity matrix for %s and %s\n", words1.toString(), words2.toString());
		for (int i = 0; i < matrix.length; i++){
			System.out.println(Arrays.toString(matrix[i]));
		}
	}

}
