package Planit.speakersuggestion.keywordextraction.util;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

import Planit.dataObjects.Event;
import edu.toronto.cs.se.ci.CI;
import edu.toronto.cs.se.ci.Selector;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * This is a selector to use in a CI which finds keywords.
 * The rule for selection is to supply the Yahoo content analysis source first,
 * and then supply any others.
 * @author wginsberg
 *
 */
public class KeywordSourceSelector implements Selector<Event, List<String>, Void> {

	@Override
	public Optional<Source<Event, List<String>, Void>> getNextSource(
			CI<Event, List<String>, Void, ?>.Invocation invocation) {

		//If Yahoo content analysis has not been queried, then use it
		for (Source<Event, List<String>, Void>  source : invocation.getRemaining()){
			if (isYahooSource(source)){
				return Optional.of(source);
			}
		}
		
		//If Yahoo content analysis gave an opinion, then return no source at all
		for (ListenableFuture<Opinion<List<String>, Void>> listenableOpinion : invocation.getOpinions()){
			try {
				Opinion<List<String>, Void> opinion = listenableOpinion.get();
				Optional<? extends Source<?,?,?>> opinionSource = opinion.getSource();
				
				if (opinionSource.isPresent() && isYahooSource(opinionSource.get())){
					if (opinion.getValue() != null && opinion.getValue().size() > 0){
						return Optional.absent();
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				return Optional.absent();
			}
			
		}
		
		//use any other source
		if (invocation.getRemaining().size() > 0){
			for (Source<Event, List<String>, Void>  source : invocation.getRemaining()){
				return Optional.of(source);
			}
		}
		
		return Optional.absent();
	}

	/**
	 * Returns true if the source is for Yahoo content analysis or is adapted from one
	 * @param source
	 * @return
	 */
	static boolean isYahooSource(Source<?, ?, ?> source){
		return source.getName().contains("ahoo");
	}
	
}
