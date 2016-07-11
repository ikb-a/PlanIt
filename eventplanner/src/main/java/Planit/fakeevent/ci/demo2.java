package Planit.fakeevent.ci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.EventContact;
import Planit.dataObjects.EventOrganizer;
import Planit.dataObjects.EventTime;
import Planit.dataObjects.Speaker;
import Planit.dataObjects.Venue;
import Planit.fakeevent.sources.AreaCodeValid;
import Planit.fakeevent.sources.CheckOrganizerFB;
import Planit.fakeevent.sources.EmailNameCooccurence;
import Planit.fakeevent.sources.EventCheckContract;
import Planit.fakeevent.sources.EventSource;
import Planit.fakeevent.sources.GoogleMapsVenueAddress;
import Planit.fakeevent.sources.OrganizerFaceBookExists;
import Planit.fakeevent.sources.OrganizerWebSiteExists;
import Planit.fakeevent.sources.TimeIsInPlausibleRange;
import Planit.fakeevent.sources.TitleMatchesDescription;
import Planit.fakeevent.sources.TwitterHandleVerified;
import edu.toronto.cs.se.ci.Contracts;
import edu.toronto.cs.se.ci.Estimate;
import edu.toronto.cs.se.ci.GenericCI;
import edu.toronto.cs.se.ci.Selector;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.budget.Allowance;
import edu.toronto.cs.se.ci.data.Opinion;
import edu.toronto.cs.se.ci.data.Result;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaAggregator;
import edu.toronto.cs.se.ci.machineLearning.aggregators.MLWekaNominalConverter;
import edu.toronto.cs.se.ci.machineLearning.util.MLWekaNominalAggregator;
import edu.toronto.cs.se.ci.machineLearning.util.MLWekaNominalThresholdAcceptor;
import edu.toronto.cs.se.ci.selectors.AllSelector;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.supervised.instance.ClassBalancer;

public class demo2 {
	public static final String eventTitle = "Brickfete";
	public static final String venueName = "Delta Toronto East";
	private static final String streetNumber = "2035";
	private static final String street = "Kennedy Road"; // Street name
	private static final String city = "Toronto"; // city
	private static final String province = "ON"; // province
	private static final String country = "Canada";
	private static final String postalCode = "M1T 3G2";
	private static final String startDate = "09/07/2016";
	private static final String endDate = "10/07/2016";
	private static final String startTime = "10:00 AM";
	private static final String endTime = "3:00 PM";
	private static final String eventUrl = "http://toronto.brickfete.com/";
	private static final String eventDescription = "Amazing, jaw dropping, outstanding LEGO creations built by hobbyist.These unique and detailed builds include... LEGO Robots & Mindstorms Creations, City Layouts with powered Trains,Space, Star Wars & Scifi Creations,Art, Architecture & Design, Planes, Trains & Automobiles, Historical Buildings and Castles, Pirates, Steampunk and Vikings, Mosaics & Sculptures and more... All made with millions of LEGO bricks by hobbyists from across Canada and parts of USA, Europe and Australia.";
	private static final String eventOrganizerName = "Brickfete";
	private static final String eventOrganizerTwitterHandle = "@brickfete";
	private static final String eventOrganizerTwitterUrl = "https://twitter.com/brickfete";
	private static final String eventOrganizerFacebookUrl = "https://www.facebook.com/Brickfete/";
	private static final String eventOrganizerWebsite = "http://toronto.brickfete.com/";
	private static final String eventOrganizerEmail = "admin@brickfete.com";
	private static final String eventOrganizerPhone = "905-677-9900";
	private static List<Speaker> confirmedSpeakers;
	private static List<String> keywords;
	private static final String TRAINING_DATA_LOCATION = "./src/main/resources/data/CvGandCvREandCvSMerged_Ian.arff";

	public demo2() {
	}

	public static void main(String[] args) {
		confirmedSpeakers = new ArrayList<Speaker>();
		String [] key = new String []{"Brickfete", "Toronto", "Lego"};
		keywords = Arrays.asList(key);
		
		Event test1 = Event.createEvent(eventTitle);
		Address a = new Address(streetNumber, street, city, province, country, postalCode);
		Venue v = new Venue(venueName, a);
		EventTime et = new EventTime(startDate, endDate, startTime, endTime);
		EventContact ec = new EventContact(eventOrganizerTwitterHandle, eventOrganizerTwitterUrl,
				eventOrganizerFacebookUrl, eventOrganizerWebsite, eventOrganizerEmail, eventOrganizerPhone);
		EventOrganizer eo = new EventOrganizer();
		eo.setName(eventOrganizerName);
		eo.setContact(ec);

		test1.setVenue(v).setTime(et).setTitle(eventTitle).setUrl(eventUrl).setDescription(eventDescription)
				.setOrganizer(eo).setConfirmedSpeakers(confirmedSpeakers).setKeyWords(keywords);

		Selector<Event, Integer, Void> sel = new AllSelector<Event, Integer, Void>();
		MLWekaNominalConverter<Integer> converter = new intToString();
		MLWekaAggregator<Integer, String, double[]> agg = null;
		try {
			agg = new MLWekaNominalAggregator<Integer>(converter, TRAINING_DATA_LOCATION, new NaiveBayes());
			agg.addFilter(new ClassBalancer());
			Evaluation result = agg.nFoldCrossValidate(10);
			System.out.println(result.toSummaryString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		// All values are considered OK, no values are considered GOOD. this
		// should force all source to be called.
		MLWekaNominalThresholdAcceptor<String> acc = new MLWekaNominalThresholdAcceptor<String>(0, 1);

		// TODO: Unused sources: CheckOrganizerFBExact; Traning data: time in
		// plausible range is always 0?
		Contracts.register(new AreaCodeValid());
		Contracts.register(new CheckOrganizerFB());
		Contracts.register(new EmailNameCooccurence());
		Contracts.register(new GoogleMapsVenueAddress());
		Contracts.register(new OrganizerFaceBookExists());
		Contracts.register(new OrganizerWebSiteExists());
		Contracts.register(new TimeIsInPlausibleRange());
		Contracts.register(new TitleMatchesDescription());
		Contracts.register(new TwitterHandleVerified());

		GenericCI<Event, Integer, String, Void, double[]> ci = new GenericCI<Event, Integer, String, Void, double[]>(
				EventCheckContract.class, agg, sel, acc);

		Estimate<String, double[]> estimate = ci.apply(test1, new Allowance[] {});
		Result<String, double[]> result = null;
		try {
			result = estimate.get();
			//result = ci.applySync(test1, new Allowance[]{});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		System.out.println("Decision of CI: " + result.getValue());
		double[] probabilities = result.getQuality();
		assert (probabilities.length == 2);
		System.out.println("Probability of fake: " + probabilities[0]);
		System.out.println("Probability of real: " + probabilities[1]);
		
		//Close cache on all sources (Not part of normal CI behaviour):
		List<Source<Event, Integer, Void>> sourcesToClose = Contracts.discover(EventCheckContract.class);
		for(Source<Event, Integer,Void> source: sourcesToClose){
			if(source instanceof EventSource){
				EventSource toClose = (EventSource) source;
				toClose.close();
				//TODO: Add way to remove closed source from contracts.
			}
		}
	}

	public static class intToString implements MLWekaNominalConverter<Integer> {

		@Override
		public String convert(Opinion<Integer, Void> sourceOutput) {
			return String.valueOf(sourceOutput.getValue());
		}

	}
}
