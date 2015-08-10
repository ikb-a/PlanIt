package Planit.speakersuggestion.scrapespeakers.sources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Optional;

import Planit.dataObjects.Speaker;
import Planit.speakersuggestion.scrapespeakers.util.GetSpeakersContract;
import Planit.speakersuggestion.scrapespeakers.util.SpeakerSetTrust;
import Planit.speakersuggestion.scrapespeakers.util.SpeakersQuery;
import edu.toronto.cs.se.ci.Source;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.data.Opinion;

public class PremiereSpeakersBureau extends Source<SpeakersQuery, Collection<Speaker>, SpeakerSetTrust> implements
		GetSpeakersContract {

	String w= "";
	

	@Override
	public Opinion<Collection<Speaker>, SpeakerSetTrust> getOpinion(
			SpeakersQuery query) throws UnknownException {
		
		List<Speaker> speakers = new ArrayList<Speaker>();
		
		//search for speakers by the keywords
		for (String keyword : query.getKeywords()){
			List<Speaker> results = getSpeakers(keyword);
			//clear extras
			if (results.size() > query.maxPerKeyword()){
				results.subList(query.maxPerKeyword(), results.size()).clear();
			}
			speakers.addAll(results);
		}
		
		//clear extras
		if (speakers.size() > query.getMaxSpeakers()){
			speakers.subList(query.getMaxSpeakers(), speakers.size()).clear();
		}
		
		//extract details
		for (Speaker speaker : speakers){
			extractDetails(speaker);
		}
		
		return new Opinion<Collection<Speaker>, SpeakerSetTrust>(query, speakers, null, this);
	}

	/**
	 * Does a search for speakers and returns the results
	 * @param search A keyword to search for
	 * @return
	 */
	static List<Speaker> getSpeakers(String search){
		
		List<Speaker> speakers =  new ArrayList<Speaker>();
		
		//try to get the results page
		String url = "http://premierespeakers.com/search?speaker_search[term]=" + search;
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			return speakers;
		}
		
		//read each speaker from the page
		Elements speakerElements = document.select(".small-12 .row .row");
		for (Element element : speakerElements){
			String name = element.select("h2").text();
			String title = element.select("h6").text();
			String bio = element.select(".small-12.columns.proxima:nth-child(3n+1)").text();
			String href = element.select(".small-12.columns.proxima a").text();
			
			if (name == null || name.length() < 1){
				continue;
			}
			
			Speaker speaker = Speaker.createSpeaker(name).setProfessionalTitle(title).setBio(bio);
			try{
				speaker.addPage(new URL("http://premierespeakers.com/" + href));
			}
			catch (MalformedURLException e){
				continue;
			}
			speakers.add(speaker);
		}
		
		return speakers;
	}
	
	/**
	 * Extracts additional details from the speaker's personal page
	 * @param speaker
	 */
	static void extractDetails(Speaker speaker){
		
		Document document;
		try{
			document = Jsoup.connect(speaker.getPages().get(0).toString()).get();
		}
		catch (IOException e){
			return;
		}
		
		
		//speech topics
		Elements speechTopics = document.select(".speech-title");
		for (Element speechTopic : speechTopics){
			speaker.getTopics().add(speechTopic.text());
		}
		
	}
	
	@Override
	public Expenditure[] getCost(SpeakersQuery args) throws Exception {
		return new Expenditure [0];
	}
	
	@Override
	public SpeakerSetTrust getTrust(SpeakersQuery args,
			Optional<Collection<Speaker>> value) {
		return null;
	}
}
