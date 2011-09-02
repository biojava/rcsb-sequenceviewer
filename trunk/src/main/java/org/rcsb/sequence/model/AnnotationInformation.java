package org.rcsb.sequence.model;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;

/**
 * Base interface for methods common to both {@link Annotation} and {@link AnnotationGroup}
 * @author mulvaney
 *
 */
public interface AnnotationInformation {

   /**
    * Get the {@link ResidueNumberScheme} of the annotation
    * @return
    */
	public abstract ResidueNumberScheme getResidueNumberScheme();

	/**
	 * Get the {@link AnnotationClassification} of the annotation
	 * @return
	 */
	public abstract AnnotationClassification getClassification();

	/**
	 * Get the {@link AnnotationName} of the annotation
	 * @return
	 */
	public abstract AnnotationName getName();
	
			
		

}
