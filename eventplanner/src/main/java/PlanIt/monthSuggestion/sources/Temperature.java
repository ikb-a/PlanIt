package PlanIt.monthSuggestion.sources;

import com.google.common.base.Optional;

import PlanIt.monthSuggestion.resources.MonthWithBestTempByCountry;
import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

/**
 * This source returns the month with the nth average temperature closest to 20
 * Celsius. The value for n is defined at construction.
 * 
 * @author ikba
 *
 */
public class Temperature extends BasicSource<Event, Month, Void> implements MLMonthSuggestionContract {
	/**
	 * The n for which this source returns nth best temperature (i.e. nth
	 * closest to 20C). Therefore n is between 1 and 12 inclusive.
	 */
	private int nthBestTemp;

	public Temperature(int n) {
		if (n < 1 || n > 12) {
			throw new IllegalArgumentException(n + "th month does not exist.");
		}
		nthBestTemp = n;
	}

	@Override
	public Month getResponse(Event input) throws UnknownException {
		Venue venue = input.getVenue();
		Address address = venue.getAddress();
		String country = address.getCountry();
		return MonthWithBestTempByCountry.nthMonthWithBestTemp(country, this.nthBestTemp);
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		// TODO: add realistic time expenditure
		return new Expenditure[] {};
	}

	@Override
	public Void getTrust(Event args, Optional<Month> value) {
		return null;
	}

	// NOTE that name includes n so that for each value of n, the name of the
	// source is unique. This is important as each source must have a unique
	// name, as the WEKA classifier takes source name to be attribute name, and
	// each attribute must have a unique name.
	@Override
	public String getName() {
		return super.getName() + this.nthBestTemp + "thBest";
	}

}
