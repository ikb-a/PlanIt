package Planit.speakersuggestion;

import java.io.FileReader;

import Planit.speakersuggestion.similarity.sources.DiscreteSource1;
import Planit.speakersuggestion.similarity.sources.DiscreteSource3;
import Planit.speakersuggestion.similarity.sources.DiscreteSource2;
import Planit.speakersuggestion.similarity.sources.DiscreteSource4;
import Planit.speakersuggestion.similarity.util.ComparisonRequest;

import com.google.gson.Gson;

import edu.toronto.cs.se.ci.UnknownException;

/**
 * Systematically debugging the opinions of CI sources
 * @author wginsberg
 *
 */
public class DebuggingPlayground {

	
	public static void main (String [] args) throws Exception{
		
		String fileName = "src/main/resources/speaker suggestion/processed cases/debugging cases/low.json";
		
		Gson gson = new Gson();
		ComparisonRequest cr;
		cr = gson.fromJson(new FileReader(fileName), ComparisonRequest.class);
		
		try{
			DiscreteSource1.similarity(cr);
		}
		catch (UnknownException e){};
		try{
			DiscreteSource2.similarity(cr);
		}
		catch (UnknownException e){};
		try{
			DiscreteSource3.similarity(cr);
		}
		catch (UnknownException e){};
		try{
			DiscreteSource4.similarity(cr);
		}
		catch (UnknownException e){};
		
		
	}
	
	
}
