package PlanIt.monthSuggestion.sources;

import com.google.common.base.Optional;

import PlanIt.monthSuggestion.resources.MonthWithBestTempByCountry;
import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.utils.BasicSource;

public class Temperature extends BasicSource<Event, Month, Void> implements MLMonthSuggestionContract {
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

	@Override
	public String getName() {
		return super.getName() + this.nthBestTemp + "thBest";
	}

}
