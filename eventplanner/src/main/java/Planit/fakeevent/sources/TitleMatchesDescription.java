package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;

public class TitleMatchesDescription extends EventSource {
	
	@Override
	public String getName(){
		return "Title matches description";
	}
	
	/**
	 * Checks that words in the event title also appear in its description.
	 * Returns 1 if at least two thirds of the words in the title also appear in the description.
	 */
	@Override
	protected Integer getResponseOnline(Event e) {

		String description = e.getDescription();
		if (description == null){
			return 0;
		}
		
		String [] wordsToCheck = e.getTitle().split(" ");
		if (wordsToCheck == null){
			return 0;
		}
		int numWordsToCheck = (int) (wordsToCheck.length * (2.0d / 3.0d));
		
		int numWordsMatched = 0;
		for (int i = 0; numWordsMatched < numWordsToCheck && i < wordsToCheck.length; i++){
			if (description.contains(wordsToCheck[i])){
				numWordsMatched++;
			}
		}
		
		if (numWordsMatched < numWordsToCheck){
			return 0;
		}
		
		return 1;
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void getTrust(Event args, Optional<Integer> value) {
		// TODO Auto-generated method stub
		return null;
	}

}
