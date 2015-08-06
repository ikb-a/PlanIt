package Planit.speakersuggestion.keywordextraction.util;

import java.util.List;

import edu.toronto.cs.se.ci.Contract;

/**
 * A contract which is fulfilled by a source when it can provide a small list of keywords when given
 * a large list of words from a body of text.
 * 
 * The list of words used as input is assumed to have all desired parsing and processing done already.
 * This includes the assumption that each element in the list is either exactly corresponds to an English word
 * as it would appear in a dictionary, or is a proper noun, slang term, or non-word in all lower case English alphabet,
 * and also that stop words have been removed.
 * @author wginsberg
 *
 */
public interface WordListKeywordsContract extends Contract<List<String>, List<String>, Void> {

}
