package org.rcsb.sequence.core;

import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.ResidueUtils;
import org.rcsb.sequence.model.SegmentedSequence;
import org.rcsb.sequence.model.Sequence;



public abstract class AbstractSequence implements Sequence, Serializable
{
	private static final long serialVersionUID = 1L;

	private Map<Class<AnnotationGroup<?>>, AnnotationGroup<?>> annotationGroupMap = new LinkedHashMap<Class<AnnotationGroup<?>>, AnnotationGroup<?>>();

	private String sequence;

	// this is a store for generated sequences for a given residue number scheme
	private transient Map<ResidueNumberScheme, String> sequences = null;

	private final Map<ResidueNumberScheme, Map<String, ResidueId>> residueIdMaps = ResidueNumberScheme.initEmptyResidueIdMap();

   
	protected AbstractSequence(String sequence)
	{
		this.sequence = sequence;
		//System.out.println("creating new AbstractSequence: " + sequence);
	}
	
	protected abstract void ensureResiduesInstantiated();

   /** clean up all internal data structures
	 *  
	 */
	public void destroy(){
	   
		
		//PDBWW-1753
	   
	   Collection<AnnotationGroup<?>> annotations = getAvailableAnnotationGroups();
	   for (AnnotationGroup<?> group: annotations){
	      group.destroy();
	   }
	   
	   annotationGroupMap.clear();
	   if ( sequences != null)
	      sequences.clear();
	   residueIdMaps.clear();
	   
	   
	}

	@SuppressWarnings("unchecked")
	public void addAnnotationGroup(AnnotationGroup<?> ag)
	{
		
		getAnnotationGroupMap().put((Class<AnnotationGroup<?>>) ag.getClass(), ag);
	}

	public <T extends AnnotationGroup<?>> boolean containsAnnotationGroup(Class<T> an)
	{
		return getAnnotationGroupMap().containsKey(an);
	}

	protected Map<Class<AnnotationGroup<?>>, AnnotationGroup<?>> getAnnotationGroupMap() {
		return annotationGroupMap;
	}

	public Collection<AnnotationGroup<?>> getAvailableAnnotationGroups() {
		ensureAnnotated();
		System.out.println("AbstractSequence: getAvailableAnnotationGroups " + getAnnotationGroupMap().size());
		
		return Collections.unmodifiableCollection(getAnnotationGroupMap().values());
	}

	@SuppressWarnings("unchecked")
	public <T extends AnnotationGroup<?>> T getAnnotationGroup(Class<T> object) {
		ensureAnnotated();
		return (T) getAnnotationGroupMap().get(object);
	}
	
	public DisulfideAnnotationGroup getDisulfideAnnotationGroup(){
		ensureAnnotated();
		
		for (Class ag : annotationGroupMap.keySet()) {
			Class[] interfaces = ag.getInterfaces();
			for ( Class interf : interfaces){
				if ( interf.equals(DisulfideAnnotationGroup.class)) {
					return (DisulfideAnnotationGroup) getAnnotationGroupMap().get(ag);
				}
			}
		}
		return null;
		
		
	}


	@SuppressWarnings("unchecked")
	public Collection<AnnotationGroup<?>> getAnnotationGroupsWithData() {
		
		Collection<AnnotationGroup<?>> annos = CollectionUtils.select(getAvailableAnnotationGroups(),
				new Predicate() {
			public boolean evaluate(Object arg0) {
				return arg0 instanceof AnnotationGroup && ((AnnotationGroup)arg0).hasData();
			}
		});
		
		
		for (AnnotationGroup<?> anno: annos){
			System.out.println("AbstractSequence: annotationgroup has data:" + anno.getName().getName());
		}
		return annos;
	}

	@SuppressWarnings("unchecked")
	public Collection<AnnotationGroup<?>> getAnnotationGroupsWithData(final AnnotationClassification ac) {
		return CollectionUtils.select(getAvailableAnnotationGroups(),
				new Predicate() {
			public boolean evaluate(Object arg0) {
				return arg0 instanceof AnnotationGroup && ((AnnotationGroup)arg0).getClassification() == ac && ((AnnotationGroup)arg0).hasData();
			}
		});
	}

	public AnnotationGroup<?> getFirstAnnotationGroupWithData(final AnnotationClassification ac)
	{
		Iterator<AnnotationGroup<?>> agIt = getAnnotationGroupsWithData(ac).iterator();
		return agIt.hasNext() ? agIt.next() : null;
	}

	public Collection<ResidueId> getResidueIds()
	{
		return getResidueIds(getDefaultResidueNumberScheme());
	}

	public Collection<ResidueId> getResidueIds(ResidueNumberScheme rns)
	{
		if(rns == ResidueNumberScheme.DBREF)
		{
			ensureAnnotated();
		}
		else
		{
			ensureResiduesInstantiated();
		}
		return Collections.unmodifiableCollection(getResidueIdMaps().get(rns).values());
	}

	@SuppressWarnings("unchecked")
	public Collection<ResidueId> getResidueIdsBetween(ResidueId start, ResidueId end)
	{
		ensureResiduesInstantiated();
		ResidueUtils.ensureResiduesComparable(start, end);
		return Collections.unmodifiableCollection(CollectionUtils.select(getResidueIdMaps().get(start.getResidueNumberScheme()).values(), new ResidueIdBetweenPredicate(start, end)));
	}

	public Collection<ResidueId> getResidueIdsBetween(ResidueNumberScheme rns, Integer startId, Integer endId)
	{
		ResidueId start = getResidueId(rns, startId);
		ResidueId end   = getResidueId(rns, endId);
		if ( start == null || end == null){
			System.out.println("Could not getResiduesIdsBetween for " + start + " - " + end);
		}
		return getResidueIdsBetween(start, end);
	}

	public ResidueId getResidueId(ResidueNumberScheme rns, String idAsString)
	{
		ResidueId result = null;
		Map<String, ResidueId> intermediate;
		if( (intermediate = getResidueIdMaps().get(rns)) != null)
		{
			result = intermediate.get(idAsString);
		}
		if ( result == null){
			System.out.println("AbstractSeqeunce: could not getResidueId: " + idAsString + " probably not found on residueIdMap.");
		}
		return result;
	}

	public ResidueId getFirstResidue(ResidueNumberScheme rns)
	{

		ensureResiduesInstantiated();
		Iterator<ResidueId> it = getResidueIdMaps().get(rns).values().iterator();

		return it.hasNext() ? it.next() : null;
	}

	public ResidueId getLastResidue(ResidueNumberScheme rns)
	{
		ensureResiduesInstantiated();
		// TODO: Make this less sucky
		ResidueId result = null;
		Map<ResidueNumberScheme, Map<String, ResidueId>> ridm = getResidueIdMaps();
		if ( ridm == null)
			return null;
		Map<String,ResidueId> rmap =  ridm.get(rns);
		if (rmap == null)
			return null;

		Iterator<ResidueId> it = rmap.values().iterator();
		while(it.hasNext())
			result = it.next();
		return result;
	}

	/**
	 * Returns <tt>true</tt> if this collection contains the specified element.
	 *
	 * @param element
	 *           whose presence in this collection is to be tested.
	 * @see java.util.Collection#contains(Object)
	 * @uml.property name="availableResidueNumberSchemes"
	 */
	public boolean hasResiduesIndexedBy(
			ResidueNumberScheme residueNumberScheme) {
		ensureAnnotated();
		return this.residueIdMaps.keySet().contains(residueNumberScheme) && this.residueIdMaps.get(residueNumberScheme).size() > 0;
	}

	public boolean hasDbRefMapping()
	{
		return hasResiduesIndexedBy(ResidueNumberScheme.DBREF);
	}

	public ResidueId getResidueId(ResidueNumberScheme rns, Integer id)
	{
		ensureResiduesInstantiated();
		ResidueId result = null;
		if(rns == null)
		{
			// do nothing
			System.out.println("AbstractSequence: requested residue " + id + " but provided ResidueNumberScheme null.");
		}
		else // if(id != null)
		{
			result = getResidueId(rns, id.toString());
		}
		return result;
	}

	public ResidueId getResidueId(ResidueNumberScheme rns, Integer id, Character insertionCode)
	{
		if(id != null && insertionCode != null && rns != null && rns.hasInsertionCodes())
		{
			return getResidueId(rns, String.valueOf(id) + insertionCode);
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public Collection<ResidueId> getIdsForResidue(ResidueInfo r, ResidueNumberScheme rns)
	{
		ensureAnnotated();
		Collection<ResidueId> result = CollectionUtils.select(this.residueIdMaps.get(rns).values(), new ResiduePredicate(r));
		return result;
	}

	public ResidueId getFirstResidue()
	{
		return getFirstResidue(this.getDefaultResidueNumberScheme());
	}
	public ResidueId getLastResidue()
	{
		return getLastResidue(this.getDefaultResidueNumberScheme());
	}

	public boolean containsResidue(ResidueId theResidueId)
	{
		Collection<ResidueId> rids;
		return !(theResidueId == null
				|| theResidueId.isBeginningOfChainMarker()
				|| theResidueId.isEndOfChainMarker()
				|| (rids = getResidueIds(theResidueId.getResidueNumberScheme())) == null
				|| !rids.contains(theResidueId));
	}

	/**
	 * Get a map with keys that are the residues from the sequence in one ResidueNumberScheme that precede gaps
	 * in that sequence when compared to the sequence of another ResidueNumberScheme. The returned
	 * map may contain the marker BEGINNING_OF_CHAIN if the sequence of
	 * the comparison ResidueNumberScheme starts before that of the ResidueNumberScheme of the sequence. In all cases
	 * the value is the size of the gap in residues.
	 * @param seqRns the ResidueNumberScheme being considered
	 * @param gapsRns the compared ResidueNumberScheme
	 * @return
	 */
	public Map<ResidueId, Integer> getNonContiguousResidueIds(ResidueNumberScheme seqRns, ResidueNumberScheme gapsRns)
	{
		Map<ResidueId, Integer> result;
		if(!hasResiduesIndexedBy(seqRns) || (!hasResiduesIndexedBy(gapsRns)))
		{
			System.err.println(this + " has no residues indexed by " + seqRns + " or " + gapsRns);
			result = Collections.emptyMap();
		}
		else
		{
			result = new LinkedHashMap<ResidueId, Integer>();
			   ResidueId first = getFirstResidue(seqRns);

			// warning this can be null if no prev seq found!
			if (first == null)
				return Collections.emptyMap();

			ResidueId prevRidSeqRns =first.getPrevious(),
			ridOtherRns,
			prevRidOtherRns = getFirstResidue(gapsRns).getPrevious();

			int comparison;

			for( ResidueId ridSeqRns : getResidueIds(seqRns))
			{
				assert ridSeqRns.getPrevious() == prevRidSeqRns;

				ridOtherRns = ridSeqRns.getEquivalentResidueId(gapsRns);

				// if there is no residue id in the gaps rns, then clearly there can't be a gap
				if(ridOtherRns == null)
				{
					continue;
				}

				// now compare this residue and prevResidue in gapsRns
				comparison = ridOtherRns.getPrevious().compareTo(prevRidOtherRns);

				// if they are the same...
				if(comparison == 0)
				{
					// we are all good; do nothing.
				}
				// if we have skipped ahead, there must be a gap
				else if(comparison > 0)
				{
					// how big is that gap?
					int gapSize = 0;
					if(!prevRidOtherRns.isBeginningOfChainMarker())
					{
						while( (prevRidOtherRns = prevRidOtherRns.getNext()) != ridOtherRns )
						{
							++gapSize;
						}
					}
					else if(!ridOtherRns.isEndOfChainMarker())
					{
						ResidueId aRid = ridOtherRns;
						while ((aRid = aRid.getPrevious()) != prevRidOtherRns)
						{
							++gapSize;
						}
					}
					else
					{
						throw new RuntimeException("The number of residues between the beginning and end chain markers is undetermined");
					}
					result.put(prevRidSeqRns, gapSize);
				}
				else
				{
					throw new RuntimeException("Residues may be out of order!");
				}

				prevRidSeqRns = ridSeqRns;
				prevRidOtherRns = ridOtherRns;
			}
		}
		return result;
	}

	protected static String getSequenceString(Collection<ResidueId> residues)
	{
		if(residues == null)
		{
			throw new NullPointerException("null passed to getSequenceString");
		}
		StringBuilder result = new StringBuilder(residues.size());
		for( ResidueId rid : residues )
		{
			result.append(rid.getResidueInfo().getOneLetterCode());
		}
		//System.out.println("AbstractSequence getSequenceString " + result.toString() + " size: " + residues.size());
		return result.toString();
	}


	/**
	 * Create an ordered, doubly-linked set of ResidueIds from a String sequence
	 * @param sequenceString
	 * @param polymerType
	 * @param rns
	 * @param c
	 * @return
	 */
	protected static Set<ResidueId> sequenceStringToResidueIds(String sequenceString, PolymerType polymerType, ResidueNumberScheme rns, Chain c, int indexStart)
	{
		if(sequenceString == null || rns == null) throw new NullPointerException();
		Set<ResidueId> result = new LinkedHashSet<ResidueId>();
		char[] chars = sequenceString.toCharArray();
		char res;
		String monId = "";
		int indexOfOpenBracket = -1;
		int dif;
		int resCount = indexStart;
		boolean bracketIsOpen = false;
		ResidueIdImpl prev = (ResidueIdImpl) ResidueIdImpl.BEGINNING_OF_CHAIN, resId;

		// for each character
		for(int i = 0; i < chars.length; i++)
		{
			// if it's an open bracket then we have a full mon_id coming up
			if((res = chars[i]) == '(')
			{
				bracketIsOpen = true;
				indexOfOpenBracket = i;
			}
			// or, if a bracket has been opened but not closed
			else if(bracketIsOpen)
			{
				// so long as there have been fewer than three characters between the opening of the bracket and now
				if((dif = i - indexOfOpenBracket - 1) < 3)
				{
					// if it's not a close bracket, add to the mon_id
					if(res != ')')
					{
						monId += res;
					}
					// otherwise we have a full mon_id and we should add it to the result set
					else
					{
						bracketIsOpen = false;
						resId = createResidueId(rns, c, resCount++, monId, prev);
						result.add(resId);
						prev = resId;
						monId = "";
					}
				}
				// if there have been three already, then this had better be a close bracket
				else if(dif == 3 && res == ')')
				{
					bracketIsOpen = false;
					resId = createResidueId(rns, c, resCount++, monId, prev);
					result.add(resId);
					prev = resId;
					monId = "";
				}
				// otherwise something is amiss
				else
				{
					assert dif > 3 || (dif == 3 && res != '(');
					throw new RuntimeException("Ligand name > 3 characters found");
				}
			}
			// otherwise this is just a standard residue
			else
			{
				resId = createResidueId(rns, c, resCount++, res, polymerType, prev);
				result.add(resId);
				prev = resId;
			}
		}
		return result;
	}
	
	private static ResidueIdImpl createResidueId(ResidueNumberScheme rns, Chain c, int seqId, String monId, ResidueIdImpl prev)
	{
		return createResidueId(rns, c, seqId, ResidueProvider.getResidue(monId), prev);
	}

	private static ResidueIdImpl createResidueId(ResidueNumberScheme rns, Chain c, int seqId, Character oneLettercode, PolymerType pt, ResidueIdImpl prev)
	{
		return createResidueId(rns, c, seqId, ResidueProvider.getResidue(pt, oneLettercode), prev);
	}

	
	private static ResidueIdImpl createResidueId(ResidueNumberScheme rns, Chain c, int seqId, ResidueInfo rinfo, ResidueIdImpl prev)
	{
		ResidueIdImpl resId = new ResidueIdImpl(rns, c, seqId, rinfo);
		resId.setPrevious(prev);
		prev.setNext(resId);
		return resId;
	}

	
	private class ResidueIdBetweenPredicate implements Predicate, Serializable
	{
		private static final long serialVersionUID = 1L;
		private final ResidueId lowerBound;
		private final ResidueId upperBound;

		protected ResidueIdBetweenPredicate(ResidueId lowerBound, ResidueId upperBound)
		{
			ResidueUtils.ensureResiduesComparable(lowerBound, upperBound);
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		public boolean evaluate(Object arg0) {
			boolean result = false;
			ResidueId ri;
			if(arg0 instanceof ResidueId)
			{
				ri = (ResidueId)arg0;
				try {
				   ResidueUtils.ensureResiduesComparable(lowerBound, ri);
				} catch (RuntimeException e){
				   System.err.println(e.getMessage());
				   return false;
				}
				result = lowerBound.compareTo(ri) <= 0 && upperBound.compareTo(ri) >= 0;
			}

			return result;
		}
	}

	public String getSequenceString(ResidueNumberScheme rns)
	{
		String theResult;
		String sequenceString = getSequenceString();
		if(rns == null) throw new NullPointerException();

		if(rns == SEQRES) theResult = sequenceString; // mmcif is the default
		else if(sequences != null && sequences.containsKey(rns) )
		{
			theResult = sequences.get(rns);
		}
		else
		{
			if(sequences == null)
			{
				sequences = new HashMap<ResidueNumberScheme, String>();
			}
			StringBuilder sb = new StringBuilder(sequenceString.length() * 2);
			for( ResidueId rid : getResidueIdMaps().get(rns).values() )
			{
				sb.append(rid.getResidueInfo().getOneLetterCode());
			}
			theResult = sb.toString();
		}
		return theResult;
	}

	public String getSequenceString() {
		return sequence;
	}

	public int getSequenceLength() {
		
		return sequence.length();
	}

	public int getSequenceLength(ResidueNumberScheme rns)
	{
		int result;
		if(rns == null) throw new NullPointerException();

		if(rns == SEQRES) result = getSequenceLength();
		else
		{
			result = getResidueIdMaps().get(rns).size();
		}
		return result;
	}

	public Map<ResidueNumberScheme, Map<String, ResidueId>> getResidueIdMaps()
	{
		return residueIdMaps;
	}

	public SegmentedSequence getSegmentedSequence(int fragmentLength, ResidueNumberScheme rns)
	{
		return new SegmentedSequenceImpl(this, rns, fragmentLength);
	}

	@Override
	public String toString()
	{
		StringBuilder retValue = new StringBuilder();

		retValue.append(getClass().getSimpleName())
		.append(' ')
		.append(getStructureId())
		.append(':')
		.append(getChainId())
		.append('(')
				.append(getPdbChainId())
				.append(')');
		return retValue.toString();
	}




	private class ResiduePredicate implements Predicate, Serializable
	{
		private static final long serialVersionUID = 1L;
		private final ResidueInfo residueInfo;

		protected ResiduePredicate(ResidueInfo theResidue)
		{
			this.residueInfo = theResidue;
		}

		public boolean evaluate(Object arg0) {
			if(arg0 instanceof ResidueId)
			{
				ResidueId rid = (ResidueId)arg0;
				ResidueInfo r = rid.getResidueInfo();
				assert r != null : "ResidueInfo is null for " + rid;
				return r.equals(residueInfo);
			}
			return false;
		}
	}
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((getPolymerType() == null) ? 0 : getPolymerType().hashCode());
		result = PRIME * result + ((getSequenceString() == null) ? 0 : getSequenceString().hashCode());
		result = PRIME * result + ((getStructureId() == null) ? 0 : getStructureId().hashCode());
		result = PRIME * result + ((getChainId() == null) ? 0 : getChainId().hashCode());
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
		final Sequence other = (Sequence) obj;
		if (getChainId() == null) {
			if (other.getChainId() != null)
				return false;
		} else if (!getChainId().equals(other.getChainId()))
			return false;
		if (getPolymerType() == null) {
			if (other.getPolymerType() != null)
				return false;
		} else if (!getPolymerType().equals(other.getPolymerType()))
			return false;
		if (getStructureId() == null) {
			if (other.getStructureId() != null)
				return false;
		} else if (!getStructureId().equals(other.getStructureId()))
			return false;
		if (getSequenceString() == null) {
			if (other.getSequenceString() != null)
				return false;
		} else if (!getSequenceString().equals(other.getSequenceString()))
			return false;
		return true;
	}


}
