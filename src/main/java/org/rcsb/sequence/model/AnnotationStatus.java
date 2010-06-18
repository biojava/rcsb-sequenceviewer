package org.rcsb.sequence.model;

import java.io.Serializable;


/**
 * Enumerates the possible states of an {@link AnnotationGroup}
 * @author mulvaney
 *
 */
public enum AnnotationStatus implements Serializable{

   instantiated, underConstruction, populated, noData, destroyed;
   
}
