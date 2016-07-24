package PlanIt.monthSuggestion.sources;

import com.google.common.base.Optional;

import PlanIt.monthSuggestion.resources.MonthWithLeastRainByCountry;
import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class Precipitation extends BasicSource<Event, Month, Void> {
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

	@Override
	public String getName() {
		return super.getName() + this.nthLeastPrecipitation + "thLeast";
	}

}
