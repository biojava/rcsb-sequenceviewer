package org.rcsb.sequence.conf;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;


public enum AnnotationClassification implements Serializable{
   
   secstr("Secondary Structure",
         "Assignment of secondary structure types -- for example: alpha helices; beta strands and sheets; turns -- to regions of a chain according to the geometry and hydrogen bonding interactions of their backbone"),
   
   structuralFeature("Structural Feature", 
         "Property of the structure (e.g. Disulphide bond)"),
         
   strdom("Domain Assignment",
         "The structure is divided into domains using information derived from structure"),
               
   func("Assigment of Function",
         "Function is assigned to the structure or a portion of it"),
   
   seqdom("Domain Assignment from Sequence",
         "The structure is divided into domains using information derived from multiple sequence alignment"),
   
   protmod("Protein Modification",
		 "Identification of protein modifications in a structure");
   
   AnnotationClassification(String name, String description)
   {
      annotationsClassifiedThus = new LinkedHashSet<AnnotationName>();
      this.name = name;
      this.description = description;
   }
   
   private final String name;
   private final String description;
   private final Set<AnnotationName> annotationsClassifiedThus;
   private AnnotationName defaultAnnotation = null;
   
   public Set<AnnotationName> getAnnotationsClassifiedThus() {
      return Collections.unmodifiableSet(annotationsClassifiedThus);
   }
   void addToAnnotationsClassifiedThus(AnnotationName an)
   {
	   //System.out.println(" setting default annotation for classification " +  name + " " + an.getName());
      this.annotationsClassifiedThus.add(an);
      if(defaultAnnotation == null)
      {
         defaultAnnotation = an;
      }
   }
   public String getDescription() {
      return description;
   }
   public String getName() {
      return name;
   }
   public AnnotationName getDefaultAnnotation() {
      return defaultAnnotation;
   }
   public AnnotationName getNextBestAnnotation(AnnotationName an)
   {
      Iterator<AnnotationName> anIt = an.getClassification().annotationsClassifiedThus.iterator();
      while(anIt.hasNext() && anIt.next() != an) { /* do nothing */ }
      return anIt.hasNext() ? anIt.next() : null;
   }
   
   public static final Collection<AnnotationClassification> DEFAULT_CLASSIFICATIONS_TO_VIEW;
   static
   {
      Collection<AnnotationClassification> foo = new LinkedList<AnnotationClassification>();
      foo.add(strdom);
      foo.add(secstr);
      foo.add(protmod);
      foo.add(structuralFeature);
      DEFAULT_CLASSIFICATIONS_TO_VIEW = Collections.unmodifiableCollection(foo);
   }
}
