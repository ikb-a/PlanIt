package Planit.ci.ml;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import edu.toronto.cs.se.ci.Source;
import weka.core.Attribute;

public final class Util {

	/**
	 * Returns a numeric attribute which corresponds to the source.
	 * @param source
	 * @return
	 */
	static Attribute sourceToAttribute(Source<?, ?, ?> source){
		Attribute attribute;
		attribute = new Attribute(source.getName());
		return attribute;
	}
	
	static List<Attribute> attributeEnumerationToList(Enumeration<Attribute> attributes){
		List<Attribute> asList = new ArrayList<Attribute>();
		while (attributes.hasMoreElements()){
			asList.add(attributes.nextElement());
		}
		return asList;
	}
}
