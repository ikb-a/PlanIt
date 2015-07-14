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
import edu.toronto.cs.se.ci.data.Speaker;
import edu.toronto.cs.se.ci.description_similarity.sources.MeanSimilarity;
import edu.toronto.cs.se.ci.eventObjects.Event;
import edu.toronto.cs.se.ci.selectors.AllSelector;


public class App 
{
	static BufferedWriter logWriter;
	static OutputStream logStream;
	
    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException
    {
    	setLogStream(System.out);
    	logWriter = new BufferedWriter(new OutputStreamWriter(getLogStream()));
    	
    	//register sources
    	//Contracts.register(new MaxSimilarity());
    	Contracts.register(new MeanSimilarity()); 
    	//Contracts.register(new FullMeanSimilarity());
    	//Contracts.register(new StdDevOfMaxSimilarity());
    	//Contracts.register(new StdDevOfSimilarity());
    	
    	//create the CI
    	SimilarityAggregator agg = getAggregator();
    	AllSelector<SimilarityQuestion, Double, SimilarityTrust> sel = new AllSelector<SimilarityQuestion, Double, SimilarityTrust>();
    	CI<SimilarityQuestion, Double, SimilarityTrust, Double> ci;
    	ci = new CI<SimilarityQuestion, Double, SimilarityTrust, Double>
    		(SimilarityContract.class, agg, sel);
    	Allowance [] budget = new Allowance [] {new Dollars(BigDecimal.ONE), new Time(1, TimeUnit.MINUTES)};
    	
    	//load the data to execute on
    	List<SimilarityQuestion> questions = loadQuestions();
    	
    	//do the execution

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
    		String logFileName = String.format("./data/logs/%s.txt",
    				Calendar.getInstance().getTime().toString());
    		File logFile = new File(logFileName);
    		logStream = new FileOutputStream(logFile);
    	}
    	return logStream;
    }
    
    static void setLogStream(OutputStream stream){
    	logStream = stream;
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
