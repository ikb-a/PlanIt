package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;

public class TimeIsInPlausibleRange extends EventSource {

	//May 4th, 2015
	protected static long originTime = 1430712000000L;
	//May 4, 2025
	long tenYearsInTheFuture = originTime + (10 * 365 * 24 * 60 * 60 * 1000);
	
	/**
	 * Returns 1 If all of the following are true:
	 * start time > May 4, 2015	
	 * start time < end time
	 *  end time < May 4, 2015 + 10 years
	 */
	@Override
	protected Integer getResponseOnline(Event e) {

		long startTime = e.getTime().getStartDate().getTime();
		long endTime = e.getTime().getEndDate().getTime();

		if (startTime > originTime &&
				startTime < endTime &&
				endTime < tenYearsInTheFuture){
			return 1;
		}
		else return 0;
		
	}

	@Override
	public String getName(){
		return "Time in plausible range";
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
