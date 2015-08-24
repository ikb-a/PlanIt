package Planit.speakersuggestion.similarity;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import Planit.dataObjects.Event;
import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.SuggestedSpeakers;
import edu.toronto.cs.se.ci.budget.Allowance;

/**
 * Holds the contributional implementation of the similarity part of the speaker suggestion feature.
 * @author wginsberg
 *
 */
public interface SuitabilityJudge {

	/**
	 * Executes a contributional implementation on each speaker to determine if they are suitable for the event.
	 * @param event
	 * @param speakers
	 * @param budget
	 * @return The best speakers that can be suggested
	 * @throws ExecutionException If the execution of the contributional implementation was unsuccessful for every speaker
	 */
	public abstract Collection<Speaker> evaluate(Event event,
			Collection<Speaker> speakers, Allowance[] budget)
			throws ExecutionException;

	/**
	 * Returns an object containing all of the suggested speakers and the quality of each suggestion.
	 * @return
	 */
	public abstract SuggestedSpeakers getSuggestion();

}