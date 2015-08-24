package Planit.fakeevent.ci;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class loadArff {

	public static void main(String[] args) throws IOException {
		
		ArffLoader loader = new ArffLoader();
		File inFile = new File("./data/test.arff");
		Instances data;
		
		//load the data from the file
		loader.setSource(inFile);
		data = loader.getDataSet();

		System.out.println(data.toSummaryString());
	}

}
