package Planit.fakeevent.ci;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.*;

import Planit.dataObjects.Event;
import Planit.fakeevent.randomization.EnglishEventGenerator;
import Planit.fakeevent.randomization.EventObjectRandomizer;
import Planit.fakeevent.randomization.GibberishEventGenerator;

public class GenerateRandomEvents {

	public static void main(String[] args) throws IOException {

		int numEvents = 5;
		String destination = "./data/event data/random3.json";

		// generate the events
		Event[] events = new Event[numEvents];
		// EventObjectRandomizer randomizer = new EventObjectRandomizer(new
		// GibberishEventGenerator());
		EventObjectRandomizer randomizer = new EventObjectRandomizer(new EnglishEventGenerator());
		// EventObjectRandomizer randomizer = new EventObjectRandomizer(new
		// MutatedEventGenerator(new File ("./data/event data/chillwall.json")
		// ));
		for (int i = 0; i < numEvents; i++) {
			events[i] = randomizer.event();
		}

		// send them to json
		Gson gson = new Gson();
		String jsonEvents = gson.toJson(events);
		String toWrite = String.format("{\"events\":%s}", jsonEvents);

		// save to a file
		BufferedWriter buf = new BufferedWriter(new FileWriter(destination));
		buf.write(toWrite);
		buf.close();

		System.out.println("Saved to " + destination);
	}

}
