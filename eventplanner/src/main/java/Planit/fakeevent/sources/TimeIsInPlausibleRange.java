package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;

public class TimeIsInPlausibleRange extends EventSource {

	// May 4th, 2015
	protected static long originTime = 1430712000000L;
	// May 4, 2025
	long tenYearsInTheFuture = originTime + (10L * 365L * 24L * 60L * 60L * 1000L);

	/**
	 * Returns 1 If all of the following are true: start time > May 4, 2015
	 * start time < end time end time < May 4, 2015 + 10 years
	 */
	@Override
	protected Integer getResponseOnline(Event e) {

		// if start or end time is unknown return -1
		if(e.getStartTime().isEmpty()){
			return -1;
		}
		
		long startTime = e.getTime().getStartDate().getTime();
		
		if(startTime < originTime){
			return 0;
		}
		
		if(e.getEndTime().isEmpty()){
			return -1;
		}
		
		long endTime = e.getTime().getEndDate().getTime();

		if (startTime < endTime && endTime < tenYearsInTheFuture) {
			return 1;
		} else
			return 0;

	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		return new Expenditure[] {};
	}

	@Override
	public Void getTrust(Event args, Optional<Integer> value) {

		return null;
	}

}
