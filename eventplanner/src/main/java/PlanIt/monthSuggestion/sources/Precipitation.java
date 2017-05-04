package PlanIt.monthSuggestion.sources;

import com.google.common.base.Optional;

import PlanIt.monthSuggestion.resources.MonthWithLeastRainByCountry;
import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

/**
 * This source returns the month with the nth least precipitation. The value for
 * n is defined at construction.
 * 
 * @author ikba
 *
 */
public class Precipitation extends BasicSource<Event, Month, Void> implements MLMonthSuggestionContract {
	/**
	 * The n for which this source returns nth least precipitation. Therefore n
	 * is between 1 and 12 inclusive.
	 */
	private int nthLeastPrecipitation;

	public Precipitation(int n) {
		if (n < 1 || n > 12) {
			throw new IllegalArgumentException(n + "th month does not exist.");
		}
		nthLeastPrecipitation = n;
	}

	@Override
	public Month getResponse(Event input) throws UnknownException {
		Venue venue = input.getVenue();
		Address address = venue.getAddress();
		String country = address.getCountry();
		return MonthWithLeastRainByCountry.nthMonthWithLeastRain(country, this.nthLeastPrecipitation);
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
		return super.getName() + this.nthLeastPrecipitation + "thLeast";
	}

}
