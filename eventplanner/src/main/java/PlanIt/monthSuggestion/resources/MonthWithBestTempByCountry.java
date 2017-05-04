package PlanIt.monthSuggestion.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import PlanIt.monthSuggestion.sources.Month;
import edu.toronto.cs.se.ci.UnknownException;

/**
 * This class of static methods accepts the name of a country and a value, n,
 * from 1-12, and returns the month with the nth best temperature for the
 * country.
 * 
 * @author ikba
 *
 */
public class MonthWithBestTempByCountry {
	/**
	 * The country whose temperature was last checked
	 */
	private static String currCountry;

	/**
	 * The code corresponding to {@link currCountry} (this code used in the
	 * worldbank climate API call)
	 */
	private static String currCountryCode;

	/**
	 * Map from country name to country code
	 */
	private static Map<String, String> map;

	/**
	 * An ordered list of {@link MonthAndTempDelta}'s from least absolute
	 * deviation to most absolute deviation for {@link currCountry}.
	 */
	private static List<MonthAndTempDelta> data;

	/**
	 * The "most comfortable" temperature, chosen to be 20 Celsius.
	 */
	public static final int IDEAL_TEMP = 20;

	/**
	 * Quick test program.
	 * 
	 * @param args
	 * @throws UnknownException
	 */
	public static void main(String[] args) throws UnknownException {
		for (int x = 1; x < 13; x++) {
			System.out.println(nthMonthWithBestTemp("Canada", x));
		}
	}

	/**
	 * Returns the month with the nth smallest deviation between
	 * {@link IDEAL_TEMP} and the countries average temperature in that month.
	 * 
	 * @param country
	 * @param n
	 * @throws UnknownException
	 */
	public static Month nthMonthWithBestTemp(String country, int n) throws UnknownException {
		if (n < 1 || n > 12) {
			throw new IllegalArgumentException("The " + n + "th month does not exist");
		}

		// Prevents updateCountryAndCode being called unsafely
		synchronized (MonthWithBestTempByCountry.class) {
			if (!country.equals(currCountry)) {
				data = new ArrayList<MonthAndTempDelta>();
				updateCountryAndCode(country);

				String apiResult = requestData();
				if (apiResult.trim().isEmpty()) {
					throw new UnknownException("No data returned by API. Call was invalid.");
				}

				try {
					// System.out.println(apiResult);
					JSONArray jsonMonthlyData = new JSONArray(apiResult);
					assert (jsonMonthlyData.length() == 12);
					for (int x = 0; x < 12; x++) {
						JSONObject monthData = jsonMonthlyData.getJSONObject(x);
						int monthAsInt = monthData.getInt("month");
						double averageTemp = monthData.getDouble("data");

						Month thisMonth = Month.January;
						switch (monthAsInt) {
						case 0:
							thisMonth = Month.January;
							break;
						case 1:
							thisMonth = Month.February;
							break;
						case 2:
							thisMonth = Month.March;
							break;
						case 3:
							thisMonth = Month.April;
							break;
						case 4:
							thisMonth = Month.May;
							break;
						case 5:
							thisMonth = Month.June;
							break;
						case 6:
							thisMonth = Month.July;
							break;
						case 7:
							thisMonth = Month.August;
							break;
						case 8:
							thisMonth = Month.September;
							break;
						case 9:
							thisMonth = Month.October;
							break;
						case 10:
							thisMonth = Month.November;
							break;
						case 11:
							thisMonth = Month.December;
							break;
						}
						MonthAndTempDelta thisMonthData = new MonthAndTempDelta(thisMonth,
								Math.abs(averageTemp - IDEAL_TEMP));
						data.add(thisMonthData);
					}
					Collections.sort(data,
							(a, b) -> (a.getMonth() == b.getMonth() || a.getTempDelta() == b.getTempDelta()) ? 0
									: ((a.getTempDelta() > b.getTempDelta()) ? 1 : -1));
				} catch (Exception e) {
					throw new UnknownException(e);
				}
			}
		}

		return data.get(n - 1).getMonth();
	}

	/**
	 * Makes a request to the World Bank climate data api for the
	 * {@link currCountry}, and returns the response
	 * 
	 * @throws UnknownException
	 */
	private static String requestData() throws UnknownException {
		String urlString = "http://climatedataapi.worldbank.org/climateweb/rest/v1/country/cru/tas/month/"
				+ currCountryCode;
		try {
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = br.readLine();
			String result = "";
			while (line != null) {
				result += line;
				line = br.readLine();
			}
			return result;
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}

	/**
	 * Not thread safe. Updates {@link currCountry} to country, and update
	 * {@link currCountryCode} to the corresponding code.
	 * 
	 * @param country
	 * @return
	 */
	private static void updateCountryAndCode(String country) {
		if (map == null) {
			map = new HashMap<String, String>();
			map.put("Andorra, Principality Of", "AND");
			map.put("Andorra", "AND");
			map.put("United Arab Emirates", "ARE");
			map.put("Afghanistan", "AFG");
			map.put("Antigua And Barbuda", "ATG");
			map.put("Anguilla", "AIA");
			map.put("Albania", "ALB");
			map.put("Armenia", "ARM");
			map.put("Angola", "AGO");
			map.put("Argentina", "ARG");
			map.put("American Samoa", "ASM");
			map.put("Austria", "AUT");
			map.put("Australia", "AUS");
			map.put("Aruba", "ABW");
			map.put("Azerbaidjan", "AZE");
			map.put("Bosnia-Herzegovina", "BIH");
			map.put("Bosnia and Herzegovina", "BIH");
			map.put("Barbados", "BRB");
			map.put("Bangladesh", "BGD");
			map.put("Belgium", "BEL");
			map.put("Burkina Faso", "BFA");
			map.put("Bulgaria", "BGR");
			map.put("Bahrain", "BHR");
			map.put("Burundi", "BDI");
			map.put("Benin", "BEN");
			map.put("Bermuda", "BMU");
			map.put("Brunei Darussalam", "BRN");
			map.put("Bolivia", "BOL");
			map.put("Brazil", "BRA");
			map.put("Bahamas", "BHS");
			map.put("Bhutan", "BTN");
			map.put("Botswana", "BWA");
			map.put("Belarus", "BLR");
			map.put("Belize", "BLZ");
			map.put("Canada", "CAN");
			map.put("Central African Republic", "CAF");
			map.put("Congo, The Democratic Republic Of The", "COD");
			map.put("The Democratic Republic Of The Congo", "COD");
			map.put("Congo", "COG");
			map.put("Switzerland", "CHE");
			map.put("Cook Islands", "COK");
			map.put("Chile", "CHL");
			map.put("Cameroon", "CMR");
			map.put("China", "CHN");
			map.put("Colombia", "COL");
			map.put("Costa Rica", "CRI");
			map.put("Cuba", "CUB");
			map.put("Cape Verde", "CPV");
			map.put("Cabo Verde", "CPV");
			map.put("Cyprus", "CYP");
			map.put("Czech Republic", "CZE");
			map.put("Germany", "DEU");
			map.put("Djibouti", "DJI");
			map.put("Denmark", "DNK");
			map.put("Dominica", "DMA");
			map.put("Dominican Republic", "DOM");
			map.put("Algeria", "DZA");
			map.put("Ecuador", "ECU");
			map.put("Estonia", "EST");
			map.put("Egypt", "EGY");
			map.put("Western Sahara", "ESH");
			map.put("Eritrea", "ERI");
			map.put("Spain", "ESP");
			map.put("Ethiopia", "ETH");
			map.put("Finland", "FIN");
			map.put("Fiji", "FJI");
			map.put("Falkland Islands", "FLK");
			map.put("Malvinas", "FLK");
			map.put("Micronesia", "FSM");
			map.put("Federated States of Micronesia", "FSM");
			map.put("France", "FRA");
			map.put("Gabon", "GAB");
			map.put("Great Britain", "GBR");
			map.put("United Kingdom", "GBR");
			map.put("United Kingdom of Greate Britain and Northern Ireland", "GBR");
			map.put("UK", "GBR");
			map.put("U.K.", "GBR");
			map.put("Grenada", "GRD");
			map.put("Georgia", "GEO");
			map.put("French Guyana", "GUF");
			map.put("Ghana", "GHA");
			map.put("Gibraltar", "GIB");
			map.put("Greenland", "GRL");
			map.put("Gambia", "GMB");
			map.put("Guinea", "GIN");
			map.put("Guadeloupe", "GLP");
			map.put("Equatorial Guinea", "GNQ");
			map.put("Greece", "GRC");
			map.put("Guatemala", "GTM");
			map.put("Guam", "GUM");
			map.put("Guinea Bissau", "GNB");
			map.put("Guinea-Bissau", "GNB");
			map.put("Guyana", "GUY");
			map.put("Hong Kong", "HKG");
			map.put("Hong Kong Special Administrative Region", "HKG");
			map.put("Honduras", "HND");
			map.put("Croatia", "HRV");
			map.put("Haiti", "HTI");
			map.put("Hungary", "HUN");
			map.put("Indonesia", "IDN");
			map.put("Ireland", "IRL");
			map.put("Israel", "IRL");
			map.put("India", "IND");
			map.put("Iraq", "IRQ");
			map.put("Islamic Republic of Iran", "IRN");
			map.put("Iceland", "ISL");
			map.put("Italy", "ITA");
			map.put("Jamaica", "JAM");
			map.put("Jordan", "JOR");
			map.put("Japan", "JPN");
			map.put("Kenya", "KEN");
			map.put("Kyrgyzstan", "KAZ");
			map.put("Cambodia", "KHM");
			map.put("Kiribati", "KIR");
			map.put("Comoros", "COM");
			map.put("Saint Kitts and Nevis", "KNA");
			map.put("North Korea", "PRK");
			map.put("Democratic People's Republic of Korea", "PRK");
			map.put("South Korea", "KOR");
			map.put("Republic of Korea", "KOR");
			map.put("Kuwait", "KWT");
			map.put("Cayman Islands", "CYM");
			map.put("Kazakhstan", "KAZ");
			map.put("Laos", "LAO");
			map.put("Lebanon", "LBN");
			map.put("Saint Lucia", "LCA");
			map.put("Liechtenstein", "LIE");
			map.put("Sri Lanka", "LKA");
			map.put("Liberia", "LBR");
			map.put("Lesotho", "LSO");
			map.put("Lithuania", "LTU");
			map.put("Luxembourg", "LUX");
			map.put("Latvia", "LVA");
			map.put("Libya", "LBY");
			map.put("Morocco", "MAR");
			map.put("Monaco", "MCO");
			map.put("Madagascar", "MDG");
			map.put("Marshall Islands", "MHL");
			map.put("Macedonia", "MKD");
			map.put("Mali", "MLI");
			map.put("Myanmar", "MMR");
			map.put("Mongolia", "MNG");
			map.put("Northern Mariana Islands", "MNP");
			map.put("Martinique", "MTQ");
			map.put("Mauritania", "MRT");
			map.put("Montserrat", "MSR");
			map.put("Malta", "MLT");
			map.put("Mauritius", "MUS");
			map.put("Maldives", "MDV");
			map.put("Malawi", "MWI");
			map.put("Mexico", "MEX");
			map.put("Malaysia", "MYS");
			map.put("Mozambique", "MOZ");
			map.put("Namibia", "NAM");
			map.put("New Caledonia", "NCL");
			map.put("Niger", "NER");
			map.put("Norfolk Island", "NFK");
			map.put("Nigeria", "NGA");
			map.put("Nicaragua", "NIC");
			map.put("Netherlands", "NLD");
			map.put("Norway", "NOR");
			map.put("Nepal", "NPL");
			map.put("Nauru", "NRU");
			map.put("Niue", "NIU");
			map.put("New Zealand", "NZL");
			map.put("Oman", "OMN");
			map.put("Panama", "PAN");
			map.put("Peru", "PER");
			map.put("French Polynesia", "PYF");
			map.put("Papua New Guinea", "PNG");
			map.put("Philippines", "PHL");
			map.put("Pakistan", "PAK");
			map.put("Poland", "POL");
			map.put("Saint Pierre And Miquelon", "SPM");
			map.put("Pitcairn", "PCN");
			map.put("Puerto Rico", "PRI");
			map.put("Portugal", "PRT");
			map.put("Palau", "PLW");
			map.put("Paraguay", "PRY");
			map.put("Qatar", "QAT");
			map.put("Reunion", "REU");
			map.put("Romania", "ROU");
			map.put("Russian Federation", "RUS");
			map.put("Russia", "RUS");
			map.put("Rwanda", "RWA");
			map.put("Saudi Arabia", "SAU");
			map.put("Solomon Islands", "SLB");
			map.put("Seychelles", "SYC");
			map.put("Sudan", "SDN");
			map.put("Sweden", "SWE");
			map.put("Singapore", "SGP");
			map.put("Saint Helena", "SHN");
			map.put("Slovenia", "SVN");
			map.put("Svalbard And Jan Mayen Islands", "SJM");
			map.put("Slovakia", "SVK");
			map.put("Sierra Leone", "SLE");
			map.put("San Marino", "SMR");
			map.put("Senegal", "SEN");
			map.put("Somalia", "SOM");
			map.put("Suriname", "SUR");
			map.put("Saint Tome and Principe", "STP");
			map.put("Sao Tome and Principe", "STP");
			map.put("El Salvador", "SLV");
			map.put("Syria", "SYR");
			map.put("Syrian Arab Republic", "SYR");
			map.put("Swaziland", "SWZ");
			map.put("Turks And Caicos Islands", "TCA");
			map.put("Chad", "TCD");
			map.put("Togo", "TGO");
			map.put("Thailand", "THA");
			map.put("Tajikistan", "TJK");
			map.put("Tokelau", "TKL");
			map.put("Turkmenistan", "TKM");
			map.put("Tunisia", "TUN");
			map.put("Tonga", "TON");
			map.put("Turkey", "TR");
			map.put("Trinidad And Tobago", "TTO");
			map.put("Tuvalu", "TUV");
			map.put("Tanzania", "TZA");
			map.put("Ukraine", "UKR");
			map.put("Uganda", "UGA");
			map.put("United Kingdom", "GBR");
			map.put("United States", "USA");
			map.put("United States of America", "USA");
			map.put("US", "USA");
			map.put("USA", "USA");
			map.put("U.S.", "USA");
			map.put("U.S.A.", "USA");
			map.put("Uruguay", "URY");
			map.put("Uzbekistan", "UZB");
			map.put("Holy See", "VAT");
			map.put("Vatican City State", "VAT");
			map.put("Vatican", "VA");
			map.put("Saint Vincent & Grenadines", "VCT");
			map.put("Venezuela", "VEN");
			map.put("British Virgin Islands", "VGB");
			map.put("United States Virgin Islands", "VIR");
			map.put("Vietnam", "VNM");
			map.put("Viet Nam", "VNM");
			map.put("Vanuatu", "VUT");
			map.put("Wallis And Futuna Islands", "WLF");
			map.put("Samoa", "WSM");
			map.put("Yemen", "YEM");
			map.put("Mayotte", "MYT");
			map.put("South Africa", "ZAF");
			map.put("Zambia", "ZMB");
			map.put("Zimbabwe", "ZWE");
		}
		// TODO change countries to lower case

		currCountry = country;
		if (map.containsKey(country)) {
			currCountryCode = map.get(country);
		} else {
			currCountryCode = country;
		}
	}

	/**
	 * Object storing a Month, and the absolute difference between the average
	 * temperature for that month and {@link IDEAL_TEMP}
	 * 
	 * @author ikba
	 *
	 */
	private static class MonthAndTempDelta {
		/**
		 * The Month that is {@link delta} degrees Celsius from
		 * {@link IDEAL_TEMP} on average
		 */
		final Month month;

		/**
		 * Absolute difference of average temperature in this {@link month} and
		 * {@link IDEAL_TEMP}
		 */
		final double delta;

		public MonthAndTempDelta(Month month, double temperatureDifferenceFromBest) {
			this.month = month;
			this.delta = temperatureDifferenceFromBest;
		}

		public Month getMonth() {
			return month;
		}

		public double getTempDelta() {
			return delta;
		}
	}
}
