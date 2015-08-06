package Planit.speakersuggestion.keywordextraction.sources;

import java.util.List;

import com.google.common.base.Optional;

import Planit.dataObjects.Event;
import Planit.speakersuggestion.keywordextraction.util.EventKeywordsContract;
import edu.toronto.cs.se.ci.Adaptor;
import edu.toronto.cs.se.ci.Contract;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Adapts a source which maps a list of words to a list of words so that it may map an event to a list of words.
 * @author wginsberg
 *
 */
public class EventKeywordAdaptor extends Adaptor<Event, List<String>, Void, List<String>, List<String>, Void> implements
		EventKeywordsContract {
	
	public EventKeywordAdaptor(
			Class<? extends Contract<List<String>, List<String>, Void>> around) {
		super(around);
	}
	
	@Override
	public Expenditure[] getCost(Event input,
			Source<List<String>, List<String>, Void> around) throws Exception {
		return around.getCost(input.getWords());
	}

	@Override
	public Opinion<List<String>, Void> getOpinion(Event input,
			Source<List<String>, List<String>, Void> around)
			throws UnknownException {
		return around.getOpinion(input.getWords());
	}

	@Override
	public Void getTrust(Event input, Optional<List<String>> value,
			Source<List<String>, List<String>, Void> around) {
		return around.getTrust(input.getWords(), value);
	}


}
