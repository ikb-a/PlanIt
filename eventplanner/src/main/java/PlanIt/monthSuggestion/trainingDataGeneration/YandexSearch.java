package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.toronto.cs.se.ci.utils.searchEngine.SearchResult;
import edu.toronto.cs.se.ci.utils.searchEngine.SearchResults;
import edu.toronto.cs.se.ci.utils.searchEngine.XMLSearchEngine;

/**
 * To use, you must have correct username, key, and be accessing from a
 * registered IP
 * 
 * @author ikba
 *
 */
public class YandexSearch implements XMLSearchEngine {

	private String rawResults;
	public static final int pageSize = 10;
	public static final String CHARSET = "UTF-8";
	private static Scanner sc = null;
	// Page 1 is numbered as page=0
	private static final String SEARCH_URL_P1 = "https://yandex.com/search/xml?user=geor-bob&key=03.451371415:7c30b595a4b4d56780150e0db50cdc9c&query=";
	private static final String SEARCH_URL_P2 = "&l10n=en&sortby=rlv&filter=none&maxpassages=1&groupby=attr%3D%22%22.mode%3Dflat.groups-on-page%3D10.docs-in-group%3D1&page=";
	
	public YandexSearch() {
		if (sc == null) {
			sc = new Scanner(System.in);
		}
	}

	/**
	 * Quick test that search is working.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		YandexSearch mary = new YandexSearch();
		System.out.println(mary.search("Can you dig it"));
		System.out.println("----------");
		System.out.println(mary.search(""));
	}

	@Override
	public SearchResults search(String searchString) throws IOException {
		return search(searchString, 1);
	}

	@Override
	public SearchResults search(String searchString, int pageNumber) throws IOException {
		if (searchString.equals("")) {
			List<SearchResult> results = new ArrayList<SearchResult>();
			return new SearchResults(0, results, searchString, pageNumber);
		}

		String url = formatSearch(searchString, pageNumber);
		Document full = callAPIAndParse(url);

		// Get all results in the XML
		NodeList resultsInDOM = full.getElementsByTagName("doc");
		if (resultsInDOM.getLength() == 0) {
			NodeList error = full.getElementsByTagName("error");
			if (error.getLength() == 0) {
				List<SearchResult> results = new ArrayList<SearchResult>();
				return new SearchResults(0, results, searchString, pageNumber);
			} else {
				System.err.println(
						"Yandex API has returned an error. Double tap ENTER to continue regardless, returning 0 search results");
				System.err.println("Raw XML:");
				System.err.println(rawResults);
				System.err.println("DOM Interpretation of XML:");
				DFSDisplay(full.getDocumentElement(), 0);
				sc.nextLine();
				sc.nextLine();
				List<SearchResult> results = new ArrayList<SearchResult>();
				return new SearchResults(0, results, searchString, pageNumber);
			}

		} else {
			try {
				List<SearchResult> results = new ArrayList<SearchResult>();
				for (int i = 0; i < resultsInDOM.getLength(); i++) {
					if (resultsInDOM.item(i).getNodeType() == (short) 1) {
						Element elem = (Element) resultsInDOM.item(i);
						String URL = elem.getElementsByTagName("url").item(0).getTextContent();

						// title may not be present in XML returned by API
						String title = "";
						try {
							title = elem.getElementsByTagName("title").item(0).getTextContent();
						} catch (NullPointerException e) {
						}

						// snippet may or may not be present in the result
						String snippet = "";
						// attempt to find snippet
						try {
							snippet = elem.getElementsByTagName("passage").item(0).getTextContent();
						} catch (NullPointerException e) {
						}

						SearchResult result = new SearchResult(title, URL, snippet);
						results.add(result);
					}
				}

				String hitsString = full.getElementsByTagName("found").item(0).getTextContent();
				int hits = Integer.parseInt(hitsString);
				return new SearchResults(hits, results, searchString, pageNumber);
			} catch (Exception e) {
				System.err.println("Error occured in parsing of DOM rep XML");
				e.printStackTrace();
				System.err.println("Raw XML:");
				System.err.println(rawResults);
				System.err.println("DOM Interpretation of XML:");
				DFSDisplay(full.getDocumentElement(), 0);
				throw new RuntimeException(e);
			}

		}
	}

	@Override
	public SearchResults nextPage(SearchResults previousPage) throws IOException {
		String query = previousPage.getQuery();
		int page = previousPage.getPageNumber() + 1;
		return search(query, page);
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public String getRawResults() {
		return rawResults;
	}

	/**
	 * Creates the String for the URL for the Yandex API. Note that in addition
	 * to the key and user being correct, the API call must originate from an IP
	 * registered to the user.
	 * 
	 * @param searchString
	 *            The query to the search engine.
	 * @param pageNumber
	 *            The page number of results desired (min value is 1)
	 * @return String url that calls the Yandex API
	 * 
	 */
	private String formatSearch(String searchString, int pageNumber) {
		pageNumber--;
		try {
			String encodedSearch = URLEncoder.encode(searchString, CHARSET);
			return SEARCH_URL_P1 + encodedSearch + SEARCH_URL_P2 + pageNumber;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Somehow, the encoding " + CHARSET + " is invalid.");
		}
	}

	/**
	 * Calls the API, saves the raw results, and parses them into a Document.
	 * 
	 * @param url
	 *            The url from which to call the API
	 * @return Document reprsenting the returned XML file
	 * @throws IOException
	 */
	private Document callAPIAndParse(String url) throws IOException {
		try {
			URL uri = new URL(url);

			InputStream stream = uri.openStream();
			rawResults = IOUtils.toString(stream);
			// System.out.println(rawResults);
			stream.close();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document result = db.parse(new InputSource(new StringReader(rawResults)));

			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException("Malformed Search url: " + url);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Prints the DOM as a tree to System.err. Each node is represented by a
	 * triple of NodeType, NodeName, and NodeValue
	 * 
	 * @param root
	 *            root of tree to display
	 * @param indent
	 *            indent for root of this tree
	 */
	private void DFSDisplay(Node root, int indent) {
		for (int i = 0; i < indent; i++) {
			System.err.print("  ");
		}
		System.err.println(root.getNodeType() + " " + root.getNodeName() + " " + root.getNodeValue());
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			DFSDisplay(nl.item(i), indent + 1);
		}
	}

}
