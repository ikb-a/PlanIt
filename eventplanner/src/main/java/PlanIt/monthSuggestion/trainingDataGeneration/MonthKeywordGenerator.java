package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.jsoup.Jsoup;

import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import edu.toronto.cs.se.ci.utils.searchEngine.MemoizingSearch;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResult;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResults;

/**
 * A simplified version of OpenEval. This class can answer predicates by being
 * given positive and negative examples. To increase speed, this class uses
 * multiple threads so as to request information from websites while waiting for
 * the response from the search engine.
 * 
 * @author Ian Berlot-Attwell.
 *
 */
public class MonthKeywordGenerator {
	/**
	 * The search engine being used. This search engine will search the keyword
	 * followed by the arguments to the predicate, and the produced links are
	 * used. This variable cannot be {@code null}.
	 */
	GenericSearchEngine search;
	/**
	 * The keyword being used to prepend the arguments to the predicate. Must be
	 * a single word. Cannot be {@code null}. Can be {@code ""} (the empty
	 * string).
	 */
	String keyword;

	/**
	 * The number of pages of results to check using {@link search}. As
	 * different search engines have different limits to the number of pages
	 * that can be requested, this variable must be between 1 and 10 inclusive.
	 */
	int pagesToCheck = 1;

	/**
	 * Whether or not to memoize link contents
	 */
	boolean memoizeLinkContents = false;

	/**
	 * Whether or not to print debug messages
	 */
	boolean verbose = false;

	/**
	 * Map from link name to link contents, only used if
	 * {@link #memoizeLinkContents} is true.
	 */
	ConcurrentHashMap<String, String> memoizedLinkContents;

	/**
	 * The path to which any memoized data is saved
	 */
	private String linkContentsPath;

	/**
	 * The number of link-content reading threads to create. Increasing the
	 * number of link reading threads may accelerate training and evaluation,
	 * but it is limited by available memory, and ultimately by internet speed
	 * and bandwidth.
	 */
	public static final int numOfLinkThreads = 8;

	public MonthKeywordGenerator(String keyword, GenericSearchEngine search) {
		this.keyword = keyword;
		this.search = search;
	}

	public static void main(String[] args) throws IOException {
		// disable annoying HTMLUnit messages produced by UnBubble
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		GenericSearchEngine searchEngine = new MemoizingSearch(
				"./src/main/resources/data/monthData/OpenEval/TrainingSearchMemoizationDec2016.ser", new UnBubbleSearchHTML());
		MonthKeywordGenerator bob = new MonthKeywordGenerator("", searchEngine);

		bob.run(Arrays.asList(new String[] { "usa summer", "usa cheese", "usa aeronautics", "usa lego",
				"united states cheese", "united states aeronautics", "united states lego", "united states music",
				"united states marathon", "united states conference", "united states outdoors",
				"united states wilderness", "united states exercise", "united states camping", "united states fishing",
				"united states culinary delight", "united states parade", "united states marching band",
				"united states convention", "united states Star Trek", "united states comics",
				"united states film festival", "united states painting", "united states fashion", "united states scary",
				"united states stand up comedy", "united states wine", "united states pokemon",
				"united states athletics", "united states indie music", "united states progressive rock",
				"united states banking", "united states workshop", "united states live music",
				"united states outdoor cinema", "united states photography exhibit", "united states art workship",
				"united states renaissance fair", "russia cheese", "russia aeronautics", "russia lego", "russia music",
				"russia marathon", "russia conference", "russia outdoors", "russia wilderness", "russia exercise",
				"russia camping", "russia fishing", "russia culinary delight", "russia parade", "russia marching band",
				"russia convention", "russia Star Trek", "russia comics", "russia film festival", "russia painting",
				"russia fashion", "russia scary", "russia stand up comedy", "russia wine", "russia pokemon",
				"russia athletics", "russia indie music", "russia progressive rock", "russia banking",
				"russia workshop", "russia live music", "russia outdoor cinema", "russia photography exhibit",
				"russia art workship", "russia renaissance fair", "south africa cheese", "south africa aeronautics",
				"south africa lego", "south africa music", "south africa marathon", "south africa conference",
				"south africa outdoors", "south africa wilderness", "south africa exercise", "south africa camping",
				"south africa fishing", "south africa culinary delight", "south africa parade",
				"south africa marching band", "south africa convention", "south africa Star Trek",
				"south africa comics", "south africa film festival", "south africa painting", "south africa fashion",
				"south africa scary", "south africa stand up comedy", "south africa wine", "south africa pokemon",
				"south africa athletics", "south africa indie music", "south africa progressive rock",
				"south africa banking", "south africa workshop", "south africa live music",
				"south africa outdoor cinema", "south africa photography exhibit", "south africa art workship",
				"south africa renaissance fair", "brazil cheese", "brazil aeronautics", "brazil lego", "brazil music",
				"brazil marathon", "brazil conference", "brazil outdoors", "brazil wilderness", "brazil exercise",
				"brazil camping", "brazil fishing", "brazil culinary delight", "brazil parade", "brazil marching band",
				"brazil convention", "brazil Star Trek", "brazil comics", "brazil film festival", "brazil painting",
				"brazil fashion", "brazil scary", "brazil stand up comedy", "brazil wine", "brazil pokemon",
				"brazil athletics", "brazil indie music", "brazil progressive rock", "brazil banking",
				"brazil workshop", "brazil live music", "brazil outdoor cinema", "brazil photography exhibit",
				"brazil art workship", "brazil renaissance fair", "saudi arabia cheese", "saudi arabia aeronautics",
				"saudi arabia lego", "saudi arabia music", "saudi arabia marathon", "saudi arabia conference",
				"saudi arabia outdoors", "saudi arabia wilderness", "saudi arabia exercise", "saudi arabia camping",
				"saudi arabia fishing", "saudi arabia culinary delight", "saudi arabia parade",
				"saudi arabia marching band", "saudi arabia convention", "saudi arabia Star Trek",
				"saudi arabia comics", "saudi arabia film festival", "saudi arabia painting", "saudi arabia fashion",
				"saudi arabia scary", "saudi arabia stand up comedy", "saudi arabia wine", "saudi arabia pokemon",
				"saudi arabia athletics", "saudi arabia indie music", "saudi arabia progressive rock",
				"saudi arabia banking", "saudi arabia workshop", "saudi arabia live music",
				"saudi arabia outdoor cinema", "saudi arabia photography exhibit", "saudi arabia art workship",
				"saudi arabia renaissance fair", "china cheese", "china aeronautics", "china lego", "china music",
				"china marathon", "china conference", "china outdoors", "china wilderness", "china exercise",
				"china camping", "china fishing", "china culinary delight", "china parade", "china marching band",
				"china convention", "china Star Trek", "china comics", "china film festival", "china painting",
				"china fashion", "china scary", "china stand up comedy", "china wine", "china pokemon",
				"china athletics", "china indie music", "china progressive rock", "china banking", "china workshop",
				"china live music", "china outdoor cinema", "china photography exhibit", "germany cheese",
				"germany aeronautics", "germany lego", "germany music", "germany marathon", "germany conference",
				"germany outdoors", "germany wilderness", "germany exercise", "germany camping", "germany fishing",
				"germany culinary delight", "germany parade", "germany marching band", "germany convention",
				"germany Star Trek", "germany comics", "germany film festival", "germany painting", "germany fashion",
				"germany scary", "germany stand up comedy", "germany wine", "germany pokemon", "germany athletics",
				"germany indie music", "germany progressive rock", "germany banking", "germany workshop",
				"germany live music", "germany outdoor cinema", "germany photography exhibit", "germany art workship",
				"germany renaissance fair", "china art workship", "china renaissance fair", "japan cheese",
				"japan aeronautics", "japan lego", "japan music", "japan marathon", "japan conference",
				"japan outdoors", "japan wilderness", "japan exercise", "japan camping", "japan fishing",
				"japan culinary delight", "japan parade", "japan marching band", "japan convention", "japan Star Trek",
				"japan comics", "japan film festival", "japan painting", "japan fashion", "japan scary",
				"japan stand up comedy", "japan wine", "japan pokemon", "japan athletics", "japan indie music",
				"japan progressive rock", "japan banking", "japan workshop", "japan live music", "japan outdoor cinema",
				"japan photography exhibit", "japan art workship", "japan renaissance fair", "united kingdom cheese",
				"united kingdom aeronautics", "united kingdom lego", "united kingdom music", "united kingdom marathon",
				"united kingdom conference", "united kingdom outdoors", "united kingdom wilderness",
				"united kingdom exercise", "united kingdom camping", "united kingdom fishing",
				"united kingdom culinary delight", "united kingdom parade", "united kingdom marching band",
				"united kingdom convention", "united kingdom Star Trek", "united kingdom comics",
				"united kingdom film festival", "united kingdom painting", "united kingdom fashion",
				"united kingdom scary", "united kingdom stand up comedy", "united kingdom wine",
				"united kingdom pokemon", "united kingdom athletics", "united kingdom indie music",
				"united kingdom progressive rock", "united kingdom banking", "united kingdom workshop",
				"united kingdom live music", "united kingdom outdoor cinema", "united kingdom photography exhibit",
				"united kingdom art workship", "united kingdom renaissance fair", "Russia Spring", "Russia Summer",
				"Russia Autumn", "Brazil Last Day of School", "Brazil Cattleya Orchid (Cattleya Labiata)",
				"Brazil Autumn", "Brazil Winter", "Brazil Spring", "Brazil Rainy", "Brazil Cold", "Brazil Rainy",
				"Brazil Tax Season", "Saudi Arabia Summer Vacations", "Saudi Arabia Spring", "Saudi Arabia Summer",
				"Saudi Arabia Autumn", "Saudi Arabia Very Hot", "Saudi Arabia Rainy", "Saudi Arabia Sandstorm",
				"Saudi Arabia Tax Season", "South Africa Autumn", "South Africa Winter", "South Africa Spring",
				"South Africa Nice temperature", "South Africa Cape Town Humid", "South Africa Cape Town Rain",
				"South Africa Tax Season", "United States vacations", "United States Beach", "United States Outdoors",
				"United States Torandos", "United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Spring", "United States Summer",
				"united states autumn", "China comfortable temperature", "China rain season",
				"China First Day Of School", "China Last Day of School", "China Summer Vacations", "China Tornado",
				"China Plum Blossom", "China Spring", "China Summer", "China Autumn", "Japan First Day of School",
				"Japan Pleasant", "Japan Trees Blossoming", "Japan Very humind and hot", "Japan Hot",
				"Japan Tax Season", "Japan Rain", "Japan Last Day of School", "Japan Summer Vacations",
				"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring", "Japan Summer", "Japan Autumn",
				"Germany Knapweed (Centaurea Cyanus)", "Germany Spring", "Germany Summer", "Germany Autumn",
				"United Kingdom Summer Vacations", "United Kingdom Hot", "United Kingdom Cloudy",
				"United Kingdom Rainy", "United Kingdom Humidity Worse", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Spring", "United Kingdom Summer", "United Kingdom Autumn",
				"Australia Very Hot ", "Australia Nice Temperature", "Australia Dry Season", "Australia Tax Season",
				"Australia Last Day of Class", "Australia Flu Season", "Australia Autumn", "Australia Winter",
				"Australia Spring", "Russia First Day of School", "Russia Last Day of School",
				"Russia Summer Vacations", "Russia Camomile (Matricaria Recutita)", "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States Winter", "China Harsh Winter", "China Winter", "Japan Cold", "Japan Hokkaido snow",
				"Japan Winter", "Germany Winter", "United Kingdom Cold", "United Kingdom Snow",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Turnip",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Wet Season",
				"Australia Tropical Cyclones", "Australia Bushfires", "Australia First Day of Class",
				"Australia Summer", "Russia Winter", "Brazil First Day of School", "Brazil Summer Vacations",
				"Brazil Summer", "Brazil Very Hot", "Saudi Arabia Winter", "Saudi Arabia Humid", "South Africa Summer",
				"South Africa Rainy", "South Africa Very Hot", "South Africa Tornados" }));
	}

	public void run(List<String> possibleKeywords) {
		Map<String, Integer[]> occurencesPerMonth = new HashMap<String, Integer[]>();
		for (String possibleKeyword : possibleKeywords) {
			possibleKeyword = possibleKeyword.toLowerCase();
			if (possibleKeyword.startsWith(keyword.toLowerCase() + " ")) {
				possibleKeyword = possibleKeyword.replace(keyword.toLowerCase() + " ", "");
			}
			occurencesPerMonth.put(possibleKeyword, new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		}

		// whether the thread in charge of making queries to the search engine
		// is done
		AtomicBoolean SearchDone = new AtomicBoolean(false);
		
		//Website contents
		List<LinkContentsForSearch> cont = new ArrayList<LinkContentsForSearch>();

		// List of threads in charge of getting website contents from links
		List<LinkContentsThread> listT2 = new ArrayList<LinkContentsThread>();
		/*
		 * List of Lists of lists of website links by the query that created
		 * them. Each List<SearchResults> is used to send links from Thread1
		 * (the thread in charge of querying the search engine) to one of the
		 * threads in listT2.
		 */
		List<List<SearchResults>> t1Tot2Lists = new ArrayList<List<SearchResults>>();

		/*
		 * Each AtomicBoolean in the list corresponds to one of the threads in
		 * listT2. Each represents whether the thread in listT2 has completed
		 * execution.
		 */
		List<AtomicBoolean> LinksDone = new ArrayList<AtomicBoolean>();

		// for each link-reading thread, as dictated by the method's argument
		// numOfLinkThreads:
		for (int x = 0; x < numOfLinkThreads; x++) {
			// Create the list used to send links from the SearchThread t1 to
			// one of the threads in listT2.
			List<SearchResults> res = new ArrayList<SearchResults>();
			t1Tot2Lists.add(res);

			// Create the AtomicBoolean that will be used to indicated whether
			// one of the threads in listT2 has completed
			AtomicBoolean linksDoneBool = new AtomicBoolean(false);
			LinksDone.add(linksDoneBool);

			// Create a new thread object using the list and boolean from above
			LinkContentsThread t2 = new LinkContentsThread(res, SearchDone, linksDoneBool, cont);
			// give it a unique name for debugging
			t2.setName("_" + x);
			// Add the thread to listT2
			listT2.add(t2);
		}

		SearchThread t1 = new SearchThread(possibleKeywords, search, SearchDone, t1Tot2Lists);

		// Start all of the threads.
		(new Thread(t1)).start();
		for (LinkContentsThread t2 : listT2) {
			(new Thread(t2)).start();
		}
		// Wait for the word processing thread to be done placing word bags into
		// the list named wordBags
		synchronized (cont) {
			// System.out.println("synchronized");
			while (!allTrue(LinksDone)) {
				// System.out.println("not all links done");
				if (!cont.isEmpty()) {
					List<LinkContentsForSearch> webpagesContents = new ArrayList<LinkContentsForSearch>();
					webpagesContents.addAll(cont);
					cont.clear();

					for (LinkContentsForSearch contents : webpagesContents) {
						String key = contents.getKeywords().toLowerCase();
						if (key.startsWith(keyword.toLowerCase() + " ")) {
							key = key.replace(keyword.toLowerCase() + " ", "");
						}
						if (!occurencesPerMonth.containsKey(key)) {
							System.err.println("Failed to save: " + key);
						}

						Integer[] resultsPerMonth = occurencesPerMonth.get(key);
						for (String webpage : contents) {
							String webpageLower = webpage.toLowerCase();
							resultsPerMonth[0] += webpageLower.split("january").length - 1;
							resultsPerMonth[1] += webpageLower.split("february").length - 1;
							resultsPerMonth[2] += webpageLower.split("march").length - 1;
							resultsPerMonth[3] += webpageLower.split("april").length - 1;
							// here the split is done in upper case to
							// differentiate "May" from "may"
							resultsPerMonth[4] += webpage.split("May[^A-Za-z]").length - 1;
							/*
							System.out.println("May*****************************************************");
							String[] matches = webpage.split("May[^A-Za-z]");
							for (String match : matches) {
								System.out.println(match + "May");
							}*/
							resultsPerMonth[5] += webpageLower.split("june").length - 1;
							resultsPerMonth[6] += webpageLower.split("july").length - 1;
							resultsPerMonth[7] += webpageLower.split("august").length - 1;
							resultsPerMonth[8] += webpageLower.split("september").length - 1;
							resultsPerMonth[9] += webpageLower.split("october").length - 1;
							resultsPerMonth[10] += webpageLower.split("november").length - 1;
							resultsPerMonth[11] += webpageLower.split("december").length - 1;
							/*
							System.out.println("Dec*****************************************************");
							String[] matchesD = webpageLower.split("december");
							for (String match : matchesD) {
								System.out.println(match + "december");
							}*/
						}

					}
				}

				try {
					// System.out.println("waiting");
					cont.wait();
				} catch (InterruptedException e) {
					// TODO: Determine behaviour
					e.printStackTrace();
				}
			}
			System.out.println(LinksDone);
		}

		List<String> jan = new ArrayList<String>();
		List<String> feb = new ArrayList<String>();
		List<String> mar = new ArrayList<String>();
		List<String> apr = new ArrayList<String>();
		List<String> may = new ArrayList<String>();
		List<String> jun = new ArrayList<String>();
		List<String> jul = new ArrayList<String>();
		List<String> aug = new ArrayList<String>();
		List<String> sep = new ArrayList<String>();
		List<String> oct = new ArrayList<String>();
		List<String> nov = new ArrayList<String>();
		List<String> dec = new ArrayList<String>();

		for (String key : occurencesPerMonth.keySet()) {
			Integer[] occurences = occurencesPerMonth.get(key);
			int index = getMaxIndex(occurences);
			assert (-1 < index && index < 12);
			switch (index) {
			case 0:
				jan.add(key);
				break;
			case 1:
				feb.add(key);
				break;
			case 2:
				mar.add(key);
				break;
			case 3:
				apr.add(key);
				break;
			case 4:
				may.add(key);
				break;
			case 5:
				jun.add(key);
			case 6:
				jul.add(key);
				break;
			case 7:
				aug.add(key);
				break;
			case 8:
				sep.add(key);
				break;
			case 9:
				oct.add(key);
				break;
			case 10:
				nov.add(key);
				break;
			case 11:
				dec.add(key);
				break;
			case -1:
				break;
			}
		}

		System.out.println("January\n" + jan);
		System.out.println("February\n" + feb);
		System.out.println("March\n" + mar);
		System.out.println("April\n" + apr);
		System.out.println("may\n" + may);
		System.out.println("June\n" + jun);
		System.out.println("July\n" + jul);
		System.out.println("August\n" + aug);
		System.out.println("September\n" + sep);
		System.out.println("October\n" + oct);
		System.out.println("November\n" + nov);
		System.out.println("December\n" + dec);
		System.out.println();
		for (String key : occurencesPerMonth.keySet()) {
			System.out.println(key + ": " + new ArrayList<Integer>(Arrays.asList(occurencesPerMonth.get(key))));
		}

	}

	private int getMaxIndex(Integer[] occurences) {
		int maxIndex = -1;
		int maxValue = 0;
		for (int x = 0; x < occurences.length; x++) {
			if (occurences[x] > maxValue) {
				maxIndex = x;
				maxValue = occurences[x];
			}
		}
		return maxIndex;
	}

	public void setVerbose(boolean verb) {
		this.verbose = verb;
	}

	public boolean getVerbose() {
		return this.verbose;
	}

	/**
	 * Returns the number of pages of search results that SimpleOpenEval checks
	 * for each argument to the predicate.
	 */
	public int getPagesToCheck() {
		return pagesToCheck;
	}

	/**
	 * Sets the number of pages of search results that SimpleOpenEval checks for
	 * each argument to the predicate. The number of pages must be between 1 and
	 * 10 inclusive.
	 */
	public void setPagesToCheck(int numOfPages) {
		if (numOfPages < 0 || numOfPages > 10) {
			throw new IllegalArgumentException();
		}
		this.pagesToCheck = numOfPages;
	}

	public GenericSearchEngine getSearch() {
		return search;
	}

	public void setSearch(GenericSearchEngine search) {
		this.search = search;
	}

	public String getKeyword() {
		return keyword;
	}

	/**
	 * Disables memoization of link contents and removes all saved link contents
	 * from memory
	 */
	public void setMemoizeLinkContentsOff() {
		this.memoizeLinkContents = false;
		this.memoizedLinkContents = null;
	}

	/**
	 * Enables memoization. If {@code pathToMemoizationFile} does not exist but
	 * the folder does, then the file will be created. If the file does exist,
	 * than it will be loaded to memory.
	 * 
	 * @param pathToMemoizationFile
	 *            Path to where memoized link information should be stored. If
	 *            the file exists then it should contain a serialized HashMap
	 *            that maps from String link names to String link contents.
	 * @throws IOException
	 *             If the file that pathToMemoizationFile cannot be
	 *             created/read.
	 */
	public void setMemoizeLinkContentsOn(String pathToMemoizationFile) throws IOException {
		try {
			this.memoizedLinkContents = loadMemoizedContents(pathToMemoizationFile);
		} catch (EOFException e) {
			this.memoizedLinkContents = new ConcurrentHashMap<String, String>();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
		this.linkContentsPath = pathToMemoizationFile;
		this.memoizeLinkContents = true;
	}

	public boolean getMemoizeLinkContents() {
		return this.memoizeLinkContents;
	}

	/**
	 * Loads the Map of link name to link contents. If the file does not exist,
	 * but the directory does, then the file is created.
	 * 
	 * @param path
	 *            The path containing the serialized map
	 * @return Map from link name to link contents
	 * @throws IOException
	 *             If path is an invalid path, or if the file cannot be read
	 *             from.
	 * @throws ClassNotFoundException
	 *             If the serialized object is not a known object. This should
	 *             not occur unless an invalid file is given.
	 */
	private ConcurrentHashMap<String, String> loadMemoizedContents(String path)
			throws IOException, ClassNotFoundException {
		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
			return new ConcurrentHashMap<String, String>();
		}

		try (FileInputStream fis = new FileInputStream(path)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<String, String> result = (ConcurrentHashMap<String, String>) ois.readObject();
			ois.close();
			return result;
		} catch (EOFException e) {
			return new ConcurrentHashMap<String, String>();
		}
	}

	/**
	 * Saves {@link #memoizedLinkContents} to {@link #linkContentsPath}.
	 * 
	 * @throws IOException
	 *             If {@link #linkContentsPath} is an invalid path, or if the
	 *             file cannot be written to.
	 */
	public void saveMemoizedContents() throws IOException {
		try (FileOutputStream fos = new FileOutputStream(linkContentsPath)) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.memoizedLinkContents);
			oos.close();
		}
	}

	private boolean allTrue(List<AtomicBoolean> linksDone) {
		for (AtomicBoolean b : linksDone) {
			if (!b.get()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This Runnable searches words, and returns the resulting links.
	 * 
	 * @author Ian Berlot-Attwell
	 */
	private class SearchThread implements Runnable {

		/**
		 * Whether this thread has finished searching and returning links.
		 * Should be false until the {@link #run()} method has been called no
		 * further SearchResults will be added to any of the lists in
		 * {@link #ListSearchResults}.
		 */
		AtomicBoolean amDone;

		/**
		 * A list of words to be searched with
		 * {@link MonthKeywordGenerator.keyword}. As words are searched they are
		 * removed from the list. This thread does not write to examples.
		 */
		List<String> examples;

		/**
		 * The search engine to use to make queries.
		 */
		GenericSearchEngine search;

		/**
		 * When search results are produced by searching a word in
		 * {@link #examples}, the links are divided amongst each {@code List
		 * <SearchResults>} in this list. This class only writes to these lists,
		 * it does not read any of them.
		 */
		List<List<SearchResults>> ListSearchResults;

		/**
		 * Create a SearchThread that, when run, takes a String for
		 * {@code examples}, searches it using {@code search} preceded with
		 * {@link MonthKeywordGenerator.keyword}, divides the links into
		 * {@code ListSearchResults.size()} roughly equal parts and places each
		 * part into a list in {@code ListSearchResults}. Once there are no more
		 * words in {@code examples} and all of the links have been distributed
		 * amongst the lists in {@code ListSearchResults}, then
		 * {@code searchThreadIsDone} is set to true.
		 * 
		 * @param examples
		 *            The strings to be searched
		 * @param search
		 *            The search engine to search the Strings in
		 *            {@code examples}.
		 * @param searchThreadIsDone
		 *            Whether the thread is finished searching words and
		 *            distributing the links. Must be false during contruction.
		 * @param ListSearchResults
		 *            The lists amongst which the found links are distributed.
		 *            Cannot be empty or {@code null}.
		 */
		public SearchThread(List<String> examples, GenericSearchEngine search, AtomicBoolean searchThreadIsDone,
				List<List<SearchResults>> ListSearchResults) {
			assert (searchThreadIsDone.get() == false);
			assert (!ListSearchResults.isEmpty());

			amDone = searchThreadIsDone;
			this.examples = examples;
			this.search = search;
			this.ListSearchResults = ListSearchResults;
		}

		/**
		 * For each String in {@code examples}, searches it using {@code search}
		 * preceded with {@link MonthKeywordGenerator.keyword}, divides the
		 * links into {@code ListSearchResults.size()} roughly equal parts and
		 * places each part into a list in {@code ListSearchResults}. Once there
		 * are no more words in {@code examples} and all of the links have been
		 * distributed amongst the lists in {@code ListSearchResults}, then
		 * {@code searchThreadIsDone} is set to true.
		 */
		@Override
		public void run() {
			Iterator<String> itr = examples.iterator();
			// iterates through each String in examples
			while (itr.hasNext()) {
				String ex = itr.next();
				try {
					if (verbose)
						System.out.println("1. Starting search");
					SearchResults resultsForEx;
					if (keyword.isEmpty()) {
						resultsForEx = search.search(ex);
					} else {
						resultsForEx = search.search(keyword + " " + ex);
					}
					if (verbose)
						System.out.println("1. Done search");

					List<SearchResults> splitResults = splitNWay(resultsForEx, ListSearchResults.size());

					// places a unique part of the results in list in
					// ListSearchResults, and notifies the sublist.
					for (int x = 0; x < ListSearchResults.size(); x++) {
						List<SearchResults> r = ListSearchResults.get(x);
						synchronized (r) {
							if (verbose)
								System.out.println("1. Updating Search Results #" + x);
							r.add(splitResults.get(x));
							r.notifyAll();
						}
					}

					// If there are no further Strings in example, sets amDone
					// to true and notifies all sublists in ListSearchResults
					Object lock = new Object();
					synchronized (lock) {
						if (!itr.hasNext()) {
							if (verbose)
								System.out.println("1. setting Done to true");
							amDone.set(true);
							for (List<SearchResults> sr : ListSearchResults) {
								synchronized (sr) {
									sr.notifyAll();
								}
							}
						}
					}

				} catch (IOException e) {
					Object lock = new Object();
					synchronized (lock) {
						if (verbose)
							System.err.println("Searching " + ex + " falied");

						/*
						 * If there are no further Strings in example, sets
						 * amDone to true and notifies all sublists in
						 * ListSearchResults
						 */
						if (!itr.hasNext()) {
							if (verbose)
								System.out.println("1. setting Done to true (last search failed)");
							amDone.set(true);

							for (List<SearchResults> sr : ListSearchResults) {
								synchronized (sr) {
									sr.notifyAll();
								}
							}
						}
					}
				}
			}
		}

		/**
		 * Takes a SearchResults (which is a list of links), and splits it into
		 * n SearchResults, each of roughly equal size. {@code n} must be
		 * {@code >0}.
		 */
		public List<SearchResults> splitNWay(SearchResults toSplit, int n) {
			assert (n > 0);

			List<SearchResults> result = new ArrayList<SearchResults>(n);

			// retrive non-link information stored in toSplit
			int hits = toSplit.getHits();
			String query = toSplit.getQuery();
			int pageNumber = toSplit.getPageNumber();

			// Create n empty SearchResults each containing the same non-link
			// information
			for (int x = 0; x < n; x++) {
				List<SearchResult> temp = new ArrayList<SearchResult>();
				result.add(new SearchResults(hits, temp, query, pageNumber));
			}

			// Distributes links amongst the n empty SearchResults evenly.
			int index = 0;
			while (index + n <= toSplit.size()) {
				for (int x = 0; x < n; x++) {
					result.get(x).add(toSplit.get(index + x));
				}
				index += n;
			}

			// Places the remaining links (0 <= remaining links < n) one in each
			// SearchResults until there are no further links
			for (; index < toSplit.size(); index++) {
				result.get(index % n).add(toSplit.get(index));
			}

			return result;
		}
	}

	/**
	 * A Runnable that, when run, removes SearchResults objects from a list,
	 * searches the links stored within, the passes the link's contents along
	 * with the query that produced the link to a separate list.
	 * 
	 * @author Ian Berlot-Attwell
	 */
	private class LinkContentsThread implements Runnable {
		/**
		 * List of SearchResults objects. This list should be the output of a
		 * {@link MonthKeywordGenerator.SearchThread}
		 */
		List<SearchResults> searchResults;

		/**
		 * Whether the {@link MonthKeywordGenerator.SearchThread} adding new
		 * links to {@link #searchResults} has no further links to add.
		 */
		AtomicBoolean searchDone;

		/**
		 * Whether {@link #searchResults} is empty and {@link #searchDone} is
		 * true.
		 */
		AtomicBoolean amDone;

		/**
		 * List of LinkContentsForSearch objects. Each of these objects contains
		 * the text body of a link, along with the search query that produced
		 * the link.
		 */
		List<LinkContentsForSearch> linkContents;

		/**
		 * The name for this thread. The name is used in the debugging print
		 * statements to differentiate between the different
		 * {@link MonthKeywordGenerator.SearchThread} threads running.
		 */
		String name = "";

		/**
		 * Creates a new Runnable that, when run, removes SearchResults objects
		 * from a {@code searchResuts}, searches the links stored within, and
		 * then passes the link's contents along with the query that produced
		 * the link to {@code linkContents}. This behaviour continues until
		 * {@code searchResuts} is empty and and {@code SearchThreadIsDone} is
		 * true, at which point {@code LinkContentsIsDone} is set to true.
		 * <p>
		 * Note that whenever a new element is added to {@code linkContents}, or
		 * when the thread is finished executing, notifyAll is called on
		 * {@code linkContents}.
		 * 
		 * @param searchResults
		 *            List of SearchResults objects. This list should be the
		 *            output of a {@link MonthKeywordGenerator.SearchThread} .
		 *            This list should have notifyAll() called upon it whenever
		 *            new elements are added to it, or when
		 *            {@code SearchThreadIsDone} is set to true.
		 * @param SearchThreadIsDone
		 *            Whether the {@link MonthKeywordGenerator.SearchThread}
		 *            adding new links to {@code searchResults} has no further
		 *            links to add.
		 * @param LinkContentsIsDone
		 *            Whether {@code searchResults} is empty and
		 *            {@code SearchThreadIsDone} is true.
		 * @param linkContents
		 *            List of LinkContentsForSearch objects. Each of these
		 *            objects contains the text body of a link, along with the
		 *            search query that produced the link. This list is notified
		 *            whenever this thread adds to it, or is finished executing.
		 */
		public LinkContentsThread(List<SearchResults> searchResults, AtomicBoolean SearchThreadIsDone,
				AtomicBoolean LinkContentsIsDone, List<LinkContentsForSearch> linkContents) {
			this.searchResults = searchResults;
			searchDone = SearchThreadIsDone;
			amDone = LinkContentsIsDone;
			this.linkContents = linkContents;
		}

		/**
		 * Sets the name used in debug messages to differentiate this thread
		 * from other instances of the same class.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Removes SearchResults from {@link #searchResults} until the list is
		 * empty and {@link #searchDone} is true. For each SearchResult removed
		 * from {@link #searchResults}, the links are read and their contents
		 * are added to {@link #linkContents}, at which point notifyAll() is
		 * called on {@link #linkContents}.
		 * <p>
		 * When {@link #searchResults} is empty and {@link #searchDone} is true
		 * and there are no futher links that need to be read, then
		 * {@link #amDone} is set to true and notifyAll is called on
		 * {@link #linkContents}.
		 */
		@Override
		public void run() {
			List<SearchResults> toProcess = new ArrayList<SearchResults>();
			while (true) {
				// If there are no more links to process, either get more links,
				// or return.
				if (toProcess.isEmpty()) {
					if (verbose)
						System.out.println("2." + name + " No results to process");

					// Synchronize on the SearchResults. This means that
					// SearchThread CANNOT have searchResults & searchDone in
					// a contradictory state.
					synchronized (searchResults) {
						// If there are not futher search results
						if (searchResults.isEmpty()) {
							if (verbose)
								System.out.println("2." + name + " SearchResults is empty");
							// ... and search is done, then this thread is done
							if (searchDone.get()) {
								synchronized (linkContents) {
									if (verbose)
										System.out.println("2." + name + " SearchThread is done, so am I");
									amDone.set(true);
									linkContents.notifyAll();
									return;
								}
							}
							// ... otherwise wait for more results
							try {
								if (verbose)
									System.out.println("2." + name + " Waiting on SearchThread");
								searchResults.wait();
								if (verbose)
									System.out.println("2." + name + " Done Waiting");

								if (searchDone.get() && searchResults.isEmpty()) {
									synchronized (linkContents) {
										if (verbose)
											System.out.println(
													"2." + name + " Done waiting. SearchThread is done, so am I");
										amDone.set(true);
										linkContents.notifyAll();
										return;
									}
								}
								assert (!searchResults.isEmpty());
							} catch (InterruptedException e) {
								// TODO: Determine correct behaviour
								return;
							}
						}

						toProcess.addAll(searchResults);
						searchResults.clear();
					}
				}
				assert (!toProcess.isEmpty());

				for (Iterator<SearchResults> itr = toProcess.iterator(); itr.hasNext();) {
					SearchResults curr = itr.next();
					itr.remove();
					// Creates a object that represents the contents of the
					// link, and also records that the link was found by
					// searching curr.getQuery()
					LinkContentsForSearch contents = new LinkContentsForSearch(curr.getQuery());
					for (SearchResult link : curr) {
						// if link memoization is being used, and the links
						// contents are known, then add the known results and
						// continue to the next link
						if (memoizeLinkContents && link.getLink() != null
								&& memoizedLinkContents.containsKey(link.getLink())) {
							String savedContents = (memoizedLinkContents.get(link.getLink()));
							if (!savedContents.isEmpty()) {
								contents.add(savedContents);
							}
							continue;
						}

						try {
							if (verbose)
								System.out.println("2." + name + " Reading: " + link);
							// read the contents of the website
							// TODO: set jsoup so as to reject any website that
							// request authentication
							String websiteAsString = Jsoup.connect(link.getLink())
									.userAgent(
											"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0")
									.referrer("http://www.google.com").get().text();
							if (!websiteAsString.isEmpty()) {
								if (verbose)
									System.out.println("2." + name + " Adding result to contents");
								contents.add(websiteAsString);
								if (memoizeLinkContents) {
									memoizedLinkContents.put(link.getLink(), websiteAsString);
								}
							}
						} catch (Exception e) {
							if (verbose)
								System.err.println("2." + name + " Unable to read " + link.getLink() + " CAUSE: " + e);
							if (memoizeLinkContents) {
								memoizedLinkContents.put(link.getLink(), "");
							}
						}
					}

					// once all the links in the list toProccess are completed,
					// update and notify the linkContents list.
					synchronized (linkContents) {
						if (verbose)
							System.out.println("2." + name + " Updating linkContents");
						linkContents.add(contents);
						linkContents.notifyAll();
						if (verbose)
							System.out.println("2." + name + " Notified");
					}
				}
			}
		}
	}

	/**
	 * This class represents the contents of the links produced by a query, as
	 * well as the query used to find the links. Each element in the set is the
	 * contents of a website found by searching {@link #keywords}.
	 * 
	 * @author Ian Berlot-Attwell
	 *
	 */
	@SuppressWarnings("serial")
	private static class LinkContentsForSearch extends HashSet<String> {
		final String keywords;

		public LinkContentsForSearch(String keywords) {
			this.keywords = keywords;
		}

		public String getKeywords() {
			return keywords;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LinkContentsForSearch)) {
				return false;
			}

			LinkContentsForSearch lc = (LinkContentsForSearch) o;
			return (super.equals((HashSet<String>) o) && this.getKeywords().equals(lc.getKeywords()));
		}

		@Override
		public String toString() {
			return keywords + ": " + super.toString();
		}

	}
}
