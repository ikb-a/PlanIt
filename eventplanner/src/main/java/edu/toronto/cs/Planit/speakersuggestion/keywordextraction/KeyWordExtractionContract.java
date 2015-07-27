package edu.toronto.cs.Planit.speakersuggestion.keywordextraction;

import java.util.List;

import edu.toronto.cs.se.ci.Contract;

/**
 * A contract for extracting keywords out of a list of words in a document.
 * The list of words used as input is assumed to have all desired parsing and processing done already.
 * This includes the assumption that each element in the list is either exactly corresponds to an English word
 * as it would appear in a dictionary, or is a proper noun, slang term, or non-word in all lower case English alphabet,
 * and also that stop words have been removed.
 * @author wginsberg
 *
 */
public interface KeyWordExtractionContract extends Contract<List<String>, List<String>, Void> {

}
