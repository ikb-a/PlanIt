package Planit.dataObjects.util;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Planit.dataObjects.Event;

//Non-static to simplify multithreading (albeit at a cost to memory efficiency)
public class EventExtractor {

	public Event[] extractEventsFromJsonFile(File file) throws Exception {
		JsonParser parser = new JsonParser();
		// parse file into a json object
		JsonElement jsonElement = parser.parse(new FileReader(file));
		JsonObject eventsJsonObj = jsonElement.getAsJsonObject();

		// extract the JSON array of events
		JsonArray eventsArrayObj = eventsJsonObj.getAsJsonArray("events");

		// convet the JSON array into an Event array, and return
		Gson gson = new Gson();
		Event[] events = gson.fromJson(eventsArrayObj, Event[].class);
		return events;
	}

}
