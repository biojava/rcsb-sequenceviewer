package org.rcsb.sequence.annotations;

import java.io.Serializable;

import org.rcsb.sequence.core.AbstractAnnotationValue;

public class DomainDefinitionValue extends AbstractAnnotationValue<String> implements Serializable, Comparable<DomainDefinitionValue>
{

   private static final long serialVersionUID = 1L;
   private final String value;
   private final String description;
   private final String predictionMethod;
   private final Character character;
   
   public DomainDefinitionValue(String value, String description, Character character, String predictionMethod)
   {
      if(value == null)
      {
         throw new RuntimeException("Domain annotation value name is null!");
      }
      this.value = value;
      this.predictionMethod = predictionMethod;
      this.description = description == null ? value : description;
      this.character   = character   == null && value.length() > 0 ? value.charAt(0) : character;
   }
   
   public DomainDefinitionValue(String value, String predictionMethod)
   {
      this(value, null, null, predictionMethod);
   }
   
   public String toString(){
	   StringBuffer buf = new StringBuffer();
	   
	   buf.append(this.getClass());
	   buf.append(" ");
	   buf.append("extends DomainDefinitionValue : ");
	   buf.append(value);
	   buf.append(" ");
	   buf.append(description);
	   buf.append(" ");
	   buf.append(predictionMethod);
	   
	   return buf.toString();
   }
   
   
   public String getDescription() {
      return description;
   }

   public Character toCharacter() {
      return character;
   }

   public String value() {
      return value;
   }
   
   public String getPredictionMethod() {
      return predictionMethod;
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((value == null) ? 0 : value.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final DomainDefinitionValue other = (DomainDefinitionValue) obj;
      if (value == null) {
         if (other.value != null)
            return false;
      } else if (!value.equals(other.value))
         return false;
      return true;
   }

   public int compareTo(DomainDefinitionValue arg0) {
      if(arg0 == null) throw new NullPointerException("Null not appropriate");
      
      return value.compareTo(arg0.value); // constructor ensures value not null
   }

   
   
   
}
