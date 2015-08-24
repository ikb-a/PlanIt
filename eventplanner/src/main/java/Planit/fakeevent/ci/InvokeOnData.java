package Planit.fakeevent.ci;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import com.google.gson.Gson;

import Planit.dataObjects.Event;
import Planit.fakeevent.sources.AreaCodeValid;
import Planit.fakeevent.sources.CheckOrganizerFB;
import Planit.fakeevent.sources.ClassifyingSource;
import Planit.fakeevent.sources.EmailNameCooccurence;
import Planit.fakeevent.sources.EventSource;
import Planit.fakeevent.sources.GoogleMapsVenueAddress;
import Planit.fakeevent.sources.OrganizerFaceBookExists;
import Planit.fakeevent.sources.OrganizerWebSiteExists;
import Planit.fakeevent.sources.TimeIsInPlausibleRange;
import Planit.fakeevent.sources.TitleMatchesDescription;
import Planit.fakeevent.sources.TwitterHandleVerified;
import Planit.fakeevent.ci.EventSourceInvoker;
import Planit.fakeevent.resources.SourceFactory;


/**
 * Take a .json file of events and invoke sources on it to produce a .arff file
 * @author wginsberg
 *
 */
public class InvokeOnData {

	static private String IN_FILE = "./data/event data/fabricated-plausible-events.json";
	static private int CLASSIFICATION = 0;
	static private String OUT_FILE = "./data/fabricated-event-test-plausible.arff";
	
	/**
	 * 
	 * @param args [input file, classification, output file]
	 */
	public static void main(String[] args) {

		String eventDataFile;
		int eventClassification;
		String destinationFile;
		
		//organize the paramters
		if (args.length == 3){
			eventDataFile = args[0];
			eventClassification = Integer.getInteger(args[1]);
			destinationFile = args[2];
		}
		else{
			eventDataFile = IN_FILE;
			eventClassification = CLASSIFICATION;
			destinationFile = OUT_FILE;
		}
		
		//load the events
		Gson gson = new Gson();
		Event [] events;
		try {
			Reader reader = new FileReader(eventDataFile);
			events = gson.fromJson(reader, Event[].class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		//create the sources
		ArrayList<EventSource> sources = new ArrayList<EventSource>();
		//classify the events with a classifying source
		ClassifyingSource c = new ClassifyingSource("class (real event)");
		for (int i = 0; i < events.length; i++){
			c.classify(events[i], eventClassification);
		}
		
		//add all the sources
		sources.add(c);
		sources.add((GoogleMapsVenueAddress)SourceFactory.getSource(GoogleMapsVenueAddress.class));
		sources.add((CheckOrganizerFB)SourceFactory.getSource(CheckOrganizerFB.class));
		sources.add((OrganizerWebSiteExists)SourceFactory.getSource(OrganizerWebSiteExists.class));
		//the URL they supplied, not a search for their name on facebook
		sources.add((OrganizerFaceBookExists)SourceFactory.getSource(OrganizerFaceBookExists.class));
		sources.add((EmailNameCooccurence)SourceFactory.getSource(EmailNameCooccurence.class));
		sources.add((AreaCodeValid)SourceFactory.getSource(AreaCodeValid.class));
		sources.add((TwitterHandleVerified)SourceFactory.getSource(TwitterHandleVerified.class));
		sources.add((TitleMatchesDescription)SourceFactory.getSource(TitleMatchesDescription.class));
		sources.add((TimeIsInPlausibleRange)SourceFactory.getSource(TimeIsInPlausibleRange.class));

		//this is messy and it should get fixed
		ArrayList<Event> eventList = new ArrayList<Event> (events.length);
		for (int i = 0; i< events.length; i++){
			eventList.add(events[i]);
		}
		
		//invoke the sources
		EventSourceInvoker invoker = new EventSourceInvoker("Event Plausibility", sources, eventList);
		System.out.println("invoking sources ...\n");
		invoker.invoke();
		
		//close sources to save their cache
		for (int i = 0; i < sources.size(); i++){
			sources.get(i).close();
		}	
		
		//save to a file
		try{
			File outFile = new File(OUT_FILE);
			String outFileComment = String.format("Event data : %s", IN_FILE);
			invoker.saveToArff(outFile, outFileComment);
			System.out.printf("\nSaved results to %s\n", destinationFile);
		}
		catch (IOException ex){
			System.out.println("\nCouldn't open the out file. Dumping to stdout instead:");
			System.out.println(invoker.getFormattedResults());
		}
	}

	
}
