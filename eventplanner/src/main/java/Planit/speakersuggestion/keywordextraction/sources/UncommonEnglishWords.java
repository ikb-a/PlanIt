package Planit.speakersuggestion.keywordextraction.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import Planit.speakersuggestion.keywordextraction.util.WordListKeywordsContract;

import com.google.common.base.Optional;

import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.budget.basic.Time;
import edu.toronto.cs.se.ci.data.Opinion;

/**
 * Performs keyword extraction by the rule that words which are uncommon in the English language are the keywords.
 * @author wginsberg
 *
 */
public class UncommonEnglishWords extends Source<List<String>, List<String>, Void> implements
		WordListKeywordsContract {

	//A file where on each line there is a word followed by a number representing its rarity in the English language
	private static final String wordListLocation = "src/main/resources/word list/count_1w.txt";
	
	private static Map<String, Long> wordFrequencyTable = null;
	
	private int n;

	public UncommonEnglishWords(int n) {
		super();
		this.n = n;
		try{
			getWordfrequencytable();
		}
		catch (IOException e){
			System.err.printf("Could not load the word list file from %s\nIs the file missing?\n", wordListLocation);
		}
	}
	
	public UncommonEnglishWords() {
		this(3);
	}

	private static Map<String, Long> getWordfrequencytable() throws IOException {
		if (wordFrequencyTable == null){
			wordFrequencyTable = loadFrequencyTable(wordListLocation);
		}
		return wordFrequencyTable;
	}
	/**
	 * Reads the word frequency information from a file
	 * @param fileLocation The location to a word frequency file which has the format "string int\nstring int\n ..."
	 * @throws IOException 
	 */
	private static Map<String, Long> loadFrequencyTable(String fileLocation) throws IOException{
		
		File file = new File(fileLocation);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		Map<String, Long> frequencyTable = new HashMap<String, Long>();
		while (true){
			String line = reader.readLine();
			if (line == null || line.isEmpty()){
				break;
			}
			String [] lineContents = line.split("\\s+");
			String word = lineContents[0];
			Long number = Long.parseLong(lineContents[1]);
			if (lineContents.length != 2){
				System.err.printf("Malformed line in word frequency table : %s\n Is the file formatted correctly?\n", line);
				continue;
			}
			frequencyTable.put(word, number);
		}
		reader.close();
		return frequencyTable;
	}

	/**
	 * Given a list of words, returns the frequency of each with respect to the English language.
	 * @param words A map where each word has its frequency in the English language represented as an integer. This integer can be used to compare different words, but doesn't have a meaning globally.
	 * @return
	 */
	public Map<String, Long> getWordFrequencies(Collection<String> words){
		
		Map<String, Long> f = new HashMap<String, Long>();
		
		for (String word : words){
			if (f.containsKey(word)){
				continue;
			}
			else{
				if (wordFrequencyTable.containsKey(word)){
					f.put(word, wordFrequencyTable.get(word));
				}
			}
		}
		
		return f;
	}
	
	public List<String> getUncommonWords(List<String> input, int n){
		//do the frequency computations
		Map<String, Long> frequencies = getWordFrequencies(input);
		List<Entry<String, Long>> sorted = frequencies.entrySet().stream().sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue())).collect(Collectors.toList());
		
		//get the words to return
		List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < Math.min(n, frequencies.size()); i++){
			toReturn.add(sorted.get(i).getKey());
		}
		return toReturn;
	}
	
	@Override
	public Opinion<List<String>, Void> getOpinion(List<String> input)
			throws UnknownException {
		List<String> toReturn = getUncommonWords(input, n);
		return new Opinion<List<String>, Void> (input, toReturn, getTrust(input, Optional.of(toReturn)), this);
	}
	
	@Override
	public String getName(){
		return "uncommon-english-words-as-keywords";
	}
	
	@Override
	public Expenditure[] getCost(List<String> args) throws Exception {
		return new Expenditure [] {};
	}

	@Override
	public Void getTrust(List<String> args, Optional<List<String>> value) {
		return null;
	}

}
