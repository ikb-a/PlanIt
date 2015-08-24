package Planit.speakersuggestion.keywordextraction.sources;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;
import Planit.speakersuggestion.keywordextraction.util.EventKeywordsContract;

/**
 * This source follows the rule that the city of an event can be used as a keyword for the event.
 * @author wginsberg
 *
 */
public class EventCitySource extends Source<Event, List<String>, Void> implements EventKeywordsContract {

	@Override
	public Opinion<List<String>, Void> getOpinion(Event input)
			throws UnknownException {
		
		try{
			String city = input.getVenue().getAddress().getCity();
			List<String> keywords = Arrays.asList(city);
			return new Opinion<List<String>, Void>(city, keywords, getTrust(input, Optional.of(keywords)), this);
		}
		catch (NullPointerException e){
			throw new UnknownException(e);
		}
		
	}

	@Override
	public String getName(){
		return "event-city-as-keyword";
	}
	
	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		return new Expenditure [] {};
	}

	@Override
	public Void getTrust(Event args, Optional<List<String>> value) {
		return null;
	}


}
