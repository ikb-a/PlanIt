package Planit.dataObjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class for storing the dates and times for the start and end of an event.
 * If any of start time, end time, or end date are not set then they may be given
 * default values.
 * @author wginsberg
 *
 */
public class EventTime {
	
	//raw strings to set when loading from Json with Gson
	private String start_date = "00/00/000";
	private String start_time = "";
	private String end_date = "";
	private String end_time = "";
	
	protected Date startDate = null;
	protected Date endDate = null;
	
	private static SimpleDateFormat dateFormat;
	
	public EventTime(){
		this("", "", "", "");
	}
	
	public EventTime(String start_date, String end_date, String start_time, String end_time){
		this.start_date = start_date;
		this.start_time = start_time;
		this.end_date = end_date;
		this.end_time = end_time;
		if (dateFormat == null){
			dateFormat = new SimpleDateFormat();
		}
	}

	public Date getStartDate(){
		
		//parse the date if it has not been parsed yet
		if (startDate == null){
			
			//construct the string to format into a date
			String toFormat = "";
			//use default value for time if the time doesn't exist
			if (start_time == ""){
				start_time = "12:00 AM";
			}
			toFormat = start_date + " " + start_time;
			
			try {
				startDate = dateFormat.parse(toFormat);
			} catch (ParseException e) {
				System.err.printf("Error parsing date : %s\n", toFormat);
			}
		}
		
		return startDate;
	}

	public Date getEndDate(){
		
		//parse the date if it has not been parsed yet
		if (endDate == null){
			
			//construct the string to parse into a date
			String toFormat = "";
			//use a default value for time if it doesn't exist
			if (end_time == ""){
				end_time = "11:59 PM";
			}
			//use a default value for the end date if it doesn't exist
			if (end_date == ""){
				end_date = start_date;
			}
			toFormat = end_date + " " + end_time;
			
			try {
				endDate = dateFormat.parse(toFormat);
			} catch (ParseException e) {
				System.err.printf("Error parsing date : %s\n", toFormat);
			}
		}
		return endDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
