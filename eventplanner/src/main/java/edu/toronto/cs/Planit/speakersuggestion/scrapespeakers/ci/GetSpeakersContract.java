package edu.toronto.cs.Planit.speakersuggestion.scrapespeakers.ci;

import java.util.Collection;

import edu.toronto.cs.se.ci.Contract;
import edu.toronto.cs.Planit.dataObjects.Speaker;

public interface GetSpeakersContract extends Contract<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> {

}
