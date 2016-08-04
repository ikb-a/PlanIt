package PlanIt.monthSuggestion.trainingDataGeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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
		String template = "\u0009{\n\u0009\"title\" : \"%s\",\n\u0009\"description\" : \"%s\",\n\u0009\"venue\" : {\n\u0009\u0009\"address\" : {\n\u0009\u0009\u0009\"country\" : \"%s\"\n\u0009\u0009\u0009}\n\u0009\u0009}\n\u0009},";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		String line = br.readLine();
		while (line != null) {
			String name = line.replaceAll("\"", "");
			String desc = br.readLine().replaceAll("\"", "");
			String country = br.readLine();
			assert (country != null);
			result += String.format(template, name, desc, country);
			line = br.readLine();
		}
		br.close();
		result = result.substring(0, result.length() - 2);
		result += "\u0009]\n}";

		File f2 = new File(path.replace(".txt", ".json"));
		FileWriter fw = new FileWriter(f2);
		fw.write(result);
		fw.close();
	}

}
