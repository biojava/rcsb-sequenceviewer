package org.rcsb.sequence.annotations;

import java.io.Serializable;

import org.rcsb.sequence.model.AnnotationValue;

public enum SecondaryStructureValue implements AnnotationValue<Character>, Serializable {
   empty(' ', "no secondary structure assigned"),
   H("alpha helix"),
   E("extended strand, participates in beta ladder", "beta strand"),
   T("hydrogen bonded turn", "turn"),
   B("residue in isolated beta-bridge", "beta bridge"),
   S("bend"),
   G("3-helix (3/10 helix)", "3/10-helix"),
   I("5-helix (pi helix)", "pi helix"),
   error('!', "error");
   
   SecondaryStructureValue(char code, String description)
   {
      this.code = code;
      this.description = description;
      this.shortDescription = description;
   }
   SecondaryStructureValue(String description, String shortDescription)
   {
      this.code = this.name().charAt(0);
      this.description = description;
      this.shortDescription = shortDescription;
   }
   SecondaryStructureValue(String description)
   {
      this(description, description);
   }
   
   public final char code;
   public final String description;
   public final String shortDescription;
   
   
   @Override
   public String toString() {
      return String.valueOf(code);
   }
   public Character toCharacter() {
      return value();
   }
   public Character value() {
      return code;
   }
   public String getDescription() {
      return description;
   }
   public static SecondaryStructureValue getTypeFromCharCode(Character code)
   {
      SecondaryStructureValue result = error;
      
      for(SecondaryStructureValue t : values())
      {
         if(t.code == code)
         {
            result = t;
            break;
         }
      }
      
      return result;
   }
   
   public static boolean isHelical(SecondaryStructureValue ssv)
   {
      return ssv == H || ssv == G || ssv == I;
   }
   public String getUrl() {
      return "#";
   }
   public boolean isExternalData() {
      return false;
   }
}
