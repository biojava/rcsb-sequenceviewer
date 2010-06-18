package org.rcsb.sequence.core;

import java.io.Serializable;

import org.rcsb.sequence.model.AnnotationValue;

public abstract class AbstractAnnotationValue<T> implements AnnotationValue<T>, Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 2213552411436796111L;

   public String getUrl() 
   {
      return "#";
   }

   public boolean isExternalData()
   {
      return false;
   }

}
