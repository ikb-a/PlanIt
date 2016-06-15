package Planit.fakeevent.randomization;

import Planit.dataObjects.*;

/**
 * This class will randomize an event object and its contents. It can also
 * produce a new random event object. A subclass of Randomization must be
 * supplied to generate the fields randomly.
 * 
 * @author wginsberg
 *
 */
public class EventObjectRandomizer {

	Randomization r;

	public EventObjectRandomizer(Randomization r) {
		this.r = r;
	}

	public Event event() {
		return event(null);
	}

	public Event event(Event e) {

		if (e == null)
			e = Event.createEvent("");

		e.setDescription(r.randomDescription());
		e.setTitle(r.randomTitle());
		e.setUrl(r.randomURL());

		e.setOrganizer(organizer(e.getOrganizer()));
		e.setVenue(venue(e.getVenue()));
		e.setTime(time(e.getTime()));

		return e;
	}

	public EventOrganizer organizer(EventOrganizer o) {

		if (o == null)
			o = new EventOrganizer();

		o.setName(r.randomName());

		o.setContact(contact(o.getContactInfo()));

		return o;
	}

	public EventContact contact(EventContact c) {

		if (c == null)
			c = new EventContact();

		c.setEmail(r.randomEmail());
		c.setFacebookUrl(r.randomFaceBookURL());
		c.setPhone(r.randomPhoneNumber());
		c.setTwitterHandle(r.randomTwitterHandle());
		c.setTwitterUrl(r.randomTwitterURL());
		c.setWebsite(r.randomURL());

		return c;
	}

	public Venue venue(Venue v) {

		if (v == null)
			v = new Venue();

		v.setName(r.randomVenueName());

		v.setAddress(address(v.getAddress()));

		return v;
	}

	public Address address(Address a) {

		if (a == null)
			a = new Address();

		a.setCity(r.randomCity());
		a.setCountry(r.randomCountry());
		a.setPostalCode(r.randomPostalCode());
		a.setProvince(r.randomProvince());
		a.setRoute(r.randomStreetName());
		a.setStreetNumber(r.randomStreetNumber());

		return a;
	}

	public EventTime time(EventTime t) {

		if (t == null) {
			t = new EventTime();
		}

		t.getStartDate().setTime(r.longNumber());
		t.getEndDate().setTime(r.longNumber());

		return t;
	}
}
