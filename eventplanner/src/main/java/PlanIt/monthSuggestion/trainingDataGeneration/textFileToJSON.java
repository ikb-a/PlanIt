package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Quick program to convert the text files I had of events into usable .json
 * files. Unrelated to rest of program.
 * 
 * @author ikba
 *
 */
public class textFileToJSON {
	public static final String FOLDER = "./src/main/resources/data/monthData/CI/";

	public static void main(String[] args) throws IOException, InterruptedException {

		textToJSON(FOLDER + "1_January.txt");
		textToJSON(FOLDER + "2_February.txt");
		textToJSON(FOLDER + "3_March.txt");
		textToJSON(FOLDER + "4_April.txt");
		textToJSON(FOLDER + "5_May.txt");
		textToJSON(FOLDER + "6_June.txt");
		textToJSON(FOLDER + "7_July.txt");
		textToJSON(FOLDER + "8_August.txt");
		textToJSON(FOLDER + "9_September.txt");
		textToJSON(FOLDER + "10_October.txt");
		textToJSON(FOLDER + "11_November.txt");
		textToJSON(FOLDER + "12_December.txt");

	}

	public static void textToJSON(String path) throws IOException {
		File f = new File(path);
		String result = "{\n\"events\" : [";
		String template = "\n\u0009{\n\u0009\"title\" : \"%s\",\n\u0009\"description\" : \"%s\",\n\u0009\"keyWords\":[%s],\n\u0009\"venue\" : {\n\u0009\u0009\"address\" : {\n\u0009\u0009\u0009\"country\" : \"%s\"\n\u0009\u0009\u0009}\n\u0009\u0009}\n\u0009},";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		String line = br.readLine();
		while (line != null) {
			String name = line.replaceAll("\"", "");
			String desc = br.readLine().replaceAll("\"", "");
			String keywordsAsString = br.readLine().replaceAll("\"", "");
			List<String> keywords = new ArrayList<String>();

			Pattern p = Pattern.compile("\\('(.*?)', \\d, \\d\\)");
			Matcher m = p.matcher(keywordsAsString);
			while (m.find()) {
				String keyword = m.group(1);
				keyword = keyword.replaceAll(" \\x92", "'");
				keywords.add("\"" + keyword + "\"");
			}
			keywordsAsString = keywords.toString();
			keywordsAsString = keywordsAsString.substring(1, keywordsAsString.length() - 1);

			String country = br.readLine();
			assert (country != null);
			result += String.format(template, name, desc, keywordsAsString, country);
			line = br.readLine();
		}
		br.close();
		result = result.substring(0, result.length() - 1);
		result += "\u0009]\n}";

		File f2 = new File(path.replace(".txt", ".json"));
		FileWriter fw = new FileWriter(f2);
		fw.write(result);
		fw.close();
	}

}
