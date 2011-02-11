package org.rcsb.sequence.view.html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import org.rcsb.sequence.conf.Annotation2Jmol;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.core.AnnotationDrawMapper;

import org.rcsb.sequence.util.MapOfCollections;
import org.rcsb.sequence.view.multiline.Annotation2MultiLineDrawer;
import org.rcsb.sequence.view.multiline.ImageMapData;
import org.rcsb.sequence.view.multiline.SequenceImage;


public class PageView implements Serializable {
   
   private static final long serialVersionUID = 1L;

   protected final int page;
   
   protected final SortedSet<Chain> chains;
   protected transient Map<String, ChainView> chainViews = null;
   protected StringBuffer pdbChainList = new StringBuffer();
   
   protected final ViewParameters params;
   
   private static final boolean DEBUG = false;

	static  int counter = 0;
	public int id ;
   public PageView(int page, ViewParameters params)
   {
	   counter++;
	   id = counter;
      this.chains = new TreeSet<Chain>(params.getChainSortStrategy().comparator);
      this.page = page;
      this.params = params;
   }
   
   
   // todo: THis is never called. This still needs to be hooked into the system...
   public void destroy(){
	   if ( chainViews != null)
		   chainViews.clear();
	   chainViews = null;
	   if ( chains != null)
		   chains.clear();
	   
	   pdbChainList = null;
   }
   
   public void addChain(Chain c)
   {
      
      
      // add chain to collection
      chains.add(c);
      
      // add pdb chain id to string
      if(chains.size() > 1) pdbChainList.append(',');
      pdbChainList.append(c.getPdbChainId());
   }
   
   public Collection<ChainView> getChainViews()
   {
      initChainViews();
      return chainViews.values();
   }
   
   private  void initChainViews()
   {
      if(chainViews == null || chainViews.size() != chains.size())
      {
         chainViews = new LinkedHashMap<String, ChainView>();
         for(Chain c : chains)
         {
        	 System.out.println( "PageView: " + id + "creating new ChainView: " + c.getChainId());
            chainViews.put(c.getChainId(), new ChainView(c, params));
         }
         
      }
   }
   
   public ChainView getChainView(String chainId)
   {
      initChainViews();
      return chainViews.get(chainId);
   }
   
   public String getPdbChainIds()
   {
      return pdbChainList.toString();
   }
   
   public int getPageNum()
   {
      return page;
   }
   
   public int getChainCount()
   {
      return chains.size();
   }
   
   public boolean containsChain(Chain chain)
   {
      return chains.contains(chain);
   }
   
   public boolean doAnyChainsHaveADbRefMapping()
   {
      boolean result = false;
      for(Chain c : chains)
      {
         if(c.hasDbRefMapping()) 
         {
            result = true;
            break;
         }
      }
      return result;
   }
   
   public String getAnnotationJson()
   {
      return getAnnotationJson(null);
   }
   
   
   // WHAT IS THE URL FOR THIS REQUEST???
   //example call:
   // http://www.rcsb.org/pdb/explore/remediatedSequence.do?structureId=1CDG&returnJson=true
   public String getAnnotationJson(AnnotationName theAnnotation)
   {
	 
      try
      {
         JSONObject json = new JSONObject();
         boolean isDelta = theAnnotation != null;
         //PdbLogger.debug(isDelta ? "Generating delta JSON object with data only for " + theAnnotation.name() : "Generating full JSON object for all annotations");
         Collection<AnnotationName> singleton = Collections.singleton(theAnnotation);
         JSONObject annsJson = new JSONObject();
   
         json.put("isDelta", isDelta);
         
         // do we want the complete thing, or just data for a specific annotation?
         Collection<AnnotationName> allDisplayed = isDelta ? singleton : getAllAnnotationsDisplayed();
         
         AnnotationDrawMapper annotationDrawMapper = new Annotation2MultiLineDrawer();
         annotationDrawMapper.ensureInitialized();
//         if (chainViews != null && chainViews.size() > 0 ){
//        	 annotationDrawMapper = chainViews.get(0).getAnnotationDrawMapper();
//    	  
//         }
         
         for(AnnotationName an : allDisplayed)
         {
            JSONObject anJson = new JSONObject();
            if(Annotation2Jmol.hasJsonData(an))
            {
               anJson.put("entries", Annotation2Jmol.createAnnotationJsonObject(an, getAllAnnotationsDisplayedMap().get(an)));
               anJson.put("script",  Annotation2Jmol.getJmolScriptBuilderFunction(an.getClassification()));
            } else {
            	debug("PageView: no JSON data for " + an);
            }
            
            // some annotations are displayed because the ones chosen have no data but an equivalent annotation does
            anJson.put("isFallback", !params.getAnnotations().contains(an));
            anJson.put("name", an.getName());
            
            annsJson.put(an.getName(), anJson);
         }
         
         json.put("byAnnotation", annsJson);
   
         //PdbLogger.debug("By annotation data added to JSON: " + annsJson.length() + " data items");
         
         JSONObject chainList = new JSONObject();
         for(ChainView cv : getChainViews())
         {
            SequenceImage image 			= cv.getSequenceImage();
            while (image.isBuilding()){
            	System.out.println("waiting for image building...");
            }
            //SequenceSummaryImage sumImage 	= cv.getSequenceSummaryImage();
            
            JSONObject chainObj 		= new JSONObject();
            JSONObject imageObj 		= new JSONObject();
           
            //TODO: re-enable image summary...
            //JSONObject imageSummaryObj 	= new JSONObject();
            JSONObject mapData = new JSONObject();
            JSONObject anHeights = new JSONObject();
            imageObj.put("mapData", mapData);
            chainObj.put("origChainId", cv.getChain().getPdbChainId());
            chainObj.put("img", imageObj);
            
            //chainObj.put("imgsummary", imageSummaryObj);
            
            MapOfCollections<String, ImageMapData> allMaps = image.getAllMaps();
            Collection<String> annotationNames = isDelta ? Collections.singleton(theAnnotation.getName()) : allMaps.keySet();
            for(final String an : annotationNames)
            {
               JSONArray frags = new JSONArray();
               mapData.put(an, frags);
               
               Collection<ImageMapData> hmds = allMaps.get(an);
               
               // sometimes a ChainView will replace an annotation that is requested but has no data on that chain
               // for one that does. In these cases, the JSON object needs to know about the fallback annotation.
               
               // (we don't need to explicitly add the fallback annotation to the byAnnotation part of the JSON object
               // because getAllAnnotationsDisplayed() includes fallback annotations.)
               if(hmds == null)
               {
                  String fallback = getReplacementAnnotationNameIfPossible(an, cv);
                  if(!annotationNames.contains(fallback)) hmds = allMaps.get( fallback );
               }
               
               if(hmds == null) continue;
               
               for(ImageMapData hmd : hmds)
               {
                  JSONArray entries = new JSONArray();
                  frags.put(entries);
                  if ( hmd == null){
                	  System.err.println("PageView: ImageMapData is null! " + an);
                	  continue;
                  } else {
                	  debug("PageView: adding annotation for " + an);
                  }
                  for(ImageMapData.Entry e : hmd.getImageMapDataEntries())
                  {
                	//  PdbLogger.warn("PAgeView: getAnnotationJson " + e.getJson());
                     entries.put(e.getJson());
                  }
               }
            }
            
            for(final AnnotationName an : cv.getAnnotationsToView())
            {
               AnnotationGroup<?> ag = cv.getChain().getAnnotationGroup(an.getAnnotationClass());
               if(ag != null && annotationDrawMapper.hasSummaryTableRow(ag.getName().getName()) && (!isDelta || an == theAnnotation))
               {
                  JSONObject annotationObj = new JSONObject();
                  AnnotationSummaryCell<?> summary = annotationDrawMapper.createSummaryTableRowInstance(ag);
                  if(summary.hasData())
                  {
                     chainObj.put(an.getName(), annotationObj);
                     annotationObj.put(  "keyHtml", summary.getKeyCell().getEscapedHtml());
                     annotationObj.put("valueHtml", summary.getValueCell().getEscapedHtml());
                  }
                  else
                  {
                     continue;
                  }
               }
               anHeights.put(an.getName(), image.getAnnotationHeightPx(an));
            }
            
            System.out.println("PageView " + id + " sending JSON image width, height: " + image.getImageWidth() + " " + image.getImageHeight());
           
            imageObj.put("height", image.getImageHeight());
            imageObj.put("width", image.getImageWidth());
            imageObj.put("seqHeight", image.getSequenceHeightPx());
            imageObj.put("lRulerHeight", image.getRulerHeightPx());
            imageObj.put("uRulerHeight", image.getRulerHeightPx());
            imageObj.put("fragBufferHeight", image.getFragmentBufferPx());
            imageObj.put("fragmentCount", cv.getFragmentCount());
            imageObj.put("anHeights", anHeights);
            
            chainList.put(cv.getChain().getChainId(), chainObj);
         }
         json.put("byChain", chainList);
   
         //PdbLogger.debug("By chain data added to JSON: " + chainList.length() + " data items");
         
         if(!isDelta)
         {
            JSONArray annotationOrder = new JSONArray();
            for(AnnotationName an : AnnotationRegistry.getAllAnnotations())
            {
               boolean available = getAvailableAnnotationsNotDisplayed().containsKey(an);
               boolean visible = getAllAnnotationsDisplayedMap().containsKey(an) || an == theAnnotation;
               if(available || visible)
               {
                  JSONObject a = new JSONObject();
                  a.put("name", an.getName());
                  a.put("displayName", an.getName());
                  a.put("classification", an.getClassification().getName());
                  a.put("isVisible", visible);
                  JSONArray refArr = ReferenceJsonObject.get(an);
                  a.put("ref", refArr);
                  annotationOrder.put(a);
               }
            }
            json.put("all", annotationOrder);
            //PdbLogger.debug("Data about the order and visibility of annotations added to JSON: " + annotationOrder.length() + " data items");
         }
         
        // debug("PageView.java : ");
         //debug(json.toString());
   
         //PdbLogger.debug("Generated JSON");
         //return PdbLogger.isDebugOn() ? json.toString(2) : json.toString();
         return json.toString();
      }
      catch(Exception e)
      {
         System.err.println("Exception generating JSON. " +  e.getMessage());
         e.printStackTrace();
         return "{\"failure\": true}";
      }
   }
   
   public String getReplacementAnnotationNameIfPossible(String annotationName, ChainView cv)
   {
      String result = annotationName;
      if(params.isShowNextBestAnnotationIfPossible())
      {
         try
         {
            AnnotationName an = AnnotationRegistry.getAnnotationByName(annotationName);
            cv.getReplacementAnnotationGroupIfPossible(an);
         }
         catch (Exception e) {
        	 //PdbLogger.debug("No AnnotationName for string " + annotationName, e);
         }
      }
      return result;
   }

   public MapOfCollections<AnnotationName, Sequence> allAnnotationsDisplayed = null;
   public MapOfCollections<AnnotationName, Sequence> availableAnnotationsNotDisplayed = null;

   private MapOfCollections<AnnotationName, Sequence> getAllAnnotationsDisplayedMap()
   {
	   //PdbLogger.debug("In getAllAnnotationsDisplayedMap");
      if(allAnnotationsDisplayed == null || chainViews.values() != getChainViews())
      {
         allAnnotationsDisplayed = new MapOfCollections<AnnotationName, Sequence>(TreeMap.class, ArrayList.class);
         
         for(ChainView cv : getChainViews())
         {
            if(cv.getAnnotationsToView() != null)
            {
               allAnnotationsDisplayed.addAll(cv.getAnnotationsToView(), cv.getChain());
            }
         }
      }
      //PdbLogger.debug("Leaving getAllAnnotationsDisplayedMap");
      return allAnnotationsDisplayed;
   }
   
   private MapOfCollections<AnnotationName, Sequence> getAvailableAnnotationsNotDisplayed()
   {
      
      if(availableAnnotationsNotDisplayed == null || chainViews.values() != getChainViews())
      {
         availableAnnotationsNotDisplayed = new MapOfCollections<AnnotationName, Sequence>(TreeMap.class, ArrayList.class);
         
         for(ChainView cv : getChainViews())
         {
            if(cv.getOtherAnnotationNamesAvaialable() != null)
            {
               availableAnnotationsNotDisplayed.addAll(cv.getOtherAnnotationNamesAvaialable(), cv.getChain());
            }
         }
      }

      //debug("Leaving getAvailableAnnotationsNotDisplayed " + availableAnnotationsNotDisplayed.size() + " " + availableAnnotationsNotDisplayed.keySet());
      return availableAnnotationsNotDisplayed;
   }
   
   public Set<AnnotationName> getAllAnnotationsDisplayed()
   {
      return getAllAnnotationsDisplayedMap().keySet();
   }
   
   private void debug(String msg){
	   if (DEBUG)
		   System.out.println(msg);
   }
}
