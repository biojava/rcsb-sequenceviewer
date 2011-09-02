package org.rcsb.sequence.model;


/**
 * An {@link Annotation} assigns an <tt>AnnotationValue</tt> to a {@link Sequence}.  
 * <tt>AnnotationValue</tt> can encapsulate any value type, but also contains additional
 * information describing the value, such as {@link #getDescription()} and {@link #getUrl()}.
 * @author mulvaney
 *
 * @param <T> the type of the actual value. See {@link #value()}
 */
public interface AnnotationValue<T> {

   /**
    * Get the description of the value
    * @return
    */
   public abstract String getDescription();
   
   /**
    * Get a single-character representation of the value
    * @return
    */
   public abstract Character toCharacter();
   
   /**
    * Get a string representation of the <tt>AnnotationValue</tt>
    * @return
    */
   public abstract String toString();
   
   /**
    * Get the value itself.
    * @return
    */
   public abstract T value();
   
   /**
    * If {@link AnnotationValue#isExternalData()} returns <tt>true</tt>, this
    * method will return a URL pointing to the external site that determined the <tt>Annotation</tt>
    * @return the URL as a <tt>String</tt>
    */
   public abstract String getUrl();
   
   /**
    * Was this <tt>AnnotationValue</tt> derived from information sourced from some a third-party
    * resource -- i.e. not from the PDB?
    * @return
    */
   public boolean  isExternalData();
   
}
