package edu.toronto.cs.se.ci.description_similarity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Dollars;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Speaker;
import edu.toronto.cs.se.ci.description_similarity.sources.ConstantSource;
import edu.toronto.cs.se.ci.description_similarity.sources.FullMeanSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.MaxSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.MeanSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.MinOfBestN;
import edu.toronto.cs.se.ci.description_similarity.sources.StdDevOfMaxSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.StdDevOfSimilarity;
import edu.toronto.cs.se.ci.eventObjects.Event;
import edu.toronto.cs.se.ci.selectors.AllSelector;

/**
 * Loads three .json files of similarity questions, executes a CI on them, and outputs a .arff file of the sources' opinions.
 * 
 * The three .json files are titled low.json, medium.json, high.json
 * The files should be located in and conform to the schema in /description-similarity/data/raw_data
 * 
 * @author wginsberg
 *
 */
public class App 
{
	static BufferedWriter logWriter;
	static OutputStream logStream;
	static boolean headerWasWritten = false;
	
	final static String relation_name = "description-similarity";
	final static String questionFileDir = "./data/raw_data/";
	final static String [] classifications = {"low", "medium", "high"};
	
    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException
    {
    	//start logging
    	logWriter = new BufferedWriter(new OutputStreamWriter(getLogStream()));

    	//create the sources
    	ConstantSource classificationSource = new ConstantSource();
    	Contracts.register(classificationSource);
    	Contracts.register(new MaxSimilarity());
    	Contracts.register(new MeanSimilarity()); 
    	Contracts.register(new FullMeanSimilarity());
    	Contracts.register(new StdDevOfMaxSimilarity());
    	Contracts.register(new StdDevOfSimilarity());
    	Contracts.register(new MinOfBestN(2));
    	Contracts.register(new MinOfBestN(3));
    	Contracts.register(new MinOfBestN(4));
    	Contracts.register(new MinOfBestN(5));
    	
    	//create the CI
    	SimilarityAggregator agg = getAggregator();
    	AllSelector<SimilarityQuestion, Double, SimilarityTrust> sel = new AllSelector<SimilarityQuestion, Double, SimilarityTrust>();
    	CI<SimilarityQuestion, Double, SimilarityTrust, Double> ci;
    	ci = new CI<SimilarityQuestion, Double, SimilarityTrust, Double>
    		(SimilarityContract.class, agg, sel);
    	Allowance [] budget = new Allowance [] {new Dollars(BigDecimal.ONE), new Time(20, TimeUnit.MINUTES)};
    	
    	//do a an execution of the CI for each classification and put them all into one file
    	for (int i = 0; i < classifications.length; i++){
    		
    		//set the classification (low=1.0, medium=2.0, high=3.0)
    		classificationSource.setOpinion((double) i);
    		
        	//load the data to execute on
        	List<SimilarityQuestion> questions;
        	String questionsFilePath = questionFileDir + classifications[i] + ".json";
        	try{
        		questions = loadQuestions(questionsFilePath);
        	}
        	catch (FileNotFoundException e){
        		System.err.printf("Could not load questions from %s", questionsFilePath);
        		continue;
        	}
    		
        	//write the head of the ouput file after the first file is loaded.
        	if (!headerWasWritten){
            	writeHeader();
            	headerWasWritten = true;
        	}
        	
        	//do the execution
        	logWriter.flush();
        	for (SimilarityQuestion question : questions){;
        		ci.apply(question, budget).get();
        		logWriter.flush();
        	}
    	}

    	System.out.println("Finished");
    	logWriter.close();

    }
    
    static List<SimilarityQuestion> loadQuestions(File file){
    	System.err.println("Not implemented");
    	return null;
    }
    
    /**
     * Load questions from a json file.
     * The file should exactly contain an array of objects which have "speaker" and "event" objects.
     * @param questionFilePath
     * @return
     * @throws FileNotFoundException
     */
    static List<SimilarityQuestion> loadQuestions(String questionFilePath) throws FileNotFoundException{
    	
    	Gson gson = new Gson();
    	File questionFile = null;
    	questionFile = new File(questionFilePath);
    	List<SimilarityQuestion> questions = Arrays.asList(gson.fromJson(new FileReader(questionFile), SimilarityQuestion[].class));
    	for (SimilarityQuestion q : questions){
    		q.preProcess();
    	}
    	return questions;
    }
    
    /**
     * Load a hard coded set of default questions
     * @return
     */
    static List<SimilarityQuestion> loadQuestions(){

    	Speaker speaker = new Speaker();
    	speaker.setName("Matte Babel");
    	speaker.setProfessionalTitle("Pop-Culture Icon");
    	speaker.setTopics(Arrays.asList(new String [] {"Education", "Environment", "Science", "Media", "Entertainment", "Youth", "Campus"}));
    	
    	Speaker speaker2 = new Speaker();
    	speaker2.setName("Julie Daniluk");
    	speaker2.setProfessionalTitle("Co-Host of Healthy Gourmet Nutrition Expert");
    	speaker2.setTopics(Arrays.asList(new String [] {"Celebrity", "Bestselling Authors", "Environment", "Science","Health"}));  
    	
    	Speaker speaker3 = new Speaker();
    	speaker3.setName("Diana Steele");
    	speaker3.setProfessionalTitle("Registered dietitian and owner of Eating for Energy");
    	speaker3.setTopics(Arrays.asList(new String [] {"Environment", "Science", "Health", "Youth", "Campus"}));  
    	
    	Event event = new Event();
    	event.setTitle("Diwan Restaurant Patio Now Open: Overlooking the Serene Aga Khan Park");
    	event.setDescription("This summer, Toronto’s premiere destination for art and culture invites visitors to enjoy lunch on the patio while taking in the tranquility of the new Aga Khan Park, featuring walking paths and a four-part garden oasis with reflective pools and more than 500 trees. Visitors can indulge in a delicious grilled menu inspired by Iranian, East African, and Asian at Diwan (Persian word for “meeting place”), the Aga Khan Museum’s restaurant.");
    	
    	List<SimilarityQuestion> questions = new ArrayList<SimilarityQuestion>();
    	questions.add(new SimilarityQuestion(speaker, event));
    	questions.add(new SimilarityQuestion(speaker2, event));
    	questions.add(new SimilarityQuestion(speaker3, event));
    	
    	for (SimilarityQuestion q : questions){
    		q.preProcess();
    	}
    	
    	return questions;
    }
    
    static SimilarityAggregator getAggregator() throws IOException{
    	SimilarityAggregator agg = new SimilarityAggregator();
    	agg.setLogStream(getLogStream());
    	return agg;
    }
        
    static OutputStream getLogStream() throws IOException{
    	if (logStream == null){
    		String logFileName = String.format("./data/logs/%s-%s.arff",
    				relation_name,
    				Calendar.getInstance().getTime().toString());
    		File logFile = new File(logFileName);
    		logStream = new FileOutputStream(logFile);
    	}
    	return logStream;
    }
    
    /**
     * Set the stream for the log to be written to. By default a new log file is created and used.
     * @param stream
     */
    static void setLogStream(OutputStream stream){
    	logStream = stream;
    }
    
    static void writeHeader() throws IOException{
    	logWriter.write(String.format("@relation %s\n\n", relation_name));
    	List<Source<SimilarityQuestion, Double, SimilarityTrust>> sources = Contracts.discover(SimilarityContract.class);
    	sources.sort(SourceOrdering.getComparator());
    	for (Source<?,?,?> source : sources){
    		logWriter.write(String.format("@attribute %s numeric\n", source.getName()));
    	}
    	logWriter.write("\n@data\n");
    }
}
