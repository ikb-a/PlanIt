package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResult;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResults;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class UnBubbleSearchHTML implements GenericSearchEngine {
	private WebClient webClient;

	public static final String BASE_URL = "https://www.unbubble.eu/?q=%s&focus=web&rc=100&rp=%d";
	public static final String CHARSET = "UTF-8";
	private static List<String> ignoreText;
	private static List<Pattern> ignorePattern;
	/**
	 * Currently a memory leak (scanner is never closed). Adding a static
	 * scanner is just a quick fix to a bug where after receiving a 429 HTML code for
	 * the second time, System.in would already be closed due to the scanner
	 * having been closed.
	 */
	private static Scanner sc = null;
	private Pattern resultsPattern;
	private boolean verbose = false;
	/**
	 * Whether or not UnBubble should extract snippets.
	 */
	// TODO: For some reason snippet extraction does not work on windows???
	private boolean extractSnippets = true;
	private boolean lazyLinkDiscrimination = true;
	private static final long MIN_SLEEP = 5000;
	private static final long RAND_SLEEP = 2000;
	int currBrowser = -1;

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		// disable annoying HTMLUnit messages produced by UnBubble
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		UnBubbleSearchHTML bob = new UnBubbleSearchHTML();
		// HtmlPage page = bob.goToNthPage("Lego", 1);
		// System.out.println(page.asText());
		System.out.println(bob.search("ksp danny"));
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
		if(sc==null){
			sc = new Scanner(System.in);

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
		if (this.currBrowser == -1) {
			this.currBrowser = (int) (Math.random() * (browsers.size() - 1));

		}
		// int browser = (int) (Math.random() * (browsers.size() - 1));

		int browser = this.currBrowser;
		if (verbose) {
			System.out.println(browsers.get(browser));
			System.out.println(browsers.get(browser).getUserAgent());
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
				System.err.println("!!!CODE 429 USER MUST UNLOCK!!!");
				System.out.println(e);
				System.out.println(webClient.getBrowserVersion());
				System.out.println(webClient.getBrowserVersion().getUserAgent());
				System.out.println(searchString);
				System.out.println("Once unlocked, double tap ENTER");
				sc.nextLine();
				sc.nextLine();
				return search(searchString.toLowerCase(), pageNumber);
			}
			return new SearchResults(0, new ArrayList<SearchResult>(), searchString, pageNumber);
		}
		int numHits = pageToResults(page);
		List<HtmlAnchor> anchors = page.getAnchors();
		List<HtmlAnchor> cleanList = cleanAnchors(anchors);
		Set<String> doneURL = new HashSet<String>();

		List<SearchResult> cleanedResults = new ArrayList<SearchResult>();
		for (HtmlAnchor a : cleanList) {
			try {
				String url = anchorToURL(a);
				if (doneURL.contains(url)) {
					continue;
				} else {
					doneURL.add(url);
				}
				String textSnippet = extractSnippet(url, page);
				cleanedResults.add(new SearchResult(a.asText(), url, textSnippet));
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

	private String extractSnippet(String url, HtmlPage page) {
		try {
			if (extractSnippets) {
				try {
					URI uri = new URI(url);
					String auth = uri.getAuthority();
					if (auth == null) {
						return "";
					}
					if (auth.contains("://")) {
						auth = auth.substring(auth.indexOf("://") + 3);
					}
					if (auth.startsWith("www.")) {
						auth = auth.substring(4);
					}
					String path = uri.getPath();
					if (path == null) {
						return "";
					}
					if (path.endsWith("/")) {
						path = path.substring(0, path.length() - 1);
					}

					url = auth + path;
					String pageAsString = page.asText();

					int start = pageAsString.indexOf(url);
					if (start == -1) {
						return "";
					}
					int end = pageAsString.indexOf("…\n", start);
					if (end == -1) {
						return "";
					}
					return (pageAsString.substring(start + url.length(), end));
				} catch (URISyntaxException e) {
					return "";
				}

			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
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

	public void setExtractSnippets(boolean extractSnip) {
		this.extractSnippets = extractSnip;
	}
}
