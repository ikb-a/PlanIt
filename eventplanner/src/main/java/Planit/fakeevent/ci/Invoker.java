package Planit.fakeevent.ci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import edu.toronto.cs.se.ci.utils.BasicSource;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 
 * A class for invoking a set of BasicSources on a set of inputs.
 *
 * @param <S> source type
 * @param <I> input type
 * @param <O> output type
 */
public abstract class Invoker <S extends BasicSource<I, ?, Void>, I, O>{

	protected String name;
	protected ArrayList<S> sources;
	protected O [][] results;
		
	public Invoker(String name){
		setName(name);
		if (sources == null){
			sources = new ArrayList<S>();
		}
	}
	
	/**
	 * Return a string to use as the attribute type
	 * when saving to ARFF format. See
	 * https://weka.wikispaces.com/ARFF+%28book+version%29
	 * @return
	 */
	abstract protected String getSourceArffType();
	
	/**
	 * Invoke sources on a single input
	 */
	@SuppressWarnings("unchecked")
	public O [] invoke(List<S> sources, I input){
		
		Object [] sourceResponses = new Object [sources.size()];
		for (int i = 0; i < sourceResponses.length; i++){
			try{
				sourceResponses[i] = sources.get(i).getResponse(input);
			}
			catch (Exception ex){
				sourceResponses[i] = -1;
			}
		}
		return (O[]) sourceResponses;
		
	}
	
	public O [][] getResults(){
		return results;
	}
	
	/**
	 * Invoke a set of sources on a set of inputs
	 */
	@SuppressWarnings("unchecked")
	public void invoke(List<S> sources, List<I> inputs){
		
		//create a 2d array or results where rows are inputs and columns are sources 
		results = (O[][]) new Object [inputs.size()][sources.size()];
		
		//invoke the sources on each event
		for (int i = 0; i < inputs.size(); i++){
			results[i] = invoke(sources, inputs.get(i));
		}
		
	}
	
	/**
	 * Saves the invoker's results in ARFF format.
	 * @param destination - an open file to write to
	 * @throws IOException - if there was any error writing to the file
	 */
 	public void saveToArff(File destination) throws IOException{
		
		saveToArff(destination, null);
		
	}

	/**
	 * Saves the invoker's results in ARFF format.
	 * @param destination - an open file to write to
	 * @param comment - A string to include as a comment at the top of the file.
	 * 					Newline characters are safe to use.
	 * @throws IOException - if there was any error writing to the file
	 */
	public void saveToArff(File destination, String comment) throws IOException{
		
		FileWriter writer = new FileWriter(destination);
		BufferedWriter buf = new BufferedWriter(writer);
		
		//write the file's header
		String header = getFileHeader();
		buf.write(header);
		buf.write("\n");

		//write the supplied comment, adding comment characters after newlines
		buf.write("% ");
		buf.write(comment.replace("\n", "\n% "));
		buf.write("\n\n");
		
		//write the name of the relation
		buf.write("@relation " + getName().replace(" ", "-") + "\n");
		buf.write("\n");

		//write the relation's attributes
		String attributeType = getSourceArffType();
		for (int i = 0; i < numSources(); i++){
			buf.write(
					String.format("@attribute %s %s\n",
					getSource(i).getName().replace(" ", "-"),
					attributeType));
		}
		buf.write("\n");
		
		//get the data
		O [][] data = getResults();
		
		//write the data
		buf.write("@data\n");
		for (int i = 0; i < data.length; i++){
			for (int j = 0; j < data[0].length; j++){
				buf.write(data[i][j].toString());
				if (j + 1 < data[0].length){
					buf.write(",");
				}
			}
			buf.write("\n");
		}

		buf.close();
		writer.close();
	}
	
	/**
	 * Returns a string which can be used as the header
	 * for a file containing the results of the invocation.
	 * Contains the date and time of creation.
	 */
	private String getFileHeader(){
				
		StringBuilder sb = new StringBuilder();
		
		//string the current date and time
		SimpleDateFormat sdf = new SimpleDateFormat("'Generated at' h:mm a 'on' EEE, MMM d, yyyy");
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		sb.append("% ");
		sb.append(sdf.format(date));
		sb.append("\n");
		
		return sb.toString();
	}
	
	/**
	 * Returns a weka dataset in the form of an Instances object.
	 * By convention, the class attribute is at index 0
	 * @param useStringValues - Only strings or doubles are accepted as values in a weka instance.
	 * 							If set to false then doubles will be used. The results stored in this
	 * 							object will be cast and then supplied to a weka instance.
	 * @return
	 * @throws Exception - throws an exception if the sources stored in this object have changed since
	 * 						they were last invoked to generate results.
	 */
	public Instances getResultInstances(boolean useStringValues) throws Exception {

		//sources changed since last invocation
		if (sources.size() != results[0].length){
			throw new Exception();
		}
		
		//set the attributes for the instances
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(sources.size());
		for (int i = 0; i < sources.size(); i++){
			String attributeName = sources.get(i).getName();
			attributes.add(new Attribute(attributeName));
		}
		
		//create the instances object
		Instances instances = new Instances(name, attributes, results.length);
		instances.setClassIndex(0);	
		
		//convert the results into instances
		O [][] results = getResults();
		for (int i = 0; i < results.length; i++){
			Instance instance = new DenseInstance(sources.size());
			instance.setDataset(instances);
			for (int j = 0; j < sources.size(); j++){
				//cast the results as necessary
				if (useStringValues){
					instance.setValue(j, String.valueOf(results[i][j]));
				}
				else{
					instance.setValue(j, Double.parseDouble(String.valueOf(results[i][j])));
				}
				
			}
			instances.add(instance);
		}
		
		return instances;
		
	}
	
	/**
	 * This method is used when an invocation was done
	 * which results in a 2d array. The sources are separated
	 * by commas and the events are separated by newlines.
	 */
	public String getFormattedResults(){
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < results.length; i++){
			for (int j = 0; j < results[0].length; j++){
				sb.append(results[i][j]);
				if (j + 1 < results[0].length){
					sb.append(",");
				}
			}
			if (i + 1 < results.length){
				sb.append("\n");
			}
		}
		
		return sb.toString();
		
	}	
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public boolean addSource(S source){
		return sources.add(source);
	}
	public boolean removeSource(S source){
		return sources.remove(source);
	}
	public int numSources(){
		return sources.size();
	}
	public S getSource(int i){
		return sources.get(i);
	}
}
