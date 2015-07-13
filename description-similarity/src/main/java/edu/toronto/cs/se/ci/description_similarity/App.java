package edu.toronto.cs.se.ci.description_similarity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.budget.basic.Dollars;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.description_similarity.sources.MaxSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.MeanSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.StdDevOfMaxSimilarity;
import edu.toronto.cs.se.ci.description_similarity.sources.StdDevOfSimilarity;
import edu.toronto.cs.se.ci.selectors.AllSelector;


public class App 
{
	static BufferedWriter logWriter;
	static OutputStream logStream;
	
    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException
    {
    	//register sources
    	Contracts.register(new MaxSimilarity());
    	Contracts.register(new MeanSimilarity());    	
    	Contracts.register(new StdDevOfMaxSimilarity());
    	Contracts.register(new StdDevOfSimilarity());
    	
    	//create the CI
    	SimilarityAggregator agg = getAggregator();
    	AllSelector<SimilarityQuestion, Double, SimilarityTrust> sel = new AllSelector<SimilarityQuestion, Double, SimilarityTrust>();
    	CI<SimilarityQuestion, Double, SimilarityTrust, Double> ci;
    	ci = new CI<SimilarityQuestion, Double, SimilarityTrust, Double>
    		(SimilarityContract.class, agg, sel);
    	Allowance [] budget = new Allowance [] {new Dollars(BigDecimal.ONE), new Time(10, TimeUnit.MINUTES)};
    	
    	//load the data to execute on
    	List<SimilarityQuestion> questions = loadQuestions();
    	
    	//do the execution
    	
    	logWriter = new BufferedWriter(new OutputStreamWriter(getLogStream()));
    	//logWriter = new BufferedWriter(new OutputStreamWriter(System.out));
    	
    	writeHeader();
    	logWriter.flush();
    	for (SimilarityQuestion question : questions){
        	System.out.println("Ask ...");
    		ci.apply(question, budget).get();
    		logWriter.flush();
    	}
    	System.out.println("Finished");
    	
    	logWriter.close();
    }
    
    static List<SimilarityQuestion> loadQuestions(File file){
    	System.err.println("Not implemented");
    	return null;
    }
    
    static List<SimilarityQuestion> loadQuestions(){
    	ArrayList<SimilarityQuestion> questions = new ArrayList<SimilarityQuestion>();
    	
    	String eventTitle;
    	String name;
    	String professionalTitle;
    	List<String> topics;
    	
    	eventTitle = "Taste the Town Tours - July 2015";
    	
    	name = "Matte Babel";
    	professionalTitle = "Pop-Culture Icon";
    	topics = Arrays.asList(new String [] {"Education", "Environment & Science", "Media & Entertainment", "Youth & Campus"});
    	questions.add(new SimilarityQuestion(eventTitle, name, professionalTitle, topics));
    	
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
    		String logFileName = String.format("./data/logs/%s.txt",
    				Calendar.getInstance().getTime().toString());
    		File logFile = new File(logFileName);
    		logStream = new FileOutputStream(logFile);
    	}
    	return logStream;
    }
    
    static void writeHeader() throws IOException{
    	logWriter.write("@relation description-similarity\n\n");
    	List<Source<SimilarityQuestion, Double, SimilarityTrust>> sources = Contracts.discover(SimilarityContract.class);
    	for (Source<?,?,?> source : sources){
    		logWriter.write(String.format("@attribute %s numeric\n", source.getName()));
    	}
    	logWriter.write("\n@data\n");
    }
}
