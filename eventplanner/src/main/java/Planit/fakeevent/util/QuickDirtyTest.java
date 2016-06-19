package Planit.fakeevent.util;

import java.util.ArrayList;
import java.util.List;

import Planit.dataObjects.Address;
import Planit.fakeevent.resources.GMapsGeocode;
import Planit.fakeevent.resources.NearbySearch;
import edu.toronto.cs.se.ci.UnknownException;

public class QuickDirtyTest {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws UnknownException {
		if (false) {
			System.out.println(GoogleSearchJSON.search("Dilbert"));
			System.out.println(GoogleSearchJSON.numberOfHits());
			System.out.println(GoogleSearchJSON.nextResult());
		}

		if (false) {
			YelpSearchJSON.init();
			System.out.println(YelpSearchJSON.searchBusinessByLocation("animals", "Oakville ON", 10));
			System.out.println(YelpSearchJSON.getCategories());
		}
		if (true) {
			Address one = new Address("1550", "Nottinghill Gate", "Oakville", "ON", "Canada", "L6M 1X7");
			GMapsGeocode gmg = new GMapsGeocode();
			System.out.println(gmg.getResponse(one));
		}
		if (false) {
			Address two = new Address("65", "High Park Avenue", "Toronto", "ON", "Canada", "M6P 2R7");
			GMapsGeocode gmg = new GMapsGeocode();
			System.out.println(gmg.getResponse(two));
		}
		if (false) {
			NearbySearch ns = new NearbySearch("Eiffel tower");
			List<Double> latAndLong = new ArrayList<Double>();
			latAndLong.add(48.8584);// how to denote N or S?
			latAndLong.add(2.2945);// how to denote E or W?
			System.out.println(ns.getResponse(latAndLong));
		}
	}
}
