package org.rcsb.sequence.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class AnnotationRegistry {
	static List<AnnotationName> availableAnnotations;
	static {
		availableAnnotations = new ArrayList<AnnotationName>();
	}
	private AnnotationRegistry(){
		
		
	}
	
	public static void registerAnnotation(AnnotationName an){
	
		System.out.println("registering new Annotation " + an.getName());
		if ( ! availableAnnotations.contains(an)){
			availableAnnotations.add(an);
		}
		
	}
	
	public static AnnotationName getAnnotationByName(String name){
		for (AnnotationName an : availableAnnotations){
			String n = an.getName();
			if ( n.equalsIgnoreCase(name)){
				return an;
			}
		}
		return null;
	}
	
	public static Collection<AnnotationName> getAllAnnotations(){
		
		return Collections.unmodifiableCollection( availableAnnotations);
	}
}
