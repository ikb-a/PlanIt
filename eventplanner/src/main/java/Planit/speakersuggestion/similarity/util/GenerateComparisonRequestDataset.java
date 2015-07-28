package Planit.speakersuggestion.similarity.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;

import com.google.gson.Gson;

/**
 * Generate a data set of ComparisonRequests from the cases held in main/resources/speaker suggestion/cases/
 * @author wginsberg
 *
 */
public class GenerateComparisonRequestDataset {

	static final String directory = "main/resources/speaker suggestion/cases/";
	
	static Gson gson;
	
	public static void main(String[] args) {
		
		gson = new Gson();
		
		List<ComparisonRequest> cases = new ArrayList<ComparisonRequest>();
		int i = 1;
		while (true){
			try{
				List<ComparisonRequest> newCases = getCases(i, "high");
				cases.addAll(newCases);
			}
			catch (CaseDoesNotExistException e){
				break;
			}
			i++;
		}

		System.out.println(gson.toJson(cases));
		
	}

	static List<ComparisonRequest> getCases(int caseNo, String classType) throws CaseDoesNotExistException{
		
		//case directory
		File caseDir = new File(directory + String.valueOf(caseNo));
		if (!caseDir.exists() || !caseDir.isDirectory()){
			throw new CaseDoesNotExistException();
		}
		
		//event file
		File eventFile = new File(caseDir, "events.json");
		if (!eventFile.exists() || !eventFile.isFile() || !eventFile.canRead()){
			throw new CaseDoesNotExistException();
		}
		
		Event [] events = null;
		
		//load events
		try{
			FileReader reader = new FileReader(eventFile);
			events = gson.fromJson(reader, Event[].class);
			reader.close();
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
		
		List<ComparisonRequest> comparisonCases = new ArrayList<ComparisonRequest>();
		
		//each classification file
		File classFile = null;
		classFile = new File(caseDir, classType + ".json");
		if (classFile.exists()){
			Speaker [] speakers;
			try{
				FileReader reader = new FileReader(classFile);
				speakers = gson.fromJson(reader, Speaker[].class);
				reader.close();
				//construct cases
				for (int p = 0; p< events.length; p++){
					for (int q = 0; q < speakers.length; q++){
						ComparisonRequest comparisonCase = new ComparisonRequest(events[p], speakers[q]);
						comparisonCases.add(comparisonCase);
					}

				}
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
		
		
		
		return comparisonCases;
	}
	
	static class CaseDoesNotExistException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6403652413476668698L;}
}
