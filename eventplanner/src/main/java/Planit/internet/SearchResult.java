package Planit.internet;

/**
 * An object which holds one search result from an engine such as Google Search.
 * @author wginsberg
 *
 */
public class SearchResult {

	private String title;
	private String link;
	private String snippet;
	
	public SearchResult(String title, String link, String snippet) {
		super();
		this.title = title;
		this.link = link;
		this.snippet = snippet;
	}

	public SearchResult(){
		this("", "", "");
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	
	@Override
	public String toString(){
		return String.format("%s\n%s\n%s\n", getTitle(), getSnippet(), getLink());
	}
	
}
