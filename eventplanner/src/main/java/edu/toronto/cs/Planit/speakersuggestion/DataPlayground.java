package edu.toronto.cs.Planit.speakersuggestion;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import weka.core.Instances;
import weka.core.converters.ArffSaver;

import com.google.gson.Gson;

import edu.toronto.cs.Planit.speakersuggestion.similarity.TrainingDataCreator;
import edu.toronto.cs.Planit.speakersuggestion.similarity.ci.SourceAdaptor;
import edu.toronto.cs.Planit.speakersuggestion.similarity.sources.Word2VecMaxSimilarity;
import edu.toronto.cs.Planit.speakersuggestion.similarity.sources.Word2VecMeanSimilarity;
import edu.toronto.cs.Planit.speakersuggestion.similarity.sources.Word2VecSimilarityOfMostFrequent;
import edu.toronto.cs.Planit.speakersuggestion.similarity.util.ComparisonRequest;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Time;

public class DataPlayground {
	
	static final String singleCaseFile = "src/main/resources/speaker suggestion/processed cases/tiny.json";
	static final String lowCasesFile = "src/main/resources/speaker suggestion/processed cases/low.json";
	static final String mediumCasesFile = "src/main/resources/speaker suggestion/processed cases/medium.json";
	static final String highCasesFile = "src/main/resources/speaker suggestion/processed cases/high-homogenous.json";
	
	public static void main(String [] args) throws Exception{
		
		/*
		 * Set up
		 */
		
		Contracts.register(new SourceAdaptor(new Word2VecMaxSimilarity(100)));
		Contracts.register(new SourceAdaptor(new Word2VecMeanSimilarity(100)));
		Contracts.register(new SourceAdaptor(new Word2VecSimilarityOfMostFrequent(1)));
		Contracts.register(new SourceAdaptor(new Word2VecSimilarityOfMostFrequent(5)));
		
		TrainingDataCreator wekaDataCreator = new TrainingDataCreator();
		Collection<ComparisonRequest> cases;
		double classification;
		
		/*
		 * Invoke on all the data
		 */
		

		cases = loadCases(lowCasesFile);
		classification = 1d;
		if (cases != null){
			try {
				wekaDataCreator.invokeOnLabeledInput(cases, classification, getBudget());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		cases = loadCases(mediumCasesFile);
		classification = 2d;
		if (cases != null){
			try {
				wekaDataCreator.invokeOnLabeledInput(cases, classification, getBudget());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		cases = loadCases(highCasesFile);
		classification = 3d;
		if (cases != null){
			try {
				wekaDataCreator.invokeOnLabeledInput(cases, classification, getBudget());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
		
		/*
		 * Save the results
		 */
		
		Instances dataSet = wekaDataCreator.getDataSet();
		
		String saveFileName = String.format("src/main/resources/speaker suggestion/dataset/%s - %s.arff",
				dataSet.relationName(),
				Calendar.getInstance().getTime().toString());
		File saveFile = new File(saveFileName);
		try {
			saveFile.createNewFile();
		} catch (IOException e) {
			System.err.printf("Could not save output to a file. Printing to standard output instead.");
		}
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataSet);
		
		try{
			saver.setFile(saveFile);
			saver.writeBatch();
			System.out.printf("\nSaved results to %s\n", saveFileName);
		}
		catch (IOException e){
			e.printStackTrace();
			System.err.println("Could not save dataset");
			System.out.println(dataSet.toString());
		}
	}
	
	/**
	 * Load data from a .json file of comparison requests
	 * @param fileLocation
	 * @return The data or null if it could not be read
	 */
	public static Collection<ComparisonRequest> loadCases(String fileLocation){

		Gson gson = new Gson();
		
		try{
			FileReader fr = new FileReader(fileLocation);
			ComparisonRequest [] cases = gson.fromJson(fr, ComparisonRequest[].class);
			fr.close();
			return Arrays.asList(cases);
		}
		catch (Exception e){
			System.err.printf("Can't read JSON from file %s\n", fileLocation);
			return null;
		}
		
	}
	
	public static Allowance [] getBudget(){
		return new Allowance [] {new Time(1, TimeUnit.MINUTES)};
	}
	
}
