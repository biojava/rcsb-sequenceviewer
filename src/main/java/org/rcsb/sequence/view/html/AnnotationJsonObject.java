package org.rcsb.sequence.view.html;

import java.awt.Color;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ColorWheelUtil;
import org.rcsb.sequence.view.multiline.SecondaryStructureDrawer;

import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;

public class AnnotationJsonObject extends JSONObject {
   
   public AnnotationJsonObject(AnnotationName an, Collection<Sequence> sequences)
   {
      initialise(an, sequences);
   }
   
   protected void initialise(AnnotationName an, Collection<Sequence> sequences)
   {
      for(Sequence seq : sequences)
      {
         initialise(seq.getAnnotationGroup(an.getAnnotationClass()));
      }
   }

   protected <T> void initialise(AnnotationGroup<T> ag)
   {
      if(ag == null)
      {
         return;
      }
      try 
      {
         for(Annotation<T> a : ag.getAnnotations())
         {
            initialise(a);
         }
      } 
      catch (JSONException e) 
      {
         throw new RuntimeException("Failed to create JSON object for AnnotationGroup " + ag.getName() + " on " + ag.getSequence(), e);
      }
   }

   protected <T> void initialise(Annotation<T> a) throws JSONException
   {
	  
	   
      String annotationValue = String.valueOf(a.getAnnotationValue().value());
      	   
      // either this is the first time we've seen this annotationValue, so we need to 
      // create the json object to represent it, or we've seen it before and just need
      // to add another residue range
      JSONObject aJson = (JSONObject) opt(annotationValue);
      JSONArray aRanges;
      if(aJson == null)
      {
         aJson = new JSONObject();
         aJson.put("label", annotationValue);
         put(annotationValue, aJson);
         
         Color c = null;
         if ( a.getAnnotationValue().value() instanceof SecondaryStructureSummary){
        	
        	c = getSecStrucColor(a.getAnnotationValue());
  	   } else {
  		   c = ColorWheelUtil.getArbitraryColor(a);
  	   }
         
         JSONObject aColor = new JSONObject();
         aJson.put("colour", aColor); // server-side: American, client-side: British
         
         aColor.put("r", c.getRed());
         aColor.put("g", c.getGreen());
         aColor.put("b", c.getBlue());
//         aColor.put("hex", ColorUtil.getColorHex(c));
         
         aRanges = new JSONArray();
         aJson.put("ranges", aRanges);
      }
      
      else
      {
         aRanges = aJson.getJSONArray("ranges"); // this will (correctly) throw a JSONException if it can't find 'ranges'
      }
      
      JSONObject aRange = new JSONObject();
      Sequence seq = a.getSequence();
      aRanges.put(aRange);
      aRange.put("chainId", seq.getChainId());
      aRange.put("startRes", seq.getFirstResidue(ATOM).getSeqIdWithInsertionCode());
      aRange.put("endRes", seq.getLastResidue(ATOM).getSeqIdWithInsertionCode());
   }
   
   public Color getSecStrucColor(@SuppressWarnings("rawtypes") AnnotationValue a){
	   return SecondaryStructureDrawer.SST_TO_COLOR_MAP.get(a);
	  
	   
   }
   
   
}
