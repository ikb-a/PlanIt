package edu.toronto.cs.se.ci.description_similarity;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.description_similarity.sources.SimilaritySource;

public class TestSimilarity {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		try{
			double a = SimilaritySource.similarity("brick", "house");
			double b = SimilaritySource.similarity("ocean", "house");
			assertTrue(a > b);
		}
		catch (UnknownException e){
			System.err.println("Similarity could not be computed");
		}
	}

}
