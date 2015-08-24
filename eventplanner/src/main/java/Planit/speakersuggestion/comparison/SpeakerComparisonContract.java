package Planit.speakersuggestion.comparison;

import edu.toronto.cs.se.ci.Contract;

/**
 * The result is a double which should take on the value 1.0 or 2.0 meaning that speaker 1 or speaker 2 is better.
 * @author wginsberg
 *
 */
public interface SpeakerComparisonContract extends Contract<SpeakerComparisonRequest, Double, Void> {

}
