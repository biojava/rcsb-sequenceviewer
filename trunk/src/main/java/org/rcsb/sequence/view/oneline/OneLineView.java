package org.rcsb.sequence.view.oneline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AnnotationDrawMapper;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.SegmentedSequence;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.html.AnnotationSummaryCell;
import org.rcsb.sequence.view.html.ViewParameters;
import org.rcsb.sequence.view.multiline.Annotation2MultiLineDrawer;
import org.rcsb.sequence.view.multiline.SequenceImage;
import org.rcsb.sequence.view.multiline.SequenceImageIF;
import org.rcsb.sequence.view.multiline.SequenceSummaryImage;

public class OneLineView implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4610160761984929732L;



	private final Collection<AnnotationSummaryCell<?>> annotationSummaryCells;
	private final Collection<AnnotationName> annotationsToView;
	private final SegmentedSequence backingData;
	private final Chain chain;
	private final ViewParameters params;

	AnnotationDrawMapper annotationDrawMapper ;
	
	private SequenceImage 		sequenceImage 			= null;
	

	public OneLineView(Sequence sequence, ViewParameters params)
	{
		this(sequence.getSegmentedSequence(sequence.getSequenceLength(), getBestRns(sequence, params.getDesiredSequenceRns())), params);
	}


	public OneLineView(SegmentedSequence segmentedSequence, ViewParameters params)
	{


		this.params = params;
		this.backingData = segmentedSequence;
		this.annotationDrawMapper = new Annotation2SingleLineDrawer();
		
		/*
		 * At this point, we should instantiate a collection of annotations that we are ACTUALLY GOING TO DISPLAY
		 * FOR THIS CHAIN. If all the desired annotations from ViewParameters are present, we can just use that. 
		 * 
		 * Otherwise we should create a new collection. This will either remove those annotations that don't exist,
		 * or replace them with the next best annotation, depending on the state of params.isShowNextBestAnnotationIfPossible()
		 * 
		 * (Currently, the 'Next Best' annotation is defined as the first one available in the same AnnotationClassification.
		 * To get it on a sequence, segmentedSequence.getFirstAnnotationGroupWithData(AnnotationClassification)
		 * is probably the way to go. )
		 */
		AnnotationGroup<?> ag;
		Set<AnnotationSummaryCell<?>> strs = new LinkedHashSet<AnnotationSummaryCell<?>>();
		Set<AnnotationName> someAnnotationsToView = new LinkedHashSet<AnnotationName>();
		Collection<AnnotationName> lst = params.getAnnotations();
		if ( lst == null)
			lst = new ArrayList<AnnotationName>();

		annotationDrawMapper = new Annotation2SingleLineDrawer();
		
		annotationDrawMapper.ensureInitialized();

		for(AnnotationName an : lst)
		{

			if ( an == null) {
				System.err.println("got NULL instead of annotation name!");
				continue;
			}


			if( ((ag = this.backingData.getAnnotationGroup(an.getAnnotationClass())) == null || !ag.hasData()) 
					&& params.isShowNextBestAnnotationIfPossible() ) // should we look for the next best?
			{
				ag = getReplacementAnnotationGroupIfPossible(an);

				if(ag != null) {

					an = ag.getName();
				}
			}

			//System.out.println("testing if ag should be displayed " + ag + " an: " + an.getAnnotationClass());

			if(ag != null && ag.hasData())
			{
				someAnnotationsToView.add(an);

				if(annotationDrawMapper.hasSummaryTableRow(an.getName()))
				{
					strs.add( annotationDrawMapper.createSummaryTableRowInstance(ag) );
				}
			}
		}

		this.annotationSummaryCells = Collections.unmodifiableCollection(strs);
		this.annotationsToView = Collections.unmodifiableCollection(someAnnotationsToView);


		this.chain = this.backingData.getFirstResidue().getChain();
	}


	private static ResidueNumberScheme getBestRns(Sequence s, ResidueNumberScheme rns)
	{
		ResidueNumberScheme result = rns;
		if(!s.hasResiduesIndexedBy(rns))
		{
			System.err.println("Requested RNS " + rns + " is not available for sequence " + s);
			result = ResidueNumberScheme.SEQRES;
		}
		return result;
	}

	public AnnotationGroup<?> getReplacementAnnotationGroupIfPossible(AnnotationName an)
	{
		AnnotationGroup<?> ag = this.backingData.getFirstAnnotationGroupWithData(an.getClassification());
		if(ag == null) return null;

		an = ag.getName();
		if(params.getDisabledAnnotations().contains(an)) // this new annotation might be disabled
		{
			return null;
		}

		return ag;
	}

	public int getFragmentCount()
	{
		return this.backingData.getSegmentCount();
	}



	public SequenceImage getSequenceImage() {
		System.out.println("chainview: getSequenceImage " + sequenceImage );
		System.out.println("params: " +params);
		System.out.println("backinData: " + backingData);
		System.out.println("annotationsToView: " + annotationsToView);

		if(sequenceImage == null)
		{
			this.sequenceImage = new SequenceImage(backingData, annotationsToView, params.getDesiredBottomRulerRns(), params.getDesiredTopRulerRns(), params.getFontSize(), params.getFragmentBuffer(), params.getNumCharsInKey(), annotationDrawMapper);
			
		}

		return sequenceImage;
	}

	void resetSequenceImage()
	{
		sequenceImage = null;
	}

	public SegmentedSequence getSegmentedSequence()
	{
		return this.backingData;
	}

	public Chain getChain() {
		return this.chain;
	}

	public Collection<AnnotationSummaryCell<?>> getAnnotationSummaryCells()
	{
		return this.annotationSummaryCells;
	}

	private Collection<AnnotationName> otherAnnotationsAvailable = null;
	public Collection<AnnotationName> getOtherAnnotationNamesAvaialable()
	{
		if(this.otherAnnotationsAvailable == null)
		{
			this.otherAnnotationsAvailable = new TreeSet<AnnotationName>();

			Collection<AnnotationGroup<?>> dataAnnos = this.chain.getAnnotationGroupsWithData();
			System.out.println("OneLineView: got annotations with data:" + dataAnnos.size());
			for(AnnotationGroup<?> ag : dataAnnos )
			{
				this.otherAnnotationsAvailable.add(ag.getName());
			}

			// remove those that are already displayed
			this.otherAnnotationsAvailable.removeAll(this.annotationsToView);

			//TODO: find a different way to filter things that can get displayed!
			// uncommenting for now AP
			//System.out.println("OneLineView beforeretain: " + otherAnnotationsAvailable.size());
			// only keep those that can be rendered
			//this.otherAnnotationsAvailable.retainAll(Annotation2Html.ANNOTATION_TO_RENDERER_MAP.keySet());
			//System.out.println("OneLineView after retain: " + otherAnnotationsAvailable.size());
		}


		return this.otherAnnotationsAvailable;
	}

	public Map<String, String> getOtherAnnotationsAvailable()
	{
		return getOtherAnnotationsAvailable(null); // special case
	}

	public Map<String, String> getOtherAnnotationsAvailable(String annotationClassification)
	{
		AnnotationClassification ac = annotationClassification == null ? null : AnnotationClassification.valueOf(annotationClassification);
		Map<String, String> result = new LinkedHashMap<String, String>();
		for(AnnotationName an : getOtherAnnotationNamesAvaialable())
		{
			if(ac == null || ac == an.getClassification())
			{
				result.put(an.getName(), an.getName());
			}
		}
		return result;
	}

	public Collection<AnnotationClassification> getAvailableAnnotationClassifications()
	{
		Collection<AnnotationClassification> result = new LinkedHashSet<AnnotationClassification>();
		for(String an : getOtherAnnotationsAvailable().keySet())
		{
			result.add(AnnotationRegistry.getAnnotationByName(an).getClassification());
		}
		return result;
	}

	public Collection<AnnotationName> getAnnotationsToView() {
		return annotationsToView;
	}

	public void setAnnotationDrawMapper(AnnotationDrawMapper a2h) {
		annotationDrawMapper = a2h;

	}
	
	public AnnotationDrawMapper getAnnotationDrawMapper(){
		return annotationDrawMapper;
	}
}
