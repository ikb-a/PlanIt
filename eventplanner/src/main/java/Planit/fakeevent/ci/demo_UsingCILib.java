package Planit.fakeevent.ci;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import Planit.dataObjects.Event;
import Planit.dataObjects.util.EventExtractor;
import Planit.fakeevent.resources.SourceFactory;
import Planit.fakeevent.sources.AreaCodeValid;
import Planit.fakeevent.sources.CheckOrganizerFB;
import Planit.fakeevent.sources.EmailNameCooccurence;
import Planit.fakeevent.sources.EventSource;
import Planit.fakeevent.sources.GoogleMapsVenueAddress;
import Planit.fakeevent.sources.OrganizerFaceBookExists;
import Planit.fakeevent.sources.OrganizerWebSiteExists;
import Planit.fakeevent.sources.TimeIsInPlausibleRange;
import Planit.fakeevent.sources.TitleMatchesDescription;
import Planit.fakeevent.sources.TwitterHandleVerified;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaNominalConverter;
import edu.toronto.cs.se.ci.machineLearning.util.training.NominalTrainer;

/**
 * Perform same task as demo.java, using the new CI Library.
 * 
 * @author ikba
 *
 */
public class demo_UsingCILib {

	static private String fileRealEvents = "./src/main/resources/data/event data/chillwall.json";
	static private String fileFakeEvents = "./src/main/resources/data/event data/allFake.json";

	static private String outFilePath = "./src/main/resources/data/ChillwallVFab1.arff";

	public static void main(String[] args) throws IOException {
		// disable annoying HTMLUnit messages produced by UnBubble
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		EventExtractor extractor = new EventExtractor();
		// load the real events
		Event[] realEvents;
		File inFile = new File(fileRealEvents);
		try {
			realEvents = extractor.extractEventsFromJsonFile(inFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// load the fake events
		Event[] fakeEvents;
		inFile = new File(fileFakeEvents);
		try {
			fakeEvents = extractor.extractEventsFromJsonFile(inFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// create the collection of all the sources to ask
		Collection<EventSource> sources = new ArrayList<EventSource>();

		// add all the sources
		sources.add((GoogleMapsVenueAddress) SourceFactory.getSource(GoogleMapsVenueAddress.class));
		sources.add((CheckOrganizerFB) SourceFactory.getSource(CheckOrganizerFB.class));
		sources.add((OrganizerWebSiteExists) SourceFactory.getSource(OrganizerWebSiteExists.class));
		sources.add((OrganizerFaceBookExists) SourceFactory.getSource(OrganizerFaceBookExists.class));
		sources.add((EmailNameCooccurence) SourceFactory.getSource(EmailNameCooccurence.class));
		sources.add((AreaCodeValid) SourceFactory.getSource(AreaCodeValid.class));
		sources.add((TwitterHandleVerified) SourceFactory.getSource(TwitterHandleVerified.class));
		sources.add((TitleMatchesDescription) SourceFactory.getSource(TitleMatchesDescription.class));
		sources.add((TimeIsInPlausibleRange) SourceFactory.getSource(TimeIsInPlausibleRange.class));

		Map<String, Event[]> trainingData = new HashMap<String, Event[]>();
		trainingData.put("0", fakeEvents);
		trainingData.put("1", realEvents);
		// because of the way the original sources were made, they return -1
		// instead of UnknownException. For this reason, -1 must be considered
		// one of the possible nominal values.
		trainingData.put("-1", new Event[] {});

		NominalTrainer<Event, Integer> nt = new NominalTrainer<Event, Integer>(sources);
		nt.createNominalTrainingData(trainingData, new intToNumConverter(), outFilePath);

		/*
		 * //save caches for (Source<Event, Integer, Void> source : sources) {
		 * if (source instanceof EventSource) { EventSource toClose =
		 * (EventSource) source; toClose.close(); } }
		 */
	}

	private static class intToNumConverter implements MLWekaNominalConverter<Integer> {
		@Override
		public String convert(Opinion<Integer, Void> sourceOutput) {
			return String.valueOf(sourceOutput.getValue());
		}
	}

}
