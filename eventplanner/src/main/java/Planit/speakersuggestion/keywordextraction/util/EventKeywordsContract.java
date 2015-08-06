package Planit.speakersuggestion.keywordextraction.util;

import java.util.List;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.Contract;

/**
 * The principle contract of the keyword extraction part of speaker suggestion.
 * @author wginsberg
 *
 */
public interface EventKeywordsContract extends Contract<Event, List<String>, Void> {

}
