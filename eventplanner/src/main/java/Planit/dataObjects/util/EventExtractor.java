package Planit.dataObjects.util;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Planit.dataObjects.Event;

/**
 * Class for converting .json files into Event objects
 * Non-static to simplify multithreading (albeit at a cost to memory efficiency)
 * @author ikba
 *
 */
public class EventExtractor {

	/**
	 * Converts the .json file at {@code file} into an array of {@link Event} objects.
	 * @param file
	 * @return Array of events in {@code file}
	 * @throws Exception
	 */
	public Event[] extractEventsFromJsonFile(File file) throws Exception {
		JsonParser parser = new JsonParser();
		// parse file into a json object
		JsonElement jsonElement = parser.parse(new FileReader(file));
		JsonObject eventsJsonObj = jsonElement.getAsJsonObject();

		// extract the JSON array of events
		JsonArray eventsArrayObj = eventsJsonObj.getAsJsonArray("events");

		// convert the JSON array into an Event array, and return
		Gson gson = new Gson();
		Event[] events = gson.fromJson(eventsArrayObj, Event[].class);
		return events;
	}

}
