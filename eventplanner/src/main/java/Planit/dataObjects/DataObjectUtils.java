package Planit.dataObjects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;

/**
 * Utilities for data objects such as writing an object to a file
 * @author wginsberg
 *
 */
public class DataObjectUtils {

	static Gson gson;
	
	public static String asJson(Object o){
		return getGson().toJson(o);
	}

	public static boolean writeObjectJson(Object o, String outputFilePath){
		File outputFile;
		try{
			outputFile = new File(outputFilePath);
		}
		catch (NullPointerException e){
			return false;
		}
		return writeObjectJson(o, outputFile);
	}
	
	public static boolean writeObjectJson(Object o, File outputFile){
		OutputStreamWriter writer;
		try{
			writer = new FileWriter(outputFile);
		}
		catch (IOException e){
			return false;
		}
		boolean result = writeObjectJSON(o, writer);
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean writeObjectJson(Object o, OutputStream outStream){
		OutputStreamWriter writer = new OutputStreamWriter(outStream);
		return writeObjectJSON(o, writer);
	}
	
	public static boolean writeObjectJSON(Object o, OutputStreamWriter writer){
		try {
			writer.write(asJson(o));
			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private static Gson getGson(){
		if (gson == null){
			gson = new Gson();
		}
		return gson;
	}
}
