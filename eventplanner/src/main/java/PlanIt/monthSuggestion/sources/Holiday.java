package PlanIt.monthSuggestion.sources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import Planit.dataObjects.Address;
import Planit.dataObjects.Event;
import Planit.dataObjects.Venue;
import edu.toronto.cs.se.ci.UnknownException;
import edu.toronto.cs.se.ci.budget.Expenditure;
import edu.toronto.cs.se.ci.machineLearning.MLBasicSource;

public class Holiday extends MLBasicSource<Event, Month> implements MLMonthSuggestionContract {
	private static Map<String, String> nameToISOCode;

	public Holiday() {
		if (nameToISOCode == null) {
			nameToISOCode = new HashMap<String, String>();
			nameToISOCode.put("Andorra, Principality Of", "AD");
			nameToISOCode.put("United Arab Emirates", "AE");
			nameToISOCode.put("Afghanistan, Islamic State Of", "AF");
			nameToISOCode.put("Antigua And Barbuda", "AG");
			nameToISOCode.put("Anguilla", "AI");
			nameToISOCode.put("Albania", "AL");
			nameToISOCode.put("Armenia", "AM");
			nameToISOCode.put("Netherlands Antilles", "AN");
			nameToISOCode.put("Angola", "AO");
			nameToISOCode.put("Antarctica", "AQ");
			nameToISOCode.put("Argentina", "AR");
			nameToISOCode.put("American Samoa", "AS");
			nameToISOCode.put("Austria", "AT");
			nameToISOCode.put("Australia", "AU");
			nameToISOCode.put("Aruba", "AW");
			nameToISOCode.put("Azerbaidjan", "AZ");
			nameToISOCode.put("Bosnia-Herzegovina", "BA");
			nameToISOCode.put("Barbados", "BB");
			nameToISOCode.put("Bangladesh", "BD");
			nameToISOCode.put("Belgium", "BE");
			nameToISOCode.put("Burkina Faso", "BF");
			nameToISOCode.put("Bulgaria", "BG");
			nameToISOCode.put("Bahrain", "BH");
			nameToISOCode.put("Burundi", "BI");
			nameToISOCode.put("Benin", "BJ");
			nameToISOCode.put("Bermuda", "BM");
			nameToISOCode.put("Brunei Darussalam", "BN");
			nameToISOCode.put("Bolivia", "BO");
			nameToISOCode.put("Brazil", "BR");
			nameToISOCode.put("Bahamas", "BS");
			nameToISOCode.put("Bhutan", "BT");
			nameToISOCode.put("Bouvet Island", "BV");
			nameToISOCode.put("Botswana", "BW");
			nameToISOCode.put("Belarus", "BY");
			nameToISOCode.put("Belize", "BZ");
			nameToISOCode.put("Canada", "CA");
			nameToISOCode.put("Cocos (Keeling) Islands", "CC");
			nameToISOCode.put("Central African Republic", "CF");
			nameToISOCode.put("Congo, The Democratic Republic Of The", "CD");
			nameToISOCode.put("Congo", "CG");
			nameToISOCode.put("Switzerland", "CH");
			nameToISOCode.put("Ivory Coast (Cote D'Ivoire)", "CI");
			nameToISOCode.put("Cook Islands", "CK");
			nameToISOCode.put("Chile", "CL");
			nameToISOCode.put("Cameroon", "CM");
			nameToISOCode.put("China", "CN");
			nameToISOCode.put("Colombia", "CO");
			nameToISOCode.put("Costa Rica", "CR");
			nameToISOCode.put("Former Czechoslovakia", "CS");
			nameToISOCode.put("Cuba", "CU");
			nameToISOCode.put("Cape Verde", "CV");
			nameToISOCode.put("Christmas Island", "CX");
			nameToISOCode.put("Cyprus", "CY");
			nameToISOCode.put("Czech Republic", "CZ");
			nameToISOCode.put("Germany", "DE");
			nameToISOCode.put("Djibouti", "DJ");
			nameToISOCode.put("Denmark", "DK");
			nameToISOCode.put("Dominica", "DM");
			nameToISOCode.put("Dominican Republic", "DO");
			nameToISOCode.put("Algeria", "DZ");
			nameToISOCode.put("Ecuador", "EC");
			nameToISOCode.put("Estonia", "EE");
			nameToISOCode.put("Egypt", "EG");
			nameToISOCode.put("Western Sahara", "EH");
			nameToISOCode.put("Eritrea", "ER");
			nameToISOCode.put("Spain", "ES");
			nameToISOCode.put("Ethiopia", "ET");
			nameToISOCode.put("Finland", "FI");
			nameToISOCode.put("Fiji", "FJ");
			nameToISOCode.put("Falkland Islands", "FK");
			nameToISOCode.put("Micronesia", "FM");
			nameToISOCode.put("Faroe Islands", "FO");
			nameToISOCode.put("France", "FR");
			nameToISOCode.put("France (European Territory)", "FX");
			nameToISOCode.put("Gabon", "GA");
			nameToISOCode.put("Great Britain", "UK");
			nameToISOCode.put("Grenada", "GD");
			nameToISOCode.put("Georgia", "GE");
			nameToISOCode.put("French Guyana", "GF");
			nameToISOCode.put("Ghana", "GH");
			nameToISOCode.put("Gibraltar", "GI");
			nameToISOCode.put("Greenland", "GL");
			nameToISOCode.put("Gambia", "GM");
			nameToISOCode.put("Guinea", "GN");
			nameToISOCode.put("Guadeloupe (French)", "GP");
			nameToISOCode.put("Equatorial Guinea", "GQ");
			nameToISOCode.put("Greece", "GR");
			nameToISOCode.put("S. Georgia & S. Sandwich Isls.", "GS");
			nameToISOCode.put("Guatemala", "GT");
			nameToISOCode.put("Guam (USA)", "GU");
			nameToISOCode.put("Guinea Bissau", "GW");
			nameToISOCode.put("Guyana", "GY");
			nameToISOCode.put("Hong Kong", "HK");
			nameToISOCode.put("Heard And McDonald Islands", "HM");
			nameToISOCode.put("Honduras", "HN");
			nameToISOCode.put("Croatia", "HR");
			nameToISOCode.put("Haiti", "HT");
			nameToISOCode.put("Hungary", "HU");
			nameToISOCode.put("Indonesia", "ID");
			nameToISOCode.put("Ireland", "IE");
			nameToISOCode.put("Israel", "IL");
			nameToISOCode.put("India", "IN");
			nameToISOCode.put("British Indian Ocean Territory", "IO");
			nameToISOCode.put("Iraq", "IQ");
			nameToISOCode.put("Iran", "IR");
			nameToISOCode.put("Iceland", "IS");
			nameToISOCode.put("Italy", "IT");
			nameToISOCode.put("Jamaica", "JM");
			nameToISOCode.put("Jordan", "JO");
			nameToISOCode.put("Japan", "JP");
			nameToISOCode.put("Kenya", "KE");
			nameToISOCode.put("Kyrgyz Republic (Kyrgyzstan)", "KG");
			nameToISOCode.put("Cambodia, Kingdom Of", "KH");
			nameToISOCode.put("Kiribati", "KI");
			nameToISOCode.put("Comoros", "KM");
			nameToISOCode.put("Saint Kitts & Nevis Anguilla", "KN");
			nameToISOCode.put("North Korea", "KP");
			nameToISOCode.put("South Korea", "KR");
			nameToISOCode.put("Kuwait", "KW");
			nameToISOCode.put("Cayman Islands", "KY");
			nameToISOCode.put("Kazakhstan", "KZ");
			nameToISOCode.put("Laos", "LA");
			nameToISOCode.put("Lebanon", "LB");
			nameToISOCode.put("Saint Lucia", "LC");
			nameToISOCode.put("Liechtenstein", "LI");
			nameToISOCode.put("Sri Lanka", "LK");
			nameToISOCode.put("Liberia", "LR");
			nameToISOCode.put("Lesotho", "LS");
			nameToISOCode.put("Lithuania", "LT");
			nameToISOCode.put("Luxembourg", "LU");
			nameToISOCode.put("Latvia", "LV");
			nameToISOCode.put("Libya", "LY");
			nameToISOCode.put("Morocco", "MA");
			nameToISOCode.put("Monaco", "MC");
			nameToISOCode.put("Moldavia", "MD");
			nameToISOCode.put("Madagascar", "MG");
			nameToISOCode.put("Marshall Islands", "MH");
			nameToISOCode.put("Macedonia", "MK");
			nameToISOCode.put("Mali", "ML");
			nameToISOCode.put("Myanmar", "MM");
			nameToISOCode.put("Mongolia", "MN");
			nameToISOCode.put("Macau", "MO");
			nameToISOCode.put("Northern Mariana Islands", "MP");
			nameToISOCode.put("Martinique (French)", "MQ");
			nameToISOCode.put("Mauritania", "MR");
			nameToISOCode.put("Montserrat", "MS");
			nameToISOCode.put("Malta", "MT");
			nameToISOCode.put("Mauritius", "MU");
			nameToISOCode.put("Maldives", "MV");
			nameToISOCode.put("Malawi", "MW");
			nameToISOCode.put("Mexico", "MX");
			nameToISOCode.put("Malaysia", "MY");
			nameToISOCode.put("Mozambique", "MZ");
			nameToISOCode.put("Namibia", "NA");
			nameToISOCode.put("New Caledonia (French)", "NC");
			nameToISOCode.put("Niger", "NE");
			nameToISOCode.put("Norfolk Island", "NF");
			nameToISOCode.put("Nigeria", "NG");
			nameToISOCode.put("Nicaragua", "NI");
			nameToISOCode.put("Netherlands", "NL");
			nameToISOCode.put("Norway", "NO");
			nameToISOCode.put("Nepal", "NP");
			nameToISOCode.put("Nauru", "NR");
			nameToISOCode.put("Neutral Zone", "NT");
			nameToISOCode.put("Niue", "NU");
			nameToISOCode.put("New Zealand", "NZ");
			nameToISOCode.put("Oman", "OM");
			nameToISOCode.put("Panama", "PA");
			nameToISOCode.put("Peru", "PE");
			nameToISOCode.put("Polynesia (French)", "PF");
			nameToISOCode.put("Papua New Guinea", "PG");
			nameToISOCode.put("Philippines", "PH");
			nameToISOCode.put("Pakistan", "PK");
			nameToISOCode.put("Poland", "PL");
			nameToISOCode.put("Saint Pierre And Miquelon", "PM");
			nameToISOCode.put("Pitcairn Island", "PN");
			nameToISOCode.put("Puerto Rico", "PR");
			nameToISOCode.put("Portugal", "PT");
			nameToISOCode.put("Palau", "PW");
			nameToISOCode.put("Paraguay", "PY");
			nameToISOCode.put("Qatar", "QA");
			nameToISOCode.put("Reunion (French)", "RE");
			nameToISOCode.put("Romania", "RO");
			nameToISOCode.put("Russian Federation", "RU");
			nameToISOCode.put("Rwanda", "RW");
			nameToISOCode.put("Saudi Arabia", "SA");
			nameToISOCode.put("Solomon Islands", "SB");
			nameToISOCode.put("Seychelles", "SC");
			nameToISOCode.put("Sudan", "SD");
			nameToISOCode.put("Sweden", "SE");
			nameToISOCode.put("Singapore", "SG");
			nameToISOCode.put("Saint Helena", "SH");
			nameToISOCode.put("Slovenia", "SI");
			nameToISOCode.put("Svalbard And Jan Mayen Islands", "SJ");
			nameToISOCode.put("Slovak Republic", "SK");
			nameToISOCode.put("Sierra Leone", "SL");
			nameToISOCode.put("San Marino", "SM");
			nameToISOCode.put("Senegal", "SN");
			nameToISOCode.put("Somalia", "SO");
			nameToISOCode.put("Suriname", "SR");
			nameToISOCode.put("Saint Tome (Sao Tome) And Principe", "ST");
			nameToISOCode.put("Former USSR", "SU");
			nameToISOCode.put("El Salvador", "SV");
			nameToISOCode.put("Syria", "SY");
			nameToISOCode.put("Swaziland", "SZ");
			nameToISOCode.put("Turks And Caicos Islands", "TC");
			nameToISOCode.put("Chad", "TD");
			nameToISOCode.put("French Southern Territories", "TF");
			nameToISOCode.put("Togo", "TG");
			nameToISOCode.put("Thailand", "TH");
			nameToISOCode.put("Tadjikistan", "TJ");
			nameToISOCode.put("Tokelau", "TK");
			nameToISOCode.put("Turkmenistan", "TM");
			nameToISOCode.put("Tunisia", "TN");
			nameToISOCode.put("Tonga", "TO");
			nameToISOCode.put("East Timor", "TP");
			nameToISOCode.put("Turkey", "TR");
			nameToISOCode.put("Trinidad And Tobago", "TT");
			nameToISOCode.put("Tuvalu", "TV");
			nameToISOCode.put("Taiwan", "TW");
			nameToISOCode.put("Tanzania", "TZ");
			nameToISOCode.put("Ukraine", "UA");
			nameToISOCode.put("Uganda", "UG");
			nameToISOCode.put("United Kingdom", "UK");
			nameToISOCode.put("USA Minor Outlying Islands", "UM");
			nameToISOCode.put("United States", "US");
			nameToISOCode.put("Uruguay", "UY");
			nameToISOCode.put("Uzbekistan", "UZ");
			nameToISOCode.put("Holy See (Vatican City State)", "VA");
			nameToISOCode.put("Saint Vincent & Grenadines", "VC");
			nameToISOCode.put("Venezuela", "VE");
			nameToISOCode.put("Virgin Islands (British)", "VG");
			nameToISOCode.put("Virgin Islands (USA)", "VI");
			nameToISOCode.put("Vietnam", "VN");
			nameToISOCode.put("Vanuatu", "VU");
			nameToISOCode.put("Wallis And Futuna Islands", "WF");
			nameToISOCode.put("Samoa", "WS");
			nameToISOCode.put("Yemen", "YE");
			nameToISOCode.put("Mayotte", "YT");
			nameToISOCode.put("Yugoslavia", "YU");
			nameToISOCode.put("South Africa", "ZA");
			nameToISOCode.put("Zambia", "ZM");
			nameToISOCode.put("Zaire", "ZR");
			nameToISOCode.put("Zimbabwe", "ZW");
		}
	}

	public static void main(String[] args) throws UnknownException {
		Holiday bob = new Holiday();
		System.out.println(bob.getResponse(null));
	}

	@Override
	public Month getResponse(Event input) throws UnknownException {
		Venue venue = input.getVenue();
		Address address = venue.getAddress();
		String country = address.getCountry();

		List<String> keywords = input.getWords();
		List<String> definedKeywords = input.getKeyWords();
		if (definedKeywords != null) {
			keywords.addAll(definedKeywords);
			definedKeywords = null;
		}
		for (int x = 0; x < keywords.size(); x++) {
			keywords.set(x, keywords.get(x).toLowerCase());
		}

		String nationalHolidaysString = getHolidaysString(country);
		List<HolidayEvent> holidayEvents = stringToHolidays(nationalHolidaysString);
		List<HolidayEvent> matches = new ArrayList<HolidayEvent>();
		for (HolidayEvent holiday : holidayEvents) {
			if (keywords.contains(holiday.getName())) {
				matches.add(holiday);
			}
		}

		if (matches.isEmpty()) {
			throw new UnknownException();
		} else if (matches.size() == 1) {
			return matches.get(0).getMonth();
		} else {
			Month month = matches.get(0).getMonth();
			for (HolidayEvent match : matches) {
				if (match.getMonth() != month) {
					throw new UnknownException();
				}
			}
			return month;
		}
	}

	private List<HolidayEvent> stringToHolidays(String nationalHolidaysString) throws UnknownException {
		try {
			List<HolidayEvent> result = new ArrayList<HolidayEvent>();
			JSONObject data = new JSONObject(nationalHolidaysString);
			data = data.getJSONObject("holidays");
			Set<String> keys = data.keySet();
			for (String key : keys) {
				assert (key.length() == 10);
				int month = Integer.parseInt(key.substring(5, 7));

				JSONArray holidaysOnThisDate = data.getJSONArray(key);
				for (int x = 0; x < holidaysOnThisDate.length(); x++) {
					String name = holidaysOnThisDate.getJSONObject(x).getString("name");
					result.add(new HolidayEvent(name, month));
				}
			}
			return result;
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}

	private String getHolidaysString(String country) throws UnknownException {
		try {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			String urlString = "https://holidayapi.com/v1/holidays?key=ac6e9126-4fd4-4bee-aea4-914d7a8ba0d8&country=%s&year="
					+ year;

			String countryCode;
			if (nameToISOCode.containsKey(country)) {
				countryCode = nameToISOCode.get(country);
			} else {
				countryCode = country;
			}

			urlString = String.format(urlString, countryCode);

			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String result = "";
			String line = br.readLine();
			while (line != null) {
				result += line;
				line = br.readLine();
			}
			return result;
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}

	@Override
	public Expenditure[] getCost(Event args) throws Exception {
		// TODO Add values (time)
		return new Expenditure[] {};
	}

	private class HolidayEvent {
		final String name;
		final Month month;

		public HolidayEvent(String name, int monthAsInt) {
			if (name == null) {
				throw new IllegalArgumentException();
			}
			this.name = name.toLowerCase();

			switch (monthAsInt) {
			case 1:
				month = Month.January;
				break;
			case 2:
				month = Month.February;
				break;
			case 3:
				month = Month.March;
				break;
			case 4:
				month = Month.April;
				break;
			case 5:
				month = Month.May;
				break;
			case 6:
				month = Month.June;
				break;
			case 7:
				month = Month.July;
				break;
			case 8:
				month = Month.August;
				break;
			case 9:
				month = Month.September;
				break;
			case 10:
				month = Month.October;
				break;
			case 11:
				month = Month.November;
				break;
			case 12:
				month = Month.December;
				break;
			default:
				throw new IllegalArgumentException(monthAsInt + " is not within 1-12");
			}
		}

		public String getName() {
			return name;
		}

		public Month getMonth() {
			return month;
		}
	}
}
