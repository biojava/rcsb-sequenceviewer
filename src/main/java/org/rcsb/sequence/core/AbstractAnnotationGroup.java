package org.rcsb.sequence.core;

import static org.rcsb.sequence.model.AnnotationStatus.instantiated;
import static org.rcsb.sequence.model.AnnotationStatus.noData;
import static org.rcsb.sequence.model.AnnotationStatus.populated;
import static org.rcsb.sequence.model.AnnotationStatus.underConstruction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.bag.HashBag;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationStatus;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.ResidueUtils;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceSegment;

public abstract class AbstractAnnotationGroup<T> 
extends AnnotationInformationImpl implements AnnotationGroup<T>, Serializable {

   private static final long serialVersionUID = 1L;
   protected final TreeSet<Annotation<T>> annotations;
   private final Bag annotatedResidues;
   protected AnnotationStatus status = instantiated;
   protected  Sequence chain;
   
   protected transient Map<AnnotationValue<T>, Integer> residuesPerAnnotationValue = null;
   protected transient Map<AnnotationValue<T>, Integer> annotationValueCount = null;

   /**
    * <p>
    * Should contain <b>only</b> inexpensive operations.
    * </p><p>
    * Put expensive db access, parsing, etc into <tt>constructAnnotations()</tt>
    * </p>
    * @param classification
    * @param name
    * @param rns
    * @param sequence
    */
   @SuppressWarnings("unchecked")
   public AbstractAnnotationGroup(AnnotationClassification classification, AnnotationName name, ResidueNumberScheme rns, Sequence chain) {
      super(classification, name, rns);
      this.chain = chain;
      this.annotatedResidues = new HashBag();
   
      // if this sequence is a fragment, we should get annotation information from the AnnotationGroup
      // object of the parent sequence
      if(chain instanceof SequenceSegment)
      {
         SequenceSegment sequenceFragment = (SequenceSegment)chain;
         AnnotationGroup<T> parent = (AnnotationGroup<T>) sequenceFragment.getFullSequence().getAnnotationGroup(name.getAnnotationClass());
         this.annotations = new TreeSet<Annotation<T>>(parent.getAnnotations().comparator());
   
         Annotation<T> toAdd;
         ResidueId lowerBound = sequenceFragment.getFirstResidue();
         ResidueId upperBound = sequenceFragment.getLastResidue();
   
         for(Annotation<T> a : parent.getAnnotations())
         {
            if((toAdd = checkAnnotation(a, lowerBound, upperBound, sequenceFragment)) != null)
            {
               annotations.add(toAdd);
               annotatedResidues.addAll(toAdd.getSequence().getResidueIds());
            }
         }
         status = parent.getStatus();
      }
      else
      {
         // annotations is a tree set ordered by the first residue of the annotation
         this.annotations = new TreeSet<Annotation<T>>(SORT_ANNOTATIONS_COMPARATOR);
      }
   }

   public void destroy(){
      chain = null;
      annotatedResidues.clear();
      annotations.clear();
      status = AnnotationStatus.destroyed;
      
      residuesPerAnnotationValue = null;
      annotationValueCount = null;
      
   }

   protected abstract void constructAnnotationsImpl() throws Exception;
   
   /** returns null if something went wrong...
    *
    * @param <T>
    * @param a
    * @param lowerBound
    * @param upperBound
    * @param sequenceFragment
    * @return
    */
   private static <T> Annotation<T> checkAnnotation(Annotation<T> a, ResidueId lowerBound, ResidueId upperBound, SequenceSegment sequenceFragment)
   {
      Annotation<T> result;
      ResidueNumberScheme rns = sequenceFragment.getDefaultResidueNumberScheme();
      int maxLength = sequenceFragment.getMaxLength();
      ResidueId annotationStart, annotationEnd;
      boolean startBeforeChain, startAfterChain, endBeforeChain, endAfterChain;

      annotationStart = a.getSequence().getFirstResidue(rns);
      annotationEnd   = a.getSequence().getLastResidue(rns);

      if(annotationStart == null)
      {
         // there is no equivalent residue for the start pos of this annotation in the given rns

         // get the next residue with the correct id type
         annotationStart = a.getSequence().getFirstResidue(a.getResidueNumberScheme()).getNextEquivalentResidueId(rns);
      }
      if(annotationEnd == null)
      {
         annotationEnd = a.getSequence().getLastResidue(a.getResidueNumberScheme()).getPreviousEquivalentResidueId(rns);
      }

      // the following may occur if the preceeding call to getFirst..().getNext..() returns a residue that is after the end
      // or visa versa.

      // if that happens, it means that the annotation is after or before the section of the sequence with alignment
      // between teh sequence rns and the annotation rns. we should disregard this annotation for this sequence

      // (structural data is confusing)
      if(annotationEnd.isBefore(annotationStart))
      {
         return null;
      }

      startBeforeChain = annotationStart.isBefore(lowerBound);
      startAfterChain  = annotationStart.isAfter (upperBound);
      endBeforeChain   = annotationEnd  .isBefore(lowerBound);
      endAfterChain    = annotationEnd  .isAfter (upperBound);

      if( (startBeforeChain || endAfterChain) && !startAfterChain && !endBeforeChain)
      {
         try {
            result = new BoundedAnnotation<T>(a, lowerBound, upperBound, maxLength);
         } catch (RuntimeException e){
            System.err.println(e.getMessage() + " " + a + " " + lowerBound + " " + upperBound + " " + maxLength);
            return null;

         }
      }
      else if( !(startBeforeChain || endAfterChain || startAfterChain || startBeforeChain) )
      {
         result = a;
      }
      else
      {
         result = null;
      }
      return result;
   }

   /** Add an annotation to the group.
    * returns false if a problem  has been found.
    *
    * @param value
    * @param start
    * @param end
    * @return
    */
   @SuppressWarnings("unchecked")

   protected boolean addAnnotation(AnnotationValue<T> value, ResidueId start, ResidueId end)
   {
      try {
         start.ensureBeforeOrEqual(end);
      } catch (Exception e){
         // argh another RuntimeException thrown just because of state info...
         //System.out.println(e.getMessage());
         System.err.println("AbstractAnnotationGroup: can't add Annotation >" +value + "< because can't ensure that start is before end or equal (" +start+ " , " + end + " )" );
         return false;
      }
      Collection<ResidueId> resForThisAn = ResidueUtils.getResidueIdsBetween(start, end);

      if(!annotationsMayOverlap() && CollectionUtils.containsAny(resForThisAn, annotatedResidues))
      {

            String nameS = ">name is not defined<";
            if ( name != null) {
               nameS = name.getName();
            }

            String annoV = ">could not get AnnotationValue<";
            try {
               annoV = getAnnotation(start).getAnnotationValue().toString();
            } catch (Exception e){
               System.out.println(e.getMessage());
            }

    	  //System.out.println("AbstractAnnotationGroup: " + chain + " with " +  nameS+ " Residues may not be annotated more than once by this annotation group. You want to assign " + value + " but it's already assigned " + annoV);
    	  return false;
      }

      annotatedResidues.addAll(resForThisAn);
      return this.annotations.add(new AnnotationImpl<T>( this.classification, this.name, this.residueNumberScheme, value, start, end));
   }

   protected boolean addAnnotation(AnnotationValue<T> value, ResidueId theResidueId)
   {
      return addAnnotation(value, theResidueId, theResidueId);
   }

   protected boolean addAnnotation(AnnotationValue<T> value, String startId, String endId)
   {
	   
	   
	   
      ResidueId start = chain.getResidueId(residueNumberScheme, startId);
      ResidueId end   = chain.getResidueId(residueNumberScheme, endId);
      if ( start == null || end == null ){
    	  System.out.println("AbstractAnnotationGroup: not adding annotation, since one of the residues is null: " + value + " " + startId + " " + endId);
    	  return false;
      }
      return addAnnotation(value, start, end);
   }

   protected boolean addAnnotation(AnnotationValue<T> value, String theResidueId)
   {
      return addAnnotation(value, theResidueId, theResidueId);
   }

   protected boolean tentativelyAddAnnotation(AnnotationValue<T> value, ResidueId start, ResidueId end)
   {
      try
      {
         return addAnnotation(value, start, end);
      }
      catch(RuntimeException e)
      {
         System.err.println("AbstractAnnotationGroup: Did not add annotation " + value + " covering residues between " + start + " and " + end);
         return false;
      }
   }

   protected boolean tentativelyAddAnnotation(AnnotationValue<T> value, ResidueId theResidueId)
   {
      return tentativelyAddAnnotation(value, theResidueId, theResidueId);
   }

   protected boolean tentativelyAddAnnotation(AnnotationValue<T> value, String startId, String endId)
   {
      ResidueId start = chain.getResidueId(residueNumberScheme, startId);
      ResidueId end   = chain.getResidueId(residueNumberScheme, endId);
      return tentativelyAddAnnotation(value, start, end);
   }

   protected boolean tentativelyAddAnnotation(AnnotationValue<T> value, String theResidueId)
   {
      return tentativelyAddAnnotation(value, theResidueId, theResidueId);
   }

	

	public Sequence getChain() {
		return chain;
	}

   public Sequence getSequence() {
      return chain;
   }

   public String getValueString(ResidueNumberScheme rnsRequested) {
      StringBuilder result = new StringBuilder();
      ResidueNumberScheme rnsOfAnnotation = getResidueNumberScheme();
      if(getAnnotations().size() == 0)
      {
         final int len = getSequence().getSequenceLength(rnsRequested);
         for(int i = 0; i < len; i++) result.append(' ');
         return result.toString();
      }


      Collection<ResidueId> ridsOfRequestedRns = getSequence().getResidueIds(rnsRequested);
      Collection<ResidueId> idsMissingEquivalents = ResidueUtils.getResidueIdsWithMissingEquivalent(ridsOfRequestedRns, rnsOfAnnotation);

      for(ResidueId r : ridsOfRequestedRns)
      {
         if(idsMissingEquivalents.contains(r) || !annotatesResidue(r.getEquivalentResidueId(rnsOfAnnotation)))
         {
            result.append(' ');
         }
         else
         {
            result.append(getAnnotation(r).getAnnotationValue().toCharacter());
         }
      }

      return result.toString();
   }

   public String getValueString() {
      return getValueString(getResidueNumberScheme());
   }

   /**
     * Construct the <tt>Annotation</tt>s for this <tt>AnnotationGroup</tt>
     */
    public void constructAnnotations() throws Exception
    {
    	//System.err.println("ABSTRACT ANNOTATIONGROUP: CONSTRUCTING ANNOTATIONS " + getName().getName() + " status:" +status);
       if(status == underConstruction)
       {
    	   System.out.println("contructAnnotations() has been called circularly!");
    	   status = noData;
    	   return;
       }
       if(status == populated || status == noData)
       {
          System.err.println("constructAnnotations() called needlessly");
          return;
       }
       
       if ( status != instantiated) {
    	   String msg = "AnstractAnnotationGroup: status != instantiated !";
    	   System.err.println( msg);
    	   throw new RuntimeException(msg);
       }
    	   //assert status == instantiated;

       status = underConstruction;
       try
       {
          constructAnnotationsImpl();
          status = annotations.size() > 0 ? populated : noData;
       }
       catch(Exception e)
       {
          System.err.println("Could not create " + name + " annotation for " + getSequence().getStructureId() + ":" + getSequence().getChainId() + " " +  e.getMessage());
          e.printStackTrace();
          status = noData;
       }
    }

    public SortedSet<Annotation<T>> getAnnotations() {
      if(status == underConstruction)
      {
         throw new RuntimeException("Multithreading not yet supported");
      }
      if(status == instantiated)
      {
         try {
            constructAnnotations();
         } catch (Exception e) {
            System.err.println("AnnotationGroup " + this.getName() + " failed when trying to assemble its annotations " +  e.getMessage());
            e.printStackTrace();
            status = noData;
         }
      }
		return Collections.unmodifiableSortedSet(annotations);
	}

   public AnnotationStatus getStatus()
   {
      return status;
   }

   public boolean hasData()
   {
      return status == populated;
   }

   public int getAnnotationCount()
   {
      return getAnnotations().size();
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

        retValue.append("AnnotationGroup ( ")
            .append(super.toString()).append(TAB)
            .append("status = ").append(this.status).append(TAB)
            .append("chain = ").append(this.chain).append(TAB)
            .append("annotations = ").append(this.annotations).append(TAB)
            .append(" )");

        return retValue.toString();
    }

   public Map<AnnotationValue<T>, Integer> getResiduesPerAnnotationValue() {
      if(residuesPerAnnotationValue == null)
      {
         initCounts();
      }
      return residuesPerAnnotationValue;
   }

   public Map<AnnotationValue<T>, Integer> getAnnotationValueCount() {
      if(annotationValueCount == null)
      {
         initCounts();
      }
      return annotationValueCount;
   }

   @SuppressWarnings("unchecked")
   private void initCounts()
   {
      Bag rpavBag = new HashBag();
      Bag avcBag = new HashBag();

      // first put it in a bag
      for(Annotation<T> a : getAnnotations())
      {
         rpavBag.add(a.getAnnotationValue(), a.getSequence().getSequenceLength());
         avcBag.add(a.getAnnotationValue());
      }

      // then create a treemap from the bag
      residuesPerAnnotationValue = Collections.synchronizedMap(new TreeMap<AnnotationValue<T>, Integer>());
      annotationValueCount = Collections.synchronizedMap(new TreeMap<AnnotationValue<T>, Integer>());
      AnnotationValue<T> av;
      for( Object o : rpavBag.uniqueSet() )
      {
         av = (AnnotationValue<T>) o;
         residuesPerAnnotationValue.put(av, rpavBag.getCount(av));
         annotationValueCount.put(av, avcBag.getCount(av));
      }
   }

   public boolean annotatesResidue(ResidueId r) {
      return annotatedResidues.contains(r);
   }

   public boolean annotationsMayOverlap()
   {
      return false;
   }

   public boolean annotationsDoOverlap() {
      return getMaxAnnotationsPerResidue() > 1;
   }

   public int getAnnotationCount(ResidueId residueId) {
      return annotatedResidues.getCount(residueId);
   }

   public Annotation<T> getAnnotation(ResidueId residueId) {
      List<Annotation<T>> result = getAnnotations(residueId, true);
      return result.size() == 0 ? null : result.get(0);
   }

   private List<Annotation<T>> getAnnotations(ResidueId residueId, boolean onlyTheFirst)
   {
      List<Annotation<T>> result = null;
     if(annotations == null || annotations.size() == 0)
     {
        return null;
     }

     for(Annotation<T> a : annotations)
     {
        if(a.annotatesResidue(residueId))
        {
           if(onlyTheFirst) return Collections.singletonList(a);
           else if(result == null) result = new ArrayList<Annotation<T>>();
           result.add(a);
        }
     }

     if(result == null) return Collections.emptyList();

     return result;
  }

   public Collection<Annotation<T>> getAnnotations(ResidueId residueId) {
      return getAnnotations(residueId, !annotationsMayOverlap());
   }

   public Bag getAnnotationsPerResidueBag() {
      return annotatedResidues;
   }

   public int getMaxAnnotationsPerResidue() {
      return getMaxCount(annotatedResidues);
   }

   public static int getMaxCount(Bag b)
   {
      if(b == null) return 0;
      return getMaxCount(b, b.uniqueSet());
   }

   @SuppressWarnings("unchecked")
   private static int getMaxCount(Bag b, Collection<ResidueId> candidates)
   {
	   
      int result = 1, contender;
      if(b == null) return result;

      for(Object o : candidates)
      {
         contender = b.getCount(o);
         if(contender > result)
         {
            result = contender;
         }
      }
      
      return result;
   }

   public static SortAnnotationsComparator SORT_ANNOTATIONS_COMPARATOR = new SortAnnotationsComparator();
   private static class SortAnnotationsComparator implements Comparator<Annotation<?>>, Serializable
   {
      private static final long serialVersionUID = 1L;
      private static final int EQUAL = 0;
      public int compare(Annotation<?> o1, Annotation<?> o2)
      {
         int result;

         result = o1.getSequence().getFirstResidue().compareTo(o2.getSequence().getFirstResidue());
         if(result != EQUAL) return result;

         result = o1.getSequence().getLastResidue().compareTo(o2.getSequence().getLastResidue());
         if(result != EQUAL) return result;

         AnnotationValue av1 = o1.getAnnotationValue();
         AnnotationValue av2 = o2.getAnnotationValue();
         if (av1 instanceof Comparable && av2 instanceof Comparable) {
            return ((Comparable)av1).compareTo((Comparable)av2);
         } else {
            return av1.toString().compareTo(av2.toString());
         }
      }
   }
}