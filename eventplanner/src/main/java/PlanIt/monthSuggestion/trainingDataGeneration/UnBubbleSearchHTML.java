package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import edu.toronto.cs.se.ci.utils.searchEngine.MemoizingSearch;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResult;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResults;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import PlanIt.monthSuggestion.resources.OpenEvalMonthController;
import PlanIt.monthSuggestion.sources.Month;
import PlanIt.monthSuggestion.sources.openEvalThresholdSource;
import PlanIt.monthSuggestion.sources.openEvalThresholdSourceSingleThread;
import Planit.dataObjects.Event;
import Planit.dataObjects.util.EventExtractor;

//TODO: Add verbose/logging setting

//dependencies on CI and PlanIt (PL2016 branch). Add these to Build Path as project dependencies
// html unit 2.22
public class UnBubbleSearchHTML implements GenericSearchEngine {
	private WebClient webClient;

	public static final String BASE_URL = "https://www.unbubble.eu/?q=%s&focus=web&rc=100&rp=%d";
	public static final String CHARSET = "UTF-8";
	private static List<String> ignoreText;
	private static List<Pattern> ignorePattern;
	private Pattern resultsPattern;
	private boolean verbose = false;
	private boolean lazyLinkDiscrimination = true;
	private static final long MIN_SLEEP = 5000;
	private static final long RAND_SLEEP = 2000;
	int currBrowser = 0;

	public static void main(String[] args) throws Exception {
		// disable annoying HTMLUnit messages produced by UnBubble
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		GenericSearchEngine bob = new MemoizingSearch(
				"./src/main/resources/data/monthData/OpenEval/TrainingSearchMemoization.ser", new UnBubbleSearchHTML());

		EventExtractor extractor = new EventExtractor();
		String CIFile = "./src/main/resources/data/monthData/CI/1_January.json";
		// int index = Integer.parseInt(args[0]);
		int index = 0;
		Event[] events = extractor.extractEventsFromJsonFile(new File(CIFile));
		extractor = null;
		Event one = events[index];
		System.out.println("Running on element " + index + " of: " + CIFile);
		System.out.println("January:");
		System.out.println(one.getTitle());
		System.out.println(one.getDescription());

		boolean useMultiThread = true;
		if (useMultiThread) {

			OpenEvalMonthController.setSearchEngine(bob);
			OpenEvalMonthController.setVerbose(true);
			openEvalThresholdSource b = new openEvalThresholdSource(Month.January);

			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nFebruary:");
			b = new openEvalThresholdSource(Month.February);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMarch:");
			b = new openEvalThresholdSource(Month.March);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nApril:");
			b = new openEvalThresholdSource(Month.April);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMay:");
			b = new openEvalThresholdSource(Month.May);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJune:");
			b = new openEvalThresholdSource(Month.June);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJuly:");
			b = new openEvalThresholdSource(Month.July);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nAugust:");
			b = new openEvalThresholdSource(Month.August);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nSeptember:");
			b = new openEvalThresholdSource(Month.September);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nOctober:");
			b = new openEvalThresholdSource(Month.October);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nNovember:");
			b = new openEvalThresholdSource(Month.November);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nDecember:");
			b = new openEvalThresholdSource(Month.December);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}
		} else {

			openEvalThresholdSourceSingleThread b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalJan.arff", 0.25, bob);

			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nFebruary:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalFeb.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMarch:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalMar.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nApril:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalApr.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nMay:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalMay.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJune:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalJun.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nJuly:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalJul.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nAugust:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalAug.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nSeptember:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalSep.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nOctober:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalOct.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nNovember:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalNov.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}

			System.out.println("\nDecember:");
			b = new openEvalThresholdSourceSingleThread(Month.January,
					"./src/main/resources/data/monthData/OpenEval/OpenEvalDec.arff", 0.25, bob);
			System.out.println(one.getTitle());
			System.out.println(one.getDescription());
			try {
				System.out.println(b.getOpinion(one));
			} catch (UnknownException e) {
				e.printStackTrace();
			}
		}
		System.out.println(
				"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public UnBubbleSearchHTML() {
		if (ignoreText == null) {
			ignoreText = new ArrayList<String>(Arrays.asList(new String[] { "",
					"Screen-reader users, click here to turn off Google Instant.", "Sign in", "Videos", "Images",
					"News", "Maps", "More", "Search tools", "Report images", "YouTube", "Instagram", "Facebook",
					"LinkedIn", "Google+", "Feedback", "Help", "Send feedback", "Privacy", "Terms", "Use Google.com",
					"Learn more", "Imprint", "Contact", "Install plug-in", "Sources", "News Archive", "Newsletter",
					"About Unbubble.eu", "Bubble Tags", "8 Good Reasons", "Feature Tour", "Options Menu", "Homepage",
					"Privacy Policy", "Yandex", "Bing", "Web", "Press", "Become a sponsor...", "Mojeek", "Faroo",
					"ExactSeek", "FastBot", "Partner Websites i", "Advertise here..." }));
			ignorePattern = new ArrayList<Pattern>(Arrays.asList(new Pattern[] { Pattern.compile("More results from.*"),
					Pattern.compile("More news for.*"), Pattern.compile("Images for.*"),
					Pattern.compile("More images for.*"), Pattern.compile(".* - YouTube"), Pattern.compile("\\d"),
					Pattern.compile("\\d{2}"), Pattern.compile("\\d{3}"), Pattern.compile("\\d{4}"),
					Pattern.compile(".*Support Unbubble.*"), Pattern.compile("Search at .*"), Pattern.compile("next.*"),
					Pattern.compile(".*Plugin.*") }));

		}
		webClient = new WebClient();
		resultsPattern = Pattern.compile("About (?<hits>[\\d,]+) results");
	}

	private HtmlPage goToNthPage(String searchString, int n)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String stringURL = String.format(BASE_URL, URLEncoder.encode(searchString, CHARSET), n);
		List<BrowserVersion> browsers = new ArrayList<BrowserVersion>(
				Arrays.asList(new BrowserVersion[] { BrowserVersion.INTERNET_EXPLORER_11, BrowserVersion.FIREFOX_38,
						BrowserVersion.INTERNET_EXPLORER_8, BrowserVersion.CHROME }));
		webClient.close();
		// int browser = (int) (Math.random() * (browsers.size() - 1));
		int browser = this.currBrowser;
		if (verbose) {
			System.out.println(browsers.get(browser));
		}
		webClient = new WebClient(browsers.get(browser));
		this.currBrowser++;
		if (this.currBrowser == browsers.size()) {
			this.currBrowser = 0;
		}
		webClient.getOptions().setJavaScriptEnabled(false);
		try {
			long wait = (long) (MIN_SLEEP + RAND_SLEEP * Math.random());

			if (verbose) {
				System.out.println("sleeping for " + wait);
			}
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (verbose)
			System.out.println(stringURL);
		webClient.addRequestHeader("Referer", "https://www.unbubble.eu/");
		return webClient.getPage(stringURL);
	}

	@Override
	public SearchResults search(String searchString) throws IOException {
		return search(searchString, 1);
	}

	private String anchorToURL(HtmlAnchor anc) throws IOException {
		String href = anc.getHrefAttribute();
		if (href.startsWith("https://derefer.unbubble.eu/?u=")) {
			if (verbose)
				System.out.println("By passed " + href);
			return href.replace("https://derefer.unbubble.eu/?u=", "");
		}

		if (!lazyLinkDiscrimination) {
			HtmlPage newPage;
			String urlString = "";
			try {
				try {
					long wait = (long) (MIN_SLEEP + RAND_SLEEP * Math.random());

					if (verbose) {
						System.out.println("sleeping");
					}
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newPage = anc.click();
				if (!newPage.isHtmlPage()) {
					if (verbose)
						System.out.println("Not HTML");
					throw new RuntimeException("Page is not html");
				}
				urlString = newPage.getUrl().toExternalForm();
				if (urlString.startsWith("https://www.google.ca/search?q=")) {
					if (verbose)
						System.out.println("Google alt search");
					throw new RuntimeException("Alternate Search");
				}
				if (verbose) {
					System.out.println("Read: " + anc.asText());
					System.out.println(urlString);
				}
			} catch (Exception e) {
				if (verbose)
					System.out.println("Failed to read: " + anc.asText());
				throw new IOException(e);
			}
			return urlString;
		}
		throw new IOException("Does not start with http");
	}

	private int pageToResults(HtmlPage page) {
		Matcher m = this.resultsPattern.matcher(page.asText());
		if (m.find()) {
			return Integer.parseInt(m.group("hits").replaceAll(",", ""));
		}
		return 0;
	}

	@Override
	public SearchResults search(String searchString, int pageNumber) throws IOException {
		HtmlPage page;
		try {
			page = goToNthPage(searchString, pageNumber);
		} catch (FailingHttpStatusCodeException e) {
			if (verbose) {
				System.out.println("Page " + pageNumber + " for " + searchString + " DNE.");
				System.out.println("Exact Failure: " + e);
			}
			int HTMLCODE = e.getResponse().getStatusCode();
			if (HTMLCODE == 423) {
				System.out.println("RESOURCE LOCKED. WILL WAIT 8 MIN And RETRY");
				try {
					long wait = (long) (5000 + 3000 * Math.random());

					if (verbose) {
						System.out.println("sleeping for " + wait);
					}
					Thread.sleep(wait);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				return search(searchString.toLowerCase(), pageNumber);
			} else if (HTMLCODE == 429) {
				System.out.println("!!!CODE 429 USER MUST UNLOCK!!!");
				Scanner sc = new Scanner(System.in);
				sc.nextLine();
				sc.nextLine();
				sc.close();
				return search(searchString.toLowerCase(), pageNumber);
			}
			return new SearchResults(0, new ArrayList<SearchResult>(), searchString, pageNumber);
		}
		int numHits = pageToResults(page);
		List<HtmlAnchor> anchors = page.getAnchors();
		List<HtmlAnchor> cleanList = cleanAnchors(anchors);

		List<SearchResult> cleanedResults = new ArrayList<SearchResult>();
		for (HtmlAnchor a : cleanList) {
			try {
				String url = anchorToURL(a);
				cleanedResults.add(new SearchResult(a.asText(), url, ""));
			} catch (IOException e) {
			}
		}
		if (verbose) {
			if (cleanedResults.isEmpty()) {
				System.out.println("NoResults for " + searchString);
			} else {
				System.out.println(cleanedResults);
			}
		}

		return new SearchResults(numHits, cleanedResults, searchString, pageNumber);
	}

	@Override
	public SearchResults nextPage(SearchResults previousPage) throws IOException {
		int n = previousPage.getPageNumber() + 1;
		String searchTerm = previousPage.getQuery();
		return search(searchTerm, n);
	}

	@Override
	public String getRawResults() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Usually 100, first page may be larger
	 * 
	 * @return
	 */
	@Override
	public int getPageSize() {
		return 100;
	}

	private List<HtmlAnchor> cleanAnchors(List<HtmlAnchor> allAnchors) {
		List<HtmlAnchor> cleanedList = new ArrayList<HtmlAnchor>();
		Matcher m;

		for (HtmlAnchor a : allAnchors) {
			String name = a.asText().trim();
			if (!ignoreText.contains(name)) {
				boolean good = true;
				for (Pattern p : ignorePattern) {
					m = p.matcher(name);
					if (m.matches()) {
						good = false;
						break;
					}
				}
				if (good) {
					cleanedList.add(a);
				}
			}
		}
		return cleanedList;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}
