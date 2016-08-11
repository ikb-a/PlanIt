package Planit.fakeevent.resources;

import java.io.IOException;

import edu.toronto.cs.se.ci.utils.searchEngine.SearchResult;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResults;

public class FBProfileExactlyExists extends FBProfileLooselyExists {

	/**
	 * Returns true if a google search for "<Name> Facebook" returned results
	 * and one result was a Facebook profile page with the exact <Name> as the
	 * page title.
	 */
	@Override
	public Integer getResponse(String name) {

		SearchResults results;
		try {
			results = search.search(name);
		} catch (IOException e) {
			return -1;
		}

		// check the search results
		for (int i = 0; i < Math.min(numResultsToCheck, results.size()); i++) {
			SearchResult result = results.get(i);
			String title = result.getTitle();
			String content = result.getSnippet();

			/*
			 * Check title of page is exactly "<Name> | Facebook" or
			 * "<Name> Profiles | Facebook"
			 */
			if (title.contains(name + " | Facebook") || title.contains(name + " Profiles | Facebook")) {

				if (allowNonPeople) {
					return 1;
				} else {
					if (isNonPerson(content)) {
						return 0;
					} else {
						return 1;
					}
				}
			}

		}
		return 0;
	}

}
