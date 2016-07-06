package Planit.fakeevent.sources;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.budget.Expenditure;
import Planit.dataObjects.Event;
import Planit.fakeevent.resources.GMapsEstablishmentHasAddress;
import Planit.fakeevent.resources.SourceFactory;

/**
 * Wrapper for the functionality of the GMapsEstablishmentHasAddress source.
 * 
 * @author will
 *
 */
public class GoogleMapsVenueAddress extends EventSource {

	private GMapsEstablishmentHasAddress googleMaps;

	public GoogleMapsVenueAddress() {
		// googleMaps = new GMapsEstablishmentHasAddress();
		googleMaps = (GMapsEstablishmentHasAddress) SourceFactory.getSource(GMapsEstablishmentHasAddress.class);
	}

	@Override
	public Integer getResponseOnline(Event e) {

		try {
			return googleMaps.getResponse(e);
		} catch (Exception ex) {
			return -1;
		}
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
