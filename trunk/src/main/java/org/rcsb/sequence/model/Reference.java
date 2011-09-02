package org.rcsb.sequence.model;

import java.io.Serializable;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.core.PubMedProvider;




/**
 * Represents the reference to be cited for a given {@link AnnotationName}
 * @author mulvaney
 */
public class Reference implements Serializable {
   
   private static final long serialVersionUID = 1L;
   private final Long pmid;
   
   
   public static final Reference PRIMARY_DATA_REF = new Reference(10592235L);
   
   public Reference(Long pubmedId)
   {
      this.pmid = pubmedId;
   }
   
   /**
    * Get the PubMed id.
    * @return
    */
   public Long getPmid() {
      return pmid;
   }
   
   /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    @Override
    public String toString()
    {
        final String TAB = "    ";
    
        StringBuilder retValue = new StringBuilder();
        
        retValue.append("Reference ( ")
            .append(super.toString()).append(TAB)
            .append("pmid = ").append(this.pmid).append(TAB)
            .append(" )");
        
        return retValue.toString();
    }

   
    public PubMed getPubmed() {
    	return PubMedProvider.getPubMed(pmid);
    }
    
   
    
    
   
}
