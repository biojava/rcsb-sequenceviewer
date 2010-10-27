package org.rcsb.sequence.view.html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.rcsb.sequence.core.SequenceCollectionProvider;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;



/**
 * <p>This class is a display-oriented front-end to {@link SequenceCollection} -- it prepares a <tt>SequenceCollection</tt> 
 * for view according to the parameters in the supplied {@link ViewParameters}. It's responsibilities are as follows:</p>
 * <ul>
 * <li>Handle selection of and paging of Chains (it groups Chains by page and instantiates new ChainViews as necessary)</li>
 * <li>Manage available default-viewed annotations, sequences, parameters</li>
 * </ul>
 * @author mulvaney
 *
 */
public class SequenceCollectionView implements Serializable {

   private static final long serialVersionUID = 1L;
   
   private SequenceCollection sequenceCollection;
   
   private ViewParameters params;
   
   private SortedSet<Chain> sortedChains;
   
   private List<PageView> pages;
   
   private Map<String, PageView> chainPageMap;
   
   private static final boolean DEBUG = false;
   
   /**
    * Creates a new instance from the given structureId. The default {@link ViewParameters} are used.
    */
   public SequenceCollectionView(String structureId)
   {
	  
      this(SequenceCollectionProvider.get(structureId), new ViewParameters());
      
      
      
      if ( DEBUG)
		   System.out.println("SequenceCollectionView: " + structureId);
	   
   }
   
   /**
    * Creates a new instance from the supplied {@link SequenceCollection} and {@link ViewParameters}
    * @param sequenceCollection
    * @param viewParams
    */
   public SequenceCollectionView(SequenceCollection sequenceCollection, ViewParameters viewParams)
   {
	   
	 
	   
	   if ( sequenceCollection == null) {
		   String message = "SequenceCollectionView got null for sequenceCollection." ;
		   System.err.println(message);
		   throw new RuntimeException(message);
	   }
	   
	   if ( DEBUG)
		   System.out.println("SequenceCollectionView: " + sequenceCollection.getStructureId() + " seq coll:" + sequenceCollection.chainCount());
	   
      this.params = cloneParams(viewParams);
      this.sequenceCollection = sequenceCollection;
      
      // get collection of chains
      initChains();
      
      if (DEBUG) {
    	  System.out.println("SequenceCollectionView: Done initChains now pages:");
    	  if ( pages != null)
    		  System.out.println("pages: " + pages.size());
      }
      
      // separate chains into pages
      initPages();
      if (DEBUG)
    	  System.out.println("SequenceCollectionView: done init" );
   }

   private boolean shouldReInitChains(ViewParameters newParams)
   {
      return !(params.getChainSortStrategy().equals(newParams.getChainSortStrategy()) 
            && params.getChainEntityStrategy().equals(newParams.getChainEntityStrategy())
            && params.getChainViewStrategy().equals(newParams.getChainViewStrategy()));
   }
   
   private void initChains()
   {
      SortedSet<Chain> theChains;
      
      theChains = new TreeSet<Chain>(params.getChainSortStrategy().comparator);
    
      // get the correct initial set
      switch( params.getChainEntityStrategy() )
      {
      case all :
         theChains.addAll(sequenceCollection.getChains().values());
         break;
      case first :
         theChains.addAll(sequenceCollection.getFirstChainFromEachEntityMap().values());
         break;
      default :
         throw new RuntimeException("Unknown chainEntityStrategy: " + params.getChainEntityStrategy());
      }
      
      // filter according to the specified predicate
      CollectionUtils.filter(theChains, params.getChainViewStrategy().predicate);
      
      //for (Chain c : theChains){
    //	  c.ensureAnnotated();
      //}

      // set!
      sortedChains = Collections.unmodifiableSortedSet(theChains);
   }
   
   private void initPages()
   {
      pages = new ArrayList<PageView>();
      chainPageMap = new HashMap<String, PageView>();
      int chainCount  = 0;
      int currentPage = 0;
      int maxChains = params.getChainsPerPage();
      PageView pv;
      
      if (DEBUG)
    	  System.out.println("SequenceCollectionView: maxChains: " + maxChains);
      // we always show at least 1 chain if we can...
      
      //System.out.println("init Pages " + sequenceCollection.getStructureId()  + maxChains + " sortedChains:" + sortedChains.size());
      if (maxChains == 0 && sortedChains.size() > 0){
         maxChains = sortedChains.size();
      }
      //System.out.println("init Pages " + sequenceCollection.getStructureId()  + maxChains + " sortedChains:" + sortedChains.size());
      //params.setChainsPerPage(ViewParameters.CHAINS_PER_PAGE_DEFAULT);
      
      // get rid of irritating page with only one chain on it
      if(maxChains > 1 && sortedChains.size() == maxChains + 1)
      {
         ++maxChains;
         if ( DEBUG){
        	 System.out.println("SequenceCollectionView: setting ChainsPerPage : " + maxChains);
         }
         params.setChainsPerPage(maxChains);
      }
      
      
      int loopCounter = 0;
      Iterator<Chain> cIt = sortedChains.iterator();
      while(cIt.hasNext())
      {
    	
    	  if (DEBUG)
    		  System.out.println("current chainCount: " + chainCount + " pages: " + pages.size() + " maxChains: " + maxChains + " currentPage: " + currentPage);
         
    	  chainCount = 0;
         
         pages.add(pv = new PageView(++currentPage, params));
         
         while(cIt.hasNext() && (chainCount++ < maxChains) )
         {
        	 if (DEBUG)
        		 System.out.println("In inner loop " + chainCount);
            Chain c = cIt.next();
            pv.addChain(c);
            chainPageMap.put(c.getChainId(), pv);
         }
         
         if ( loopCounter > sortedChains.size()){
        	 System.err.println("SequenceCollectionView is running an endless loop. Forcing interruption. This horrible bug could bring the web site down otherwise.");
        	 break;
         }
         // we increase the counter only in the end, so we terminate only 
         loopCounter ++;
      }
      
      if (DEBUG) {
    	  System.out.println("SequenceCollectionView.initPages: " + maxChains + " " + sortedChains.size() + " " + currentPage + " pages: " + pages.size());
    	  System.out.println("SequenceCollectionView " + chainPageMap);
      }
      
      
      if(pages.size() == 0)
      {
         pages.add(new PageView(1, params));
      }
      else if( maxChains != 0 &&
    		  pages.size() != ((sortedChains.size() - 1) / maxChains)+1 )
      {    	  
    	  String message = "number of pages isn't what i thought it would be";
    	  if (DEBUG)
    		  System.out.println(message);
         throw new RuntimeException(message);
      }
   }
   
//   private void initSequenceImages()
//   {
//      for(PageView p : pages)
//      {
//         for(ChainView c : p.getChainViews())
//         {
//            c.resetSequenceImage();
//         }
//      }
//   }
   
   /**
    * Gets the number of pages, taking into account the number of chains and the number of chains set per page.
    * @return
    * @see ViewParameters#getChainsPerPage()
    * @see SequenceCollectionView#getChainCount()
    */
   public int getPageCount() {
      return ((sortedChains.size() - 1) / params.getChainsPerPage()) + 1;
   }
   
   /**
    * Gets the number of chains to be displayed. This <em>may not</em> be the same as calling {@link SequenceCollection#getChains()}<tt>.size()</tt>
    * as some chains may be hidden from view.
    * @return
    * @see ViewParameters#getChainViewStrategy()
    * @see ViewParameters#getChainEntityStrategy()
    */
   public int getChainCount() {
	   if (DEBUG)
		   System.out.println("SequenceCollectionView: getChainCount for: " + sequenceCollection.getStructureId() + " " + sortedChains);
      return sortedChains.size();
   }
   
   /**
    * Gets the structure id.
    */
   public String getStructureId()
   {
      return sequenceCollection.getStructureId();
   }
   
   /**
    * Iterates over the available {@link PageView}s
    * @return
    */
   public Iterator<PageView> pageViewIterator()
   {
      return pages.iterator();
   }
   
   /**
    * Gets the page view for a specific page number
    * @param pageNumber
    * @return
    */
   public PageView getPageView(int pageNumber)
   {
      return pages.get(pageNumber - 1); // ArrayLists count from zero, hence the -1
   }
   
   /**
    * Gets the page containing a particular chain
    * @param chainId the <em>INTERNAL mmCIF-derived</em> chain id.
    * @return
    */
   public PageView getPageView(String chainId)
   {
      return chainPageMap.get(chainId);
   }
   
   /**
    * Gets the {@link ChainView} for a particular chain
    * @param chainId the <em>INTERNAL mmCIF-derived</em> chain id.s
    * @return
    */
   public ChainView getChainView(String chainId)
   {
      PageView pv = getPageView(chainId);
      return pv == null ? null : pv.getChainView(chainId);
   }
   
   private static final Integer[] AVAILABLE_CHAINS_PER_PAGE_OPTIONS = new Integer[] {1,2,3,5,10};
   
   /**
    * Gets a map listing the chains-per-page options that will be presented to the user, along with a
    * <tt>String</tt> description.  The description is usually just the string-value of the number
    * itself except for the 'all chains' option.
    * @return
    */
   public Map<Integer, String> getChainsPerPageMap()
   {
      Map<Integer, String> result = new TreeMap<Integer, String>();
      final Integer numChains = sortedChains.size();
      
      for(Integer i : AVAILABLE_CHAINS_PER_PAGE_OPTIONS)
      {
         if(i == 1 || numChains-1 > i)
         {
            result.put(i, i.toString());
         }
         else
         {
            break;
         }
      }
      
      result.put(numChains, "all (" + numChains + ")");
      
      return result;
   }
   
   private final Map<Integer, SortedSet<Chain>> sortedChainEntityMap = new HashMap<Integer, SortedSet<Chain>>();
   
   /**
    * Gets a map of chains sorted according to {@link ViewParameters#getChainSortStrategy()} for a given entity
    * @param entityId
    * @return
    */
   public Collection<Chain> getSortedChains(Integer entityId)
   {
      SortedSet<Chain> result = sortedChainEntityMap.get(entityId);
      Comparator<Chain> comparator = params.getChainSortStrategy().comparator;
      
      if(result == null || !result.comparator().equals(comparator))
      {
         result = new TreeSet<Chain>(comparator);
         result.addAll(sequenceCollection.getChains(entityId));
         sortedChainEntityMap.put(entityId, result);
      }
      
      return result;
   }
   
   /**
    * Enumeration of the possible ways that the sequence-identical chains on the same entity can be viewed.  
    * The options are "display one chain per entity" (aka "display unique chains") or "display all chains per entity"
    */
   public static enum ChainEntityStrategy implements Serializable
   {
      first("Unique chains"),
      all("All chains");
      
      ChainEntityStrategy(final String displayName)
      {
         this.displayName = displayName;
      }
      public final String displayName;
   }
   
   /**
    * Gets a {@link ChainViewStrategy} that restricts to the given {@link PolymerType}
    * @param pt
    * @return
    */
   public ChainViewStrategy getChainViewStrategy(PolymerType pt)
   {
      if(pt == null) pt = PolymerType.unknown;
      switch(pt)
      {
      case peptide:
         return ChainViewStrategy.proteinOnly;
      case dna:
      case rna:
      case dnarna:
         return ChainViewStrategy.naOnly;
      case dpeptide:
      case lpolysaccharide:
      case polysaccharide:
      case otherPolymer:
      case unknown:
      default :
         return ChainViewStrategy.all;
      }
   }
   
   // Does the user want to restrict the chains visible in some other way?
   /**
    * Enumeration of available restrictions to the {@link Sequence}s to be displayed.  Current strategies 
    * are related to limiting displayed chains to particular {@link PolymerType}s.
    */
   public static enum ChainViewStrategy implements Serializable
   {
      all("All chain types", PolymerType.ALL_POLYMER_TYPES),
      proteinOnly("Polypeptide chains only", PolymerType.PROTEIN_ONLY),
      naOnly("Polynucleotide chains only", PolymerType.POLYNUCLEOTIDE_ONLY);
      
      ChainViewStrategy(final String displayName, final Set<PolymerType> allowablePolymerTypes)
      {
         this.displayName = displayName;
         this.allowablePolymerTypes = allowablePolymerTypes;
         this.predicate = new Predicate() {

            public boolean evaluate(Object arg0) {
               boolean satisfies = false;
               
               if(arg0 instanceof Sequence)
               {
                  satisfies = allowablePolymerTypes == PolymerType.ALL_POLYMER_TYPES || allowablePolymerTypes.contains(((Sequence)arg0).getPolymerType());
               }
               
               return satisfies;
            }
         };
      }
      /**
       * The description of the <tt>ChainViewStrategy</tt> to be presented to users
       */
      public final String displayName;
      
      /**
       * The {@link Predicate} used to restrict the <tt>Collection</tt> of <tt>Sequence</tt>s.
       */
      public final Predicate predicate;
      
      /**
       * A set of allowable {@link PolymerType}s for this <tt>ChainViewStrategy</tt>
       */
      public final Set<PolymerType> allowablePolymerTypes;
   }
   


   
   private static final int BEFORE = -1;
   private static final int EQUAL  =  0;
   private static final int AFTER  =  1;
   
   private static final Comparator<Chain> BY_PDB_CHAIN_ID = new Comparator<Chain>() 
   {
      public int compare(Chain arg0, Chain arg1) {
         int comparison;
         if(arg0 == arg1) return EQUAL;
         
         if((comparison = arg0.getPdbChainId().compareTo(arg1.getPdbChainId())) != EQUAL) return comparison;
         
         return EQUAL;
      }
   };
   
   private static final Comparator<Chain> BY_TYPE_AND_PDB_CHAIN_ID = new Comparator<Chain>() {
      public int compare(Chain arg0, Chain arg1) {
         int comparison;
         if(arg0 == arg1) return EQUAL;
         
         if((comparison = arg0.getPolymerType().compareTo(arg1.getPolymerType())) != EQUAL) return comparison;
         if((comparison = arg0.getPdbChainId() .compareTo(arg1.getPdbChainId() )) != EQUAL) return comparison;
         
         return EQUAL;
      }
   };
   
   private static final Comparator<Chain> BY_CHAIN_LENGTH = new Comparator<Chain>() {
      public int compare(Chain arg0, Chain arg1) {
         int comparison;
         if(arg0 == arg1) return EQUAL;
         
         // we want DESCENDING order.
         if (arg0.getSequenceLength() > arg1.getSequenceLength()) return BEFORE;
         if (arg0.getSequenceLength() < arg1.getSequenceLength()) return AFTER;
         
         // include chain id lest there are lots of chains with the same length
         if((comparison = arg0.getPdbChainId().compareTo(arg1.getPdbChainId())) != EQUAL) return comparison;
         
         return EQUAL;
      }
   };
   
   /**
    * Enumeration of possible sorting strategies for the {@link Chain}s to be displayed.
    */
   public static enum ChainSortStrategy implements Serializable
   {
      pdbChainId( 
         "Sort by chain id",
         BY_PDB_CHAIN_ID
      ),
      chainTypeThenPdbChainId( 
         "Sort by polymer type then chain id",
         BY_TYPE_AND_PDB_CHAIN_ID
      ),
      chainLength( 
         "Sort by sequence length",
         BY_CHAIN_LENGTH
      );
      
      ChainSortStrategy(final String displayName, final Comparator<Chain> comparator)
      {
         this.displayName = displayName;
         this.comparator = comparator;
      }
      /**
       * {@link Comparator} to rank the {@link Chain}s.
       */
      public final Comparator<Chain> comparator;
      
      /**
       * The description of the <tt>ChainSortStrategy</tt> to be presented to users
       */
      public final String displayName;
   }

   /**
    * Gets the {@link ViewParameters} object that configures this <tt>SequenceCollectionView</tt>
    * @return
    */
   public ViewParameters getParams() {
      return params;
   }

   /**
    * Sets a {@link ViewParameters} object to configure this <tt>SequenceCollectionView</tt>
    * @return
    */
   public void setParams(ViewParameters newParams) {
      newParams = cloneParams(newParams);
     
      if(!this.params.equals(newParams))
      {
         if(shouldReInitChains(newParams)) {            
            initChains();
           
         }
         initPages();
      }
   }
   
   private static ViewParameters cloneParams(ViewParameters newParams)
   {
      ViewParameters result = null;
      try {
         result = (ViewParameters) newParams.clone();
      } catch (CloneNotSupportedException e) {
         System.err.println("Couldn't clone ViewParameters; creating a default one :("  + e.getMessage());
         e.printStackTrace();
         result = new ViewParameters();
      }
      return result;
   }
}
