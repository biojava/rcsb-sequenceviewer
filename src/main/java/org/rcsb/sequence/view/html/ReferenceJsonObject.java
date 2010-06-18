package org.rcsb.sequence.view.html;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.model.PubMed;
import org.rcsb.sequence.model.Reference;

public class ReferenceJsonObject extends JSONObject 
{
   private static final Map<Reference, ReferenceJsonObject> theMap;
   static
   {
      Map<Reference, ReferenceJsonObject> foo = new HashMap<Reference, ReferenceJsonObject>();
      
      for(AnnotationName an : AnnotationRegistry.getAllAnnotations())
      {
         Reference r = an.getReference();
         if(r != null && r.getPmid() != null && r.getPmid() > -1L && r.getPubmed() != null) 
         {
            foo.put(r, new ReferenceJsonObject(r));
         }
      }
      
      theMap = Collections.unmodifiableMap(foo);
   }
   
   public static JSONObject get(AnnotationName an)
   {
      return theMap.get(an.getReference());
   }
   
   private ReferenceJsonObject(Reference r)
   {
      PubMed p = r.getPubmed();
      try
      {
         put("journal", p.getJournalTitle());
         put("issue", p.getIssueNumber());
         put("pages", p.getMedlinePages());
         put("authors", p.getAuthorList());
         put("title", p.getArticleTitle());
         put("pubYear", p.getPublishedYear());
         put("pubMonth", p.getPublishedMonth());
         put("pubDay", p.getPublishedDay());
         put("volume", p.getVolume());
         put("pmid", p.getPubmedId());
      }
      catch(Exception e)
      {
         System.err.println("Couldn't make JSONObject for reference with pmid " + r.getPmid() + " " +  e.getMessage());
      }
   }
}
