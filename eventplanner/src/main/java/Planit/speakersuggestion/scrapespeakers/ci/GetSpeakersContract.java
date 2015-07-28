package Planit.speakersuggestion.scrapespeakers.ci;

import java.util.Collection;

import Planit.dataObjects.Speaker;
import edu.toronto.cs.se.ci.Contract;

public interface GetSpeakersContract extends Contract<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> {

}
