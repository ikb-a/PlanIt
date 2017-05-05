package PlanIt.monthSuggestion.trainingDataGeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.toronto.cs.se.ci.utils.searchEngine.GenericSearchEngine;
import edu.toronto.cs.se.ci.utils.searchEngine.MemoizingSearch;
import openEval.MultithreadSimpleOpenEval;

/**
 * This program creates the training data for the
 * {@link PlanIt.monthSuggestion.resources.OpenEvalMonthController}. Given
 * country/keyword pairs that do/do not belong to each month of the year, it
 * produces training data (an .arff file containing word bags and true/false as
 * to whether they relate to the month) for each month, that can be used to
 * create an OpenEval that will classify future country/keyword pairs as
 * belonging to the month or not.
 * 
 * Note: Increasing heap size to 1GB is needed. Use java -Xmx1024m -jar
 * EXECUTABLEJAR.jar
 * 
 * @author ikba
 *
 */
public class trainSimpleOpenEval {

	/**
	 * The folder in which the 12 .arff files produced will be saved.
	 */
	public static final String FOLDER = "./src/main/resources/data/monthData/OpenEval/YandexTraining/";

	public static void main(String[] args) throws Exception {
		GenericSearchEngine search = new MemoizingSearch(FOLDER + "TrainingSearchMemoization.ser", new YandexSearch());

		// Words for which the predicate AreRelatedToJanuary(Country, Word)
		// should return true
		List<String> wordsRelatedToJan = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
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
		// Words for which the predicate AreRelatedToJanuary(Country, Word)
		// should return false
		List<String> wordsNotRelatedToJan = new ArrayList<String>(Arrays.asList(new String[] { "Russia Spring",
				"Russia Summer", "Russia Autumn", "Brazil Last Day of School",
				"Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Autumn", "Brazil Winter", "Brazil Spring",
				"Brazil Rainy", "Brazil Cold", "Brazil Rainy", "Brazil Tax Season", "Saudi Arabia Summer Vacations",
				"Saudi Arabia Spring", "Saudi Arabia Summer", "Saudi Arabia Autumn", "Saudi Arabia Very Hot",
				"Saudi Arabia Rainy", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Winter", "South Africa Spring", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain", "South Africa Tax Season",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Fist Day of School", "United States Tax ", "United States Hot",
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
				"Russia Summer Vacations", "Russia Camomile (Matricaria Recutita)" }));
		// create a new SimpleOpenEval with the keyword "month" and save the
		// word bags to OpenEvalJan.arff
		MultithreadSimpleOpenEval eval1 = new MultithreadSimpleOpenEval(wordsRelatedToJan, wordsNotRelatedToJan,
				"January", FOLDER + "OpenEvalJan.arff", search, FOLDER + "memJan.ser");
		// save the contents of all the links read that were returned from the
		// above searches.
		eval1.saveMemoizedContents();
		// Turn of link memoizatoin now that results have been saved.
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToFeb = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States very cold", "United States Winter",
				"China Harsh Winter", "China Winter", "Japan Cold", "Japan Winter", "Germany Winter",
				"United Kingdom Cold", "United Kingdom Influenza Season", "United Kingdom Turnip",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Wet Season",
				"Australia Tropical Cyclones", "Australia Bushfires", "Australia Summer", "Russia Winter",
				"Brazil Summer", "Brazil Rainy", "Saudi Arabia Winter", "South Africa Summer" }));
		List<String> wordsNotRelatedToFeb = new ArrayList<String>(Arrays.asList(new String[] {
				"United States Back to school", "United States vacations", "United States Beach",
				"United States Outdoors", "United States Torandos", "United States Tornados",
				"United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Spring", "United States Summer",
				"united states autumn", "China comfortable temperature", "China rain season",
				"China First Day Of School", "China Last Day of School", "China Summer Vacations", "China Tornado",
				"China Summer Vacations", "China Plum Blossom", "China Spring", "China Summer", "China Autumn",
				"Japan First Day of School", "Japan Pleasant", "Japan Trees Blossoming", "Japan Very humind and hot",
				"Japan Hokkaido snow", "Japan Hot", "Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Summer Vacations", "Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring",
				"Japan Summer", "Japan Autumn", "Germany Knapweed (Centaurea Cyanus)", "Germany Spring",
				"Germany Summer", "Germany Autumn", "United Kingdom Summer Vacations", "United Kingdom Hot",
				"United Kingdom Cloudy", "United Kingdom Rainy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Flowers Blooming", "United Kingdom Raspberry",
				"United Kingdom Spring", "United Kingdom Summer", "United Kingdom Autumn", "Australia Very Hot",
				"Australia Nice Temperature", "Australia Dry Season", "Australia Tax Season",
				"Australia First Day of Class", "Australia Last Day of Class", "Australia Flu Season",
				"Australia Autumn", "Australia Winter", "Australia Spring", "Russia First Day of School",
				"Russia Last Day of School", "Russia Summer Vacations", "Russia Camomile (Matricaria Recutita)",
				"Russia Spring", "Russia Summer", "Russia Autumn", "Brazil First Day of School",
				"Brazil Last Day of School", "Brazil Summer Vacations", "Brazil Cattleya Orchid (Cattleya Labiata)",
				"Brazil Autumn", "Brazil Winter", "Brazil Spring", "Brazil Very Hot", "Brazil Cold",
				"Brazil Tax Season", "Saudi Arabia Summer Vacations", "Saudi Arabia Spring", "Saudi Arabia Summer",
				"Saudi Arabia Autumn", "Saudi Arabia Very Hot", "Saudi Arabia Humid", "Saudi Arabia Rainy",
				"Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn", "South Africa Winter",
				"South Africa Spring", "South Africa Rainy", "South Africa Very Hot", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain", "South Africa Tornados",
				"South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToFeb, wordsNotRelatedToFeb, "February",
				FOLDER + "OpenEvalFeb.arff", search, FOLDER + "memFeb.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToMar = new ArrayList<String>(Arrays.asList(
				new String[] { "United States Spring", "China Spring", "Japan Tax Season", "Japan Last Day of School",
						"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring",
						"Germany Knapweed (Centaurea Cyanus)", "Germany Spring", "United Kingdom Spring",
						"Australia Autumn", "Russia Camomile (Matricaria Recutita)", "Russia Spring", "Brazil Autumn",
						"Brazil Rainy", "Saudi Arabia Spring", "Saudi Arabia Sandstorm", "South Africa Autumn" }));
		List<String> wordsNotRelatedToMar = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Tornados", "United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Summer", "united states autumn",
				"United States Winter", "China comfortable temperature", "China Harsh Winter", "China rain season",
				"China First Day Of School", "China Last Day of School", "China Summer Vacations", "China Tornado",
				"China Summer Vacations", "China Plum Blossom", "China Summer", "China Autumn", "China Winter",
				"Japan First Day of School", "Japan Cold", "Japan Pleasant", "Japan Trees Blossoming",
				"Japan Very humind and hot", "Japan Hokkaido snow", "Japan Hot", "Japan Rain", "Japan Summer Vacations",
				"Japan Summer", "Japan Autumn", "Japan Winter", "Germany Summer", "Germany Autumn", "Germany Winter",
				"United Kingdom Summer Vacations", "United Kingdom Hot", "United Kingdom Cold", "United Kingdom Cloudy",
				"United Kingdom Rainy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Summer", "United Kingdom Autumn",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Nice Temperature",
				"Australia Wet Season", "Australia Dry Season", "Australia Tropical Cyclones", "Australia Bushfires",
				"Australia Tax Season", "Australia First Day of Class", "Australia Last Day of Class",
				"Australia Flu Season", "Australia Winter", "Australia Spring", "Australia Summer",
				"Russia First Day of School", "Russia Last Day of School", "Russia Summer Vacations", "Russia Summer",
				"Russia Autumn", "Russia Winter", "Brazil First Day of School", "Brazil Last Day of School",
				"Brazil Summer Vacations", "Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Winter",
				"Brazil Spring", "Brazil Summer", "Brazil Very Hot", "Brazil Cold", "Brazil Tax Season",
				"Saudi Arabia Summer Vacations", "Saudi Arabia Summer", "Saudi Arabia Autumn", "Saudi Arabia Winter",
				"Saudi Arabia Very Hot", "Saudi Arabia Humid", "Saudi Arabia Rainy", "Saudi Arabia Tax Season",
				"South Africa Winter", "South Africa Spring", "South Africa Summer", "South Africa Rainy",
				"South Africa Very Hot", "South Africa Nice temperature", "South Africa Cape Town Humid",
				"South Africa Cape Town Rain", "South Africa Tornados", "South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToMar, wordsNotRelatedToMar, "March",
				FOLDER + "OpenEvalMar.arff", search, FOLDER + "memMar.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToApr = new ArrayList<String>(Arrays.asList(new String[] { "United States Torandos",
				"United States Tax ", "United States Spring", "China Plum Blossom", "China Spring",
				"Japan First Day of School", "Japan Trees Blossoming", "Japan Spring", "Germany Spring",
				"United Kingdom Spring", "Australia Autumn", "Russia Spring", "Brazil Autumn", "Brazil Tax Season",
				"Saudi Arabia Spring", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn" }));
		List<String> wordsNotRelatedToApr = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Tornados",
				"United States Fist Day of School", "United States Hot", "United States Summer Vacations",
				"United States Summer", "united states autumn", "United States Winter", "China comfortable temperature",
				"China Harsh Winter", "China rain season", "China First Day Of School", "China Last Day of School",
				"China Summer Vacations", "China Tornado", "China Summer Vacations", "China Summer", "China Autumn",
				"China Winter", "Japan Cold", "Japan Pleasant", "Japan Very humind and hot", "Japan Hokkaido snow",
				"Japan Hot", "Japan Tax Season", "Japan Rain", "Japan Last Day of School", "Japan Summer Vacations",
				"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Summer", "Japan Autumn", "Japan Winter",
				"Germany Knapweed (Centaurea Cyanus)", "Germany Summer", "Germany Autumn", "Germany Winter",
				"United Kingdom Summer Vacations", "United Kingdom Hot", "United Kingdom Cold", "United Kingdom Cloudy",
				"United Kingdom Rainy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Summer", "United Kingdom Autumn",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Nice Temperature",
				"Australia Wet Season", "Australia Dry Season", "Australia Tropical Cyclones", "Australia Bushfires",
				"Australia Tax Season", "Australia First Day of Class", "Australia Last Day of Class",
				"Australia Flu Season", "Australia Winter", "Australia Spring", "Australia Summer",
				"Russia First Day of School", "Russia Last Day of School", "Russia Summer Vacations",
				"Russia Camomile (Matricaria Recutita)", "Russia Summer", "Russia Autumn", "Russia Winter",
				"Brazil First Day of School", "Brazil Last Day of School", "Brazil Summer Vacations",
				"Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Winter", "Brazil Spring", "Brazil Summer",
				"Brazil Rainy", "Brazil Very Hot", "Brazil Cold", "Brazil Rainy", "Saudi Arabia Summer Vacations",
				"Saudi Arabia Summer", "Saudi Arabia Autumn", "Saudi Arabia Winter", "Saudi Arabia Very Hot",
				"Saudi Arabia Humid", "Saudi Arabia Rainy", "South Africa Winter", "South Africa Spring",
				"South Africa Summer", "South Africa Rainy", "South Africa Very Hot", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain", "South Africa Tornados",
				"South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToApr, wordsNotRelatedToApr, "April",
				FOLDER + "OpenEvalApr.arff", search, FOLDER + "memApr.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToMay = new ArrayList<String>(
				Arrays.asList(new String[] { "United States Tornados", "United States Spring", "China Spring",
						"Japan Pleasant", "Japan Spring", "Germany Spring", "United Kingdom Flowers Blooming",
						"United Kingdom Spring", "Australia Autumn", "Russia Last Day of School", "Russia Spring",
						"Brazil Autumn", "Saudi Arabia Spring", "Saudi Arabia Sandstorm", "South Africa Autumn" }));
		List<String> wordsNotRelatedToMay = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Summer", "united states autumn",
				"United States Winter", "China comfortable temperature", "China Harsh Winter", "China rain season",
				"China First Day Of School", "China Last Day of School", "China Summer Vacations", "China Tornado",
				"China Summer Vacations", "China Plum Blossom", "China Summer", "China Autumn", "China Winter",
				"Japan First Day of School", "Japan Cold", "Japan Trees Blossoming", "Japan Very humind and hot",
				"Japan Hokkaido snow", "Japan Hot", "Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Summer Vacations", "Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Summer",
				"Japan Autumn", "Japan Winter", "Germany Knapweed (Centaurea Cyanus)", "Germany Summer",
				"Germany Autumn", "Germany Winter", "United Kingdom Summer Vacations", "United Kingdom Hot",
				"United Kingdom Cold", "United Kingdom Cloudy", "United Kingdom Rainy", "United Kingdom Snow",
				"United Kingdom Humidity Worse", "United Kingdom Tax Season", "United Kingdom Influenza Season",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Summer", "United Kingdom Autumn",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Nice Temperature",
				"Australia Wet Season", "Australia Dry Season", "Australia Tropical Cyclones", "Australia Bushfires",
				"Australia Tax Season", "Australia First Day of Class", "Australia Last Day of Class",
				"Australia Flu Season", "Australia Winter", "Australia Spring", "Australia Summer",
				"Russia First Day of School", "Russia Summer Vacations", "Russia Camomile (Matricaria Recutita)",
				"Russia Summer", "Russia Autumn", "Russia Winter", "Brazil First Day of School",
				"Brazil Last Day of School", "Brazil Summer Vacations", "Brazil Cattleya Orchid (Cattleya Labiata)",
				"Brazil Winter", "Brazil Spring", "Brazil Summer", "Brazil Rainy", "Brazil Very Hot", "Brazil Cold",
				"Brazil Rainy", "Brazil Tax Season", "Saudi Arabia Summer Vacations", "Saudi Arabia Summer",
				"Saudi Arabia Autumn", "Saudi Arabia Winter", "Saudi Arabia Very Hot", "Saudi Arabia Humid",
				"Saudi Arabia Rainy", "Saudi Arabia Tax Season", "South Africa Winter", "South Africa Spring",
				"South Africa Summer", "South Africa Rainy", "South Africa Very Hot", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain", "South Africa Tornados",
				"South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToMay, wordsNotRelatedToMay, "May",
				FOLDER + "OpenEvalMay.arff", search, FOLDER + "memMay.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToJun = new ArrayList<String>(Arrays.asList(new String[] { "United States vacations",
				"United States Beach", "United States Outdoors", "United States Hot", "United States Summer",
				"China Summer", "Japan Rain", "Japan Summer", "Germany Summer", "United Kingdom Hot",
				"United Kingdom Summer", "Australia Nice Temperature", "Australia Dry Season", "Australia Flu Season",
				"Australia Winter", "Russia Summer Vacations", "Russia Summer", "Brazil Winter", "Brazil Cold",
				"Saudi Arabia Summer Vacations", "Saudi Arabia Summer", "South Africa Winter",
				"South Africa Nice temperature", "South Africa Cape Town Humid", "South Africa Cape Town Rain" }));
		List<String> wordsNotRelatedToJun = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States Torandos", "United States Tornados", "United States Fist Day of School",
				"United States Tax ", "United States Summer Vacations", "United States Spring", "united states autumn",
				"United States Winter", "China comfortable temperature", "China Harsh Winter", "China rain season",
				"China First Day Of School", "China Last Day of School", "China Summer Vacations", "China Tornado",
				"China Summer Vacations", "China Plum Blossom", "China Spring", "China Autumn", "China Winter",
				"Japan First Day of School", "Japan Cold", "Japan Pleasant", "Japan Trees Blossoming",
				"Japan Very humind and hot", "Japan Hokkaido snow", "Japan Hot", "Japan Tax Season",
				"Japan Last Day of School", "Japan Summer Vacations",
				"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring", "Japan Autumn", "Japan Winter",
				"Germany Knapweed (Centaurea Cyanus)", "Germany Spring", "Germany Autumn", "Germany Winter",
				"United Kingdom Summer Vacations", "United Kingdom Cold", "United Kingdom Cloudy",
				"United Kingdom Rainy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Spring", "United Kingdom Autumn",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Wet Season",
				"Australia Tropical Cyclones", "Australia Bushfires", "Australia Tax Season",
				"Australia First Day of Class", "Australia Last Day of Class", "Australia Autumn", "Australia Spring",
				"Australia Summer", "Russia First Day of School", "Russia Last Day of School",
				"Russia Camomile (Matricaria Recutita)", "Russia Spring", "Russia Autumn", "Russia Winter",
				"Brazil First Day of School", "Brazil Last Day of School", "Brazil Summer Vacations",
				"Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Autumn", "Brazil Spring", "Brazil Summer",
				"Brazil Rainy", "Brazil Very Hot", "Brazil Rainy", "Brazil Tax Season", "Saudi Arabia Spring",
				"Saudi Arabia Autumn", "Saudi Arabia Winter", "Saudi Arabia Very Hot", "Saudi Arabia Humid",
				"Saudi Arabia Rainy", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Spring", "South Africa Summer", "South Africa Rainy", "South Africa Very Hot",
				"South Africa Tornados", "South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToJun, wordsNotRelatedToJun, "June",
				FOLDER + "OpenEvalJun.arff", search, FOLDER + "memJun.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToJul = new ArrayList<String>(Arrays.asList(new String[] { "United States vacations",
				"United States Beach", "United States Outdoors", "United States Hot", "United States Summer Vacations",
				"United States Summer", "China rain season", "China Last Day of School", "China Summer Vacations",
				"China Summer", "Japan Very humind and hot", "Japan Summer Vacations", "Japan Summer", "Germany Summer",
				"United Kingdom Summer Vacations", "United Kingdom Hot", "United Kingdom Cloudy",
				"United Kingdom Summer", "Australia Nice Temperature", "Australia Dry Season", "Australia Flu Season",
				"Australia Winter", "Russia Summer Vacations", "Russia Summer", "Brazil Winter", "Brazil Cold",
				"Saudi Arabia Summer", "Saudi Arabia Very Hot", "South Africa Winter", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain" }));
		List<String> wordsNotRelatedToJul = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States Torandos", "United States Tornados", "United States Fist Day of School",
				"United States Tax ", "United States Spring", "united states autumn", "United States Winter",
				"China comfortable temperature", "China Harsh Winter", "China First Day Of School", "China Tornado",
				"China Plum Blossom", "China Spring", "China Autumn", "China Winter", "Japan First Day of School",
				"Japan Cold", "Japan Pleasant", "Japan Trees Blossoming", "Japan Hokkaido snow", "Japan Hot",
				"Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring", "Japan Autumn", "Japan Winter",
				"Germany Knapweed (Centaurea Cyanus)", "Germany Spring", "Germany Autumn", "Germany Winter",
				"United Kingdom Cold", "United Kingdom Rainy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Spring", "United Kingdom Autumn",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Wet Season",
				"Australia Tropical Cyclones", "Australia Bushfires", "Australia Tax Season",
				"Australia First Day of Class", "Australia Last Day of Class", "Australia Autumn", "Australia Spring",
				"Australia Summer", "Russia First Day of School", "Russia Last Day of School",
				"Russia Camomile (Matricaria Recutita)", "Russia Spring", "Russia Autumn", "Russia Winter",
				"Brazil First Day of School", "Brazil Last Day of School", "Brazil Summer Vacations",
				"Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Autumn", "Brazil Spring", "Brazil Summer",
				"Brazil Rainy", "Brazil Very Hot", "Brazil Rainy", "Brazil Tax Season", "Saudi Arabia Summer Vacations",
				"Saudi Arabia Spring", "Saudi Arabia Autumn", "Saudi Arabia Winter", "Saudi Arabia Humid",
				"Saudi Arabia Rainy", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Spring", "South Africa Summer", "South Africa Rainy", "South Africa Very Hot",
				"South Africa Tornados", "South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToJul, wordsNotRelatedToJul, "July",
				FOLDER + "OpenEvalJul.arff", search, FOLDER + "memJul.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToAug = new ArrayList<String>(Arrays.asList(new String[] {
				"United States Summer Vacations", "United States Summer", "China rain season", "China Summer Vacations",
				"China Tornado", "China Summer Vacations", "China Summer", "Japan Hot", "Japan Summer Vacations",
				"Japan Summer", "Germany Summer", "United Kingdom Summer Vacations", "United Kingdom Humidity Worse",
				"United Kingdom Raspberry", "United Kingdom Summer", "Australia Flu Season", "Australia Winter",
				"Russia Summer Vacations", "Russia Summer", "Brazil Winter", "Saudi Arabia Summer Vacations",
				"Saudi Arabia Summer", "Saudi Arabia Very Hot", "South Africa Winter" }));
		List<String> wordsNotRelatedToAug = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Tornados", "United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Spring", "united states autumn", "United States Winter", "China comfortable temperature",
				"China Harsh Winter", "China First Day Of School", "China Last Day of School", "China Plum Blossom",
				"China Spring", "China Autumn", "China Winter", "Japan First Day of School", "Japan Cold",
				"Japan Pleasant", "Japan Trees Blossoming", "Japan Very humind and hot", "Japan Hokkaido snow",
				"Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring", "Japan Autumn", "Japan Winter",
				"Germany Knapweed (Centaurea Cyanus)", "Germany Spring", "Germany Autumn", "Germany Winter",
				"United Kingdom Hot", "United Kingdom Cold", "United Kingdom Cloudy", "United Kingdom Rainy",
				"United Kingdom Snow", "United Kingdom Tax Season", "United Kingdom Influenza Season",
				"United Kingdom Flowers Blooming", "United Kingdom Turnip", "United Kingdom Spring",
				"United Kingdom Autumn", "United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot",
				"Australia Very Hot ", "Australia Ultraviolet Light", "Australia Skin Cancer",
				"Australia Nice Temperature", "Australia Wet Season", "Australia Dry Season",
				"Australia Tropical Cyclones", "Australia Bushfires", "Australia Tax Season",
				"Australia First Day of Class", "Australia Last Day of Class", "Australia Autumn", "Australia Spring",
				"Australia Summer", "Russia First Day of School", "Russia Last Day of School",
				"Russia Camomile (Matricaria Recutita)", "Russia Spring", "Russia Autumn", "Russia Winter",
				"Brazil First Day of School", "Brazil Last Day of School", "Brazil Summer Vacations",
				"Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Autumn", "Brazil Spring", "Brazil Summer",
				"Brazil Rainy", "Brazil Very Hot", "Brazil Cold", "Brazil Rainy", "Brazil Tax Season",
				"Saudi Arabia Spring", "Saudi Arabia Autumn", "Saudi Arabia Winter", "Saudi Arabia Humid",
				"Saudi Arabia Rainy", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Spring", "South Africa Summer", "South Africa Rainy", "South Africa Very Hot",
				"South Africa Nice temperature", "South Africa Cape Town Humid", "South Africa Cape Town Rain",
				"South Africa Tornados", "South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToAug, wordsNotRelatedToAug, "August",
				FOLDER + "OpenEvalAug.arff", search, FOLDER + "memAug.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToSep = new ArrayList<String>(Arrays.asList(new String[] {
				"United States Fist Day of School", "united states autumn", "China comfortable temperature",
				"China First Day Of School", "China Tornado", "China Autumn", "Japan Autumn", "Germany Autumn",
				"United Kingdom Autumn", "Australia Flu Season", "Australia Spring", "Russia First Day of School",
				"Russia Autumn", "Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Spring", "Saudi Arabia Autumn",
				"South Africa Spring" }));
		List<String> wordsNotRelatedToSep = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Tornados", "United States Tax ", "United States Hot", "United States Summer Vacations",
				"United States Spring", "United States Summer", "United States Winter", "China Harsh Winter",
				"China rain season", "China Last Day of School", "China Summer Vacations", "China Summer Vacations",
				"China Plum Blossom", "China Spring", "China Summer", "China Winter", "Japan First Day of School",
				"Japan Cold", "Japan Pleasant", "Japan Trees Blossoming", "Japan Very humind and hot",
				"Japan Hokkaido snow", "Japan Hot", "Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Summer Vacations", "Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring",
				"Japan Summer", "Japan Winter", "Germany Knapweed (Centaurea Cyanus)", "Germany Spring",
				"Germany Summer", "Germany Winter", "United Kingdom Summer Vacations", "United Kingdom Hot",
				"United Kingdom Cold", "United Kingdom Cloudy", "United Kingdom Rainy", "United Kingdom Snow",
				"United Kingdom Humidity Worse", "United Kingdom Tax Season", "United Kingdom Influenza Season",
				"United Kingdom Flowers Blooming", "United Kingdom Raspberry", "United Kingdom Turnip",
				"United Kingdom Spring", "United Kingdom Summer", "United Kingdom Winter", "Australia Summer Vacations",
				"Australia Very Hot", "Australia Very Hot ", "Australia Ultraviolet Light", "Australia Skin Cancer",
				"Australia Nice Temperature", "Australia Wet Season", "Australia Dry Season",
				"Australia Tropical Cyclones", "Australia Bushfires", "Australia Tax Season",
				"Australia First Day of Class", "Australia Last Day of Class", "Australia Autumn", "Australia Winter",
				"Australia Summer", "Russia Last Day of School", "Russia Summer Vacations",
				"Russia Camomile (Matricaria Recutita)", "Russia Spring", "Russia Summer", "Russia Winter",
				"Brazil First Day of School", "Brazil Last Day of School", "Brazil Summer Vacations", "Brazil Autumn",
				"Brazil Winter", "Brazil Summer", "Brazil Rainy", "Brazil Very Hot", "Brazil Cold", "Brazil Rainy",
				"Brazil Tax Season", "Saudi Arabia Summer Vacations", "Saudi Arabia Spring", "Saudi Arabia Summer",
				"Saudi Arabia Winter", "Saudi Arabia Very Hot", "Saudi Arabia Humid", "Saudi Arabia Rainy",
				"Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn", "South Africa Winter",
				"South Africa Summer", "South Africa Rainy", "South Africa Very Hot", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain", "South Africa Tornados",
				"South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToSep, wordsNotRelatedToSep, "September",
				FOLDER + "OpenEvalSep.arff", search, FOLDER + "memSep.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToOct = new ArrayList<String>(Arrays.asList(new String[] { "united states autumn",
				"China comfortable temperature", "China Autumn", "Japan Autumn", "Germany Autumn",
				"United Kingdom Tax Season", "United Kingdom Autumn", "Australia Dry Season", "Australia Tax Season",
				"Australia Spring", "Russia Autumn", "Brazil Spring", "Saudi Arabia Autumn", "South Africa Spring" }));
		List<String> wordsNotRelatedToOct = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Tornados", "United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Spring", "United States Summer",
				"United States Winter", "China Harsh Winter", "China rain season", "China First Day Of School",
				"China Last Day of School", "China Summer Vacations", "China Tornado", "China Summer Vacations",
				"China Plum Blossom", "China Spring", "China Summer", "China Winter", "Japan First Day of School",
				"Japan Cold", "Japan Pleasant", "Japan Trees Blossoming", "Japan Very humind and hot",
				"Japan Hokkaido snow", "Japan Hot", "Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Summer Vacations", "Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring",
				"Japan Summer", "Japan Winter", "Germany Knapweed (Centaurea Cyanus)", "Germany Spring",
				"Germany Summer", "Germany Winter", "United Kingdom Summer Vacations", "United Kingdom Hot",
				"United Kingdom Cold", "United Kingdom Cloudy", "United Kingdom Rainy", "United Kingdom Snow",
				"United Kingdom Humidity Worse", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Spring", "United Kingdom Summer",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Nice Temperature",
				"Australia Wet Season", "Australia Tropical Cyclones", "Australia Bushfires",
				"Australia First Day of Class", "Australia Last Day of Class", "Australia Flu Season",
				"Australia Autumn", "Australia Winter", "Australia Summer", "Russia First Day of School",
				"Russia Last Day of School", "Russia Summer Vacations", "Russia Camomile (Matricaria Recutita)",
				"Russia Spring", "Russia Summer", "Russia Winter", "Brazil First Day of School",
				"Brazil Last Day of School", "Brazil Summer Vacations", "Brazil Cattleya Orchid (Cattleya Labiata)",
				"Brazil Autumn", "Brazil Winter", "Brazil Summer", "Brazil Rainy", "Brazil Very Hot", "Brazil Cold",
				"Brazil Rainy", "Brazil Tax Season", "Saudi Arabia Summer Vacations", "Saudi Arabia Spring",
				"Saudi Arabia Summer", "Saudi Arabia Winter", "Saudi Arabia Very Hot", "Saudi Arabia Humid",
				"Saudi Arabia Rainy", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Winter", "South Africa Summer", "South Africa Rainy", "South Africa Very Hot",
				"South Africa Nice temperature", "South Africa Cape Town Humid", "South Africa Cape Town Rain",
				"South Africa Tornados", "South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToOct, wordsNotRelatedToOct, "October",
				FOLDER + "OpenEvalOct.arff", search, FOLDER + "memOct.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToNov = new ArrayList<String>(Arrays.asList(
				new String[] { "united states autumn", "China comfortable temperature", "China Autumn", "Japan Autumn",
						"Germany Autumn", "United Kingdom Rainy", "United Kingdom Autumn", "Australia Spring",
						"Russia Autumn", "Brazil Spring", "Brazil Rainy", "Saudi Arabia Autumn", "Saudi Arabia Rainy",
						"South Africa Spring", "South Africa Tornados", "South Africa Tax Season" }));
		List<String> wordsNotRelatedToNov = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Tornados", "United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Spring", "United States Summer",
				"United States Winter", "China Harsh Winter", "China rain season", "China First Day Of School",
				"China Last Day of School", "China Summer Vacations", "China Tornado", "China Summer Vacations",
				"China Plum Blossom", "China Spring", "China Summer", "China Winter", "Japan First Day of School",
				"Japan Cold", "Japan Pleasant", "Japan Trees Blossoming", "Japan Very humind and hot",
				"Japan Hokkaido snow", "Japan Hot", "Japan Tax Season", "Japan Rain", "Japan Last Day of School",
				"Japan Summer Vacations", "Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring",
				"Japan Summer", "Japan Winter", "Germany Knapweed (Centaurea Cyanus)", "Germany Spring",
				"Germany Summer", "Germany Winter", "United Kingdom Summer Vacations", "United Kingdom Hot",
				"United Kingdom Cold", "United Kingdom Cloudy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Turnip", "United Kingdom Spring", "United Kingdom Summer",
				"United Kingdom Winter", "Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Nice Temperature",
				"Australia Wet Season", "Australia Dry Season", "Australia Tropical Cyclones", "Australia Bushfires",
				"Australia Tax Season", "Australia First Day of Class", "Australia Last Day of Class",
				"Australia Flu Season", "Australia Autumn", "Australia Winter", "Australia Summer",
				"Russia First Day of School", "Russia Last Day of School", "Russia Summer Vacations",
				"Russia Camomile (Matricaria Recutita)", "Russia Spring", "Russia Summer", "Russia Winter",
				"Brazil First Day of School", "Brazil Last Day of School", "Brazil Summer Vacations",
				"Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Autumn", "Brazil Winter", "Brazil Summer",
				"Brazil Very Hot", "Brazil Cold", "Brazil Tax Season", "Saudi Arabia Summer Vacations",
				"Saudi Arabia Spring", "Saudi Arabia Summer", "Saudi Arabia Winter", "Saudi Arabia Very Hot",
				"Saudi Arabia Humid", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Winter", "South Africa Summer", "South Africa Rainy", "South Africa Very Hot",
				"South Africa Nice temperature", "South Africa Cape Town Humid", "South Africa Cape Town Rain" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToNov, wordsNotRelatedToNov, "November",
				FOLDER + "OpenEvalNov.arff", search, FOLDER + "memNov.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

		List<String> wordsRelatedToDec = new ArrayList<String>(Arrays.asList(new String[] { "United States Winter",
				"China Winter", "Japan Winter", "Germany Winter", "United Kingdom Turnip", "United Kingdom Winter",
				"Australia Wet Season", "Australia Tropical Cyclones", "Australia Last Day of Class",
				"Australia Summer", "Russia Winter", "Brazil Last Day of School", "Brazil Summer Vacations",
				"Brazil Summer", "Brazil Very Hot", "Brazil Rainy", "Saudi Arabia Winter", "Saudi Arabia Humid",
				"South Africa Summer", "South Africa Rainy", "South Africa Very Hot", "South Africa Tornados" }));
		List<String> wordsNotRelatedToDec = new ArrayList<String>(Arrays.asList(new String[] { "United States Snow",
				"United States Cold", "United States Flu", "United States Back to school", "United States very cold",
				"United States vacations", "United States Beach", "United States Outdoors", "United States Torandos",
				"United States Tornados", "United States Fist Day of School", "United States Tax ", "United States Hot",
				"United States Summer Vacations", "United States Spring", "United States Summer",
				"united states autumn", "China comfortable temperature", "China Harsh Winter", "China rain season",
				"China First Day Of School", "China Last Day of School", "China Summer Vacations", "China Tornado",
				"China Summer Vacations", "China Plum Blossom", "China Spring", "China Summer", "China Autumn",
				"Japan First Day of School", "Japan Cold", "Japan Pleasant", "Japan Trees Blossoming",
				"Japan Very humind and hot", "Japan Hokkaido snow", "Japan Hot", "Japan Tax Season", "Japan Rain",
				"Japan Last Day of School", "Japan Summer Vacations",
				"Japan Chrysanthemum (Imperial), Cherry Blossom Sakura", "Japan Spring", "Japan Summer", "Japan Autumn",
				"Germany Knapweed (Centaurea Cyanus)", "Germany Spring", "Germany Summer", "Germany Autumn",
				"United Kingdom Summer Vacations", "United Kingdom Hot", "United Kingdom Cold", "United Kingdom Cloudy",
				"United Kingdom Rainy", "United Kingdom Snow", "United Kingdom Humidity Worse",
				"United Kingdom Tax Season", "United Kingdom Influenza Season", "United Kingdom Flowers Blooming",
				"United Kingdom Raspberry", "United Kingdom Spring", "United Kingdom Summer", "United Kingdom Autumn",
				"Australia Summer Vacations", "Australia Very Hot", "Australia Very Hot ",
				"Australia Ultraviolet Light", "Australia Skin Cancer", "Australia Nice Temperature",
				"Australia Dry Season", "Australia Bushfires", "Australia Tax Season", "Australia First Day of Class",
				"Australia Flu Season", "Australia Autumn", "Australia Winter", "Australia Spring",
				"Russia First Day of School", "Russia Last Day of School", "Russia Summer Vacations",
				"Russia Camomile (Matricaria Recutita)", "Russia Spring", "Russia Summer", "Russia Autumn",
				"Brazil First Day of School", "Brazil Cattleya Orchid (Cattleya Labiata)", "Brazil Autumn",
				"Brazil Winter", "Brazil Spring", "Brazil Cold", "Brazil Tax Season", "Saudi Arabia Summer Vacations",
				"Saudi Arabia Spring", "Saudi Arabia Summer", "Saudi Arabia Autumn", "Saudi Arabia Very Hot",
				"Saudi Arabia Rainy", "Saudi Arabia Sandstorm", "Saudi Arabia Tax Season", "South Africa Autumn",
				"South Africa Winter", "South Africa Spring", "South Africa Nice temperature",
				"South Africa Cape Town Humid", "South Africa Cape Town Rain", "South Africa Tax Season" }));
		eval1 = new MultithreadSimpleOpenEval(wordsRelatedToDec, wordsNotRelatedToDec, "December",
				FOLDER + "OpenEvalDec.arff", search, FOLDER + "memDec.ser");
		eval1.saveMemoizedContents();
		eval1.setMemoizeLinkContentsOff();

	}

}
