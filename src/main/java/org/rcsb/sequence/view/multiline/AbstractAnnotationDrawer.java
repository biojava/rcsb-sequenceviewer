package org.rcsb.sequence.view.multiline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.core.AbstractAnnotationGroup;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.ResidueUtils;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ResourceManager;


public abstract class AbstractAnnotationDrawer<T> extends AbstractDrawer<T> implements AnnotationDrawer {

	private final AnnotationName  		annotationName;
	private final AnnotationGroup<T> 	annotationGroup;

	public static final int MIN_DISPLAY_LABEL;

	static {
		ResourceManager rm = new ResourceManager("sequenceview");
		MIN_DISPLAY_LABEL = Integer.parseInt(rm.getString("MIN_DISPLAY_LABEL"));
	}


	public AbstractAnnotationDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<T>> annotationGroupClass, int annotationHeight) {
		super(image, sequence);

		this.annotationGroup = (AnnotationGroup<T>) sequence.getAnnotationGroup(annotationGroupClass);
		this.annotationName = annotationGroup.getName();

		maxAnnotationsPerResidue = annotationGroup.getMaxAnnotationsPerResidue();
		if(maxAnnotationsPerResidue > 1)
		{
			if(!annotationGroup.annotationsMayOverlap())
			{
				System.err.println("Overlapping annotations found when not allowed on " + annotationGroup);
				
			}
			if(!canDrawAnnotationsThatOverlap())
			{
				System.err.println("Overlapping annotations but I can't render them! (I am " + this.getClass().getSimpleName());
			}
		}

		setAnnotationHeight(annotationHeight);
		setImageHeight(maxAnnotationsPerResidue * annotationHeight);

		if(!canDrawAnnotation(annotationName))
		{
			throw new RuntimeException(this.getClass().getSimpleName() + " cannot render " + annotationName.getName() + " annotations");
		}

		initAnnotationPixelRanges();
	}

	public AnnotationName getAnnotation() {
		return annotationName;
	}
	protected AnnotationName getAnnotationName() {
		return annotationName;
	}

	protected AnnotationGroup<T> getAnnotationGroup() {
		return annotationGroup;
	}

	public boolean canDrawAnnotation(AnnotationName anAnnotationName) {
		return true;
	}


	/**
	 * This method controls the image map popup text that is displayed if the mouse cursor is for a while over the sequence image
	 *
	 */
	public ImageMapData getHtmlMapData()
	{
		if(mapData == null)
		{
			mapData = new ImageMapData("annotation" + hashCode(), getImageHeight())
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void populateImageMapData() {

					addImageMapDataEntry(new Entry(0, getImage().getImageWidthOffset() - 1, getKeyTooltip(), null));

					for( AnnotationPixelRange apr : annotationPixelRanges )
					{
						if(apr.isAGapInAnnotation)
						{
							addImageMapDataEntry(new Entry(apr.xMin, apr.yMin, apr.xMax, apr.yMax, "No data for this region", apr.annotation));
						}
						else
						{
							addImageMapDataEntry(new Entry(apr.xMin, apr.yMin, apr.xMax, apr.yMax, apr.annotation.getName().getName() + ": " + apr.annotation.getAnnotationValue().getDescription(), apr.annotation));
						}
					}
				}
			};
		}
		return mapData;
	}

	
	@Override
	protected void drawData(final Graphics2D g2, final int yOffset) {
		for(AnnotationPixelRange apr : annotationPixelRanges)
		{
			if(apr == null)
			{
				throw new RuntimeException("Found null value in collection of AnnotationPixelRanges");
			}

			if(apr.isAGapInAnnotation)
			{
				drawSpaceBetweenAnnotations(g2, apr.numResidues, apr.xMin, yOffset + apr.yMin, apr.xMax, yOffset + apr.yMax);
			}
			else
			{
			
				drawAnnotationFragment(g2, apr.annotation, apr.xMin, yOffset + apr.yMin, apr.xMax, yOffset + apr.yMax, apr.startTruncated, apr.endTruncated);
			
			}
		}
	}

	private void drawAnnotationFragment(Graphics2D g2, 
			Annotation<T> annotation, 
			int xMin, int yMin, int xMax, int yMax, 
			boolean startIsNotStart, boolean endIsNotEnd)
	{
		drawAnnotationFragment(g2, annotation.getAnnotationValue(), annotation.getSequence().getSequenceLength(), xMin, yMin, xMax, yMax, startIsNotStart, endIsNotEnd);
		String label = getLabelString(annotation);

		if(displayLabel()) {

			if (labelFits(xMin, xMax, label)) {
				//PdbLogger.warn("fits " + xMin + " " + xMax + " " + label);
				int labelXpos = xMin + ((xMax - xMin - (label.length() * getImage().getFontWidth())) / 2);
				renderLabel(g2, label, labelXpos, yMin + getLabelYpos());

			}	else {
				//PdbLogger.warn("does not fit " + (xMax - xMin ) + " " + label + " required: " +  label.length() * getImage().getFontWidth() + " string length: "+ label.length() + " font: " + getImage().getFontWidth());

				// DRAW A SHORTENED LABEL
				// label does not fit,  but perhaps the description line is so long that we need to truncate it
				int availablePx = xMax - xMin;

				if (( availablePx ) > MIN_DISPLAY_LABEL ) {

					// show only as many characters as fit into requredPx

					int maxC = availablePx / getImage().getFontWidth()  ;

					//PdbLogger.warn("max characters: " + maxC + " " + label.length() + " " + label.length() * getImage().getFontWidth() + "px");

					if (( maxC > 0 ) && (maxC < label.length())) {

						String miniLabel = label.substring(0,maxC) + "...";

						int miniL = miniLabel.length() * getImage().getFontWidth() ;

						int labelXpos = xMin + ((availablePx - miniL)/2);

						if ( labelXpos < xMin)
							labelXpos = xMin + 20;

						renderLabel(g2, miniLabel, labelXpos, yMin + getLabelYpos());
					}

				}
			}
		}

	}

	/** Draws the TEXT of the annotation...
	 * 
	 * @param g2
	 * @param label
	 * @param x
	 * @param y
	 */
	protected void renderLabel(Graphics2D g2, String label, int x, int y)
	{
		
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                 RenderingHints.VALUE_ANTIALIAS_ON);
		 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		
		g2.setColor(Color.black);
		//Font font = new Font("Ariel", Font.PLAIN, );
		Font font = g2.getFont();
	
		FontRenderContext frc = g2.getFontRenderContext();
        TextLayout textLayout = new TextLayout(label, font, frc);
        Color textColor = Color.black;
        Color outlineColor = Color.white;
        
        
        // outline
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        Shape outline = textLayout.getOutline(at);
        g2.setStroke(new BasicStroke(3f));

        g2.fill(outline);
        g2.setPaint(outlineColor);
        g2.draw(outline);
        
 
        // Text can be plain text if effect is 0 in colorText.
       
        g2.setPaint(textColor);
        textLayout.draw(g2, x, y);

        //g2.drawString(label, x, y +1);
        
        
        
        
        
        


	}

	abstract protected void drawSpaceBetweenAnnotations(Graphics2D g2, int sequenceLength, int xMin, int yMin, int xMax, int yMax);
	abstract protected void drawAnnotationFragment(Graphics2D g2, AnnotationValue<T> annotation, int sequenceLength, int xMin, int yMin, int xMax, int yMax, boolean startIsNotStart, boolean endIsNotEnd);
	abstract protected boolean displayLabel();

	protected boolean labelFits(final int xMin, final int xMax, String label)
	{
		if(label == null || label.length() == 0) return false;

		final int availablePx = xMax - xMin;
		final int requiredPx  = label.length() * getImage().getFontWidth();

		return requiredPx < availablePx;
	}


	/** Create the label string that is displayed for an annotation on the graphics.
	 * If description == value of annotation, only show the value.
	 * TODO: make sure that CATH and the other annotations have a nice description.
	 */

	
	protected String getLabelString(Annotation<T> av)
	{
		String value = av.getAnnotationValue().value().toString().trim();

		String desc = av.getAnnotationValue().getDescription().trim();

		if (  value.equals(desc))
			return value;

		StringBuffer buf = new StringBuffer();
		buf.append(desc);
		buf.append(" (");
		buf.append(value);
		buf.append(")");
		
		//System.out.println("AbstactAnnotationDrawer: label: " + buf.toString());
		return buf.toString();


	}
	protected int getLabelYpos()
	{
		return (int) (getImage().getFontHeight() * 0.85);
	}

	protected int getStringLengthPx(String theString)
	{
		return theString.length() * getImage().getFontWidth();
	}

	protected class AnnotationPixelRange
	{
		final int xMin, xMax, yMin, yMax, numResidues;
		final boolean startTruncated, endTruncated, isAGapInAnnotation;
		
		final Annotation<T> annotation;

		
		protected AnnotationPixelRange(int xMin, int xMax, int yMin, int yMax, int numResidues, 
				boolean startTruncated, boolean endTruncated, Annotation<T> annotation)
		{
			final int imageWidth = getImage().getImageWidth();
			if(xMax < xMin)
			{
				throw new RuntimeException("xMax is smaller than xMin");
			}
			if(xMax > imageWidth)
			{
				System.err.println("Graphic may overflow image boundary -- xMax is " + xMax + " but image width is only " + imageWidth);
				xMax = imageWidth;
			}
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
			this.numResidues = numResidues;
			this.startTruncated = startTruncated;
			this.endTruncated = endTruncated;
			this.annotation = annotation;
			this.isAGapInAnnotation = annotation == null;
		}
		
		protected AnnotationPixelRange(int xMin, int xMax, int yMin, int yMax, 
				boolean startTruncated, boolean endTruncated, Annotation<T> annotation)
		{
			this(xMin, xMax, yMin, yMax, annotation.getSequence().getSequenceLength(), startTruncated, endTruncated, annotation);
		}
		protected AnnotationPixelRange(int xMin, int xMax, int yMin, int yMax, int numResidues)
		{
			this(xMin, xMax, yMin, yMax, numResidues, false, false, null);
		}
	}

	private final int maxAnnotationsPerResidue;

	private Collection<AnnotationPixelRange> annotationPixelRanges = null;
	
	protected void initAnnotationPixelRanges()
	{
		if(annotationPixelRanges == null)
		{
			AnnotationGroup<T> annotationGroup = getAnnotationGroup();
			Sequence sequence = getSequence();
			annotationPixelRanges = new LinkedList<AnnotationPixelRange>();

			if(annotationGroup == null)
			{
				//            throw new RuntimeException("Annotation group is null!");
				System.err.println("Annotation group is null -- image will be blank");
				return;
			}

			final ResidueNumberScheme rnsOfAnnotation = annotationGroup.getResidueNumberScheme();
			final ResidueNumberScheme rnsOfChain      = sequence.getDefaultResidueNumberScheme();

			// these collections are sufficient for a very simple alignment display
			final Map<ResidueId, Integer> annotationSeqResiduesWithGapsAfter;
			try {
			    annotationSeqResiduesWithGapsAfter = sequence.getNonContiguousResidueIds(rnsOfAnnotation, rnsOfChain);
			} catch (RuntimeException e){
			   System.err.println(e.getMessage());
			   return;
			}
			ResidueId chainResAnnStart, annResStart, chainResAnnEnd, annResEnd;
			ResidueId lastResidueAnnotated;

			Sequence aSeq;
			boolean startTruncated, endTruncated;
			int xPos;
			Integer numResidues;

			Collection<SortedSet<Annotation<T>>> nonOverlappingAnnotations = initNonOverlappingAnnotations(annotationGroup.getAnnotations());

			if(nonOverlappingAnnotations.size() != maxAnnotationsPerResidue)
			{
				throw new RuntimeException("Was expecting " + maxAnnotationsPerResidue + " non-overlapping collections of annotations but instead got " + nonOverlappingAnnotations.size());
			}

			final int fontWidth = getImage().getFontWidth();
			final int annotationHeight = getAnnotationHeight();
			final int imageWidth = getImage().getImageWidth();
			final int imageHeight = getImageHeight();

			int yMin = 0, yMax = annotationHeight - 1; // oh no, another set of nudge factors!

			for(Collection<Annotation<T>> annotations : nonOverlappingAnnotations)
			{
				xPos = getImage().getImageWidthOffset();
				lastResidueAnnotated = sequence.getFirstResidue().getPrevious();

				for(Annotation<T> a : annotations)
				{
													
					startTruncated = a.isBeginningTruncated();
					endTruncated   = a.isEndTruncated();

					aSeq = a.getSequence();

					annResStart = aSeq.getFirstResidue(rnsOfAnnotation);
					annResEnd   = aSeq.getLastResidue(rnsOfAnnotation);

					if(annResStart == null && annResEnd == null)
					{
						System.err.println("There are no residues between " + aSeq.getFirstResidue() + " and " + aSeq.getLastResidue() + " with residue number scheme " + rnsOfAnnotation);
						continue;
					}

					chainResAnnStart = annResStart.getEquivalentResidueId(rnsOfChain);
					chainResAnnEnd   = annResEnd.getEquivalentResidueId(rnsOfChain);

					// if there is no equivalent residue on the sequence being displayed, get the first residue in the
					// annotaion that does have an equivalent
					if(chainResAnnStart == null)
					{
						System.err.println("Annotation starting on a residue with no equivalent in " + rnsOfChain);
						chainResAnnStart = annResStart.getNextEquivalentResidueId(rnsOfChain);
					}

					if(chainResAnnEnd == null)
					{
						System.err.println("Annotation ending on a residue with no equivalent in " + rnsOfChain);
						chainResAnnEnd = annResEnd.getPreviousEquivalentResidueId(rnsOfChain);
					}

					// we need to make sure the start and end residues on the sequence to be viewed make sense
					// (i.e. the start is before the end, or they're the same)
					if(!(chainResAnnStart == chainResAnnEnd || chainResAnnStart.isBefore(chainResAnnEnd)))
					{
						// if not, we can't draw anything for this annotation; on to the next one!
						System.err.println("Not Creating pixel range for annotation " + a +
						" because the sequence being displayed doesn't contain any of the residues in it");

						continue;
					}

					// OK. We now know that the annotation needs drawing. But what if there are sections
					// of the sequence being displayed that the annotation doesn't know about?

					// first, the obvious one: any residues on the sequence being displayed that are between
					// the end of the last annotation and the beginning of this one
					if(chainResAnnStart.getPrevious() != lastResidueAnnotated)
					{
						int counter = 0;
						ResidueId aRid = chainResAnnStart;
						while((aRid = aRid.getPrevious()) != lastResidueAnnotated && !aRid.isBeginningOfChainMarker())
						{
							++counter;
						}
						//               renderSpaceBetweenAnnotations(g2, counter, xPos, xPos += counter * fontWidth);
						
						annotationPixelRanges.add(new AnnotationPixelRange(xPos, xPos += counter * fontWidth, yMin, yMax, counter));
						lastResidueAnnotated = chainResAnnStart.getPrevious();
					}

					// let us iterate through all the residues we're thinking about annotating on the sequence being
					// displayed and see if there are any gaps.
					ResidueId aRid = chainResAnnStart.getEquivalentResidueId(rnsOfAnnotation);
					ResidueId endRid = chainResAnnEnd.getEquivalentResidueId(rnsOfAnnotation);

					if(aRid == null || endRid == null)
					{
						throw new RuntimeException("This probably should never be hit");
					}

					// make sure we really really really can render this annotation
					aRid.ensureBeforeOrEqual(endRid);

					int counter = 0;
					boolean isFirst = true;
					ResidueId cursor, maybeLastResidueAnnotated;
					for(Iterator<ResidueId> ridIt = ResidueUtils.getResidueIdsBetween(aRid, endRid).iterator(); ridIt.hasNext();)
					{
						cursor = ridIt.next();

						// if the residue at the cursor has an equivalent on the sequence being displayed, we should
						// increment the counter of how long the annotation fragment is.
						if( (maybeLastResidueAnnotated = cursor.getEquivalentResidueId(rnsOfChain)) != null )
						{
							// also make a note of the last residue being displayed that we actually annotated
							// so that the next time through the annotation loop we know whether to bung in another spacer
							// (this would go at the end of the loop but then we'd have to do another check for whether a residue
							// has an equivalent)
							lastResidueAnnotated = maybeLastResidueAnnotated;
							//                  System.err.println(cursor + " has equivalent residue " + lastResidueAnnotated + " so we have incremented the counter to " + counter);
							++counter;
						}
						// now we need to
						else
						{
							System.err.println(cursor + " doesn't have an equivalent residue in " + rnsOfChain + " so we are keeping the counter at " + counter);
						}

						if( ridIt.hasNext() && (numResidues = annotationSeqResiduesWithGapsAfter.get(cursor)) != null )
						{
							// we need to render an annotation and then a gap... UNLESS it's the last residue in the loop (because then the gap is dealt with elsewhere)

							// can only render an annotation fragment if it's large enough
							if(counter > 0)
							{
								
								annotationPixelRanges.add(new AnnotationPixelRange(xPos, xPos += counter * fontWidth, yMin, yMax, startTruncated || !isFirst, ridIt.hasNext() || endTruncated, a));
								isFirst = false;
							}
							else
							{
								System.err.println("I would have rendered an annotation fragment but it looks like it was going to cover 0 residues so i didn't");
							}

							//System.err.println("Creating pixel range space to cover " + numResidues + " residues that are in " + rnsOfChain + " but not " + rnsOfAnnotation + " for " + a);
							annotationPixelRanges.add(new AnnotationPixelRange(xPos, xPos += numResidues * fontWidth, yMin, yMax, numResidues));
							counter = 0;
						}
					}

					// and then render the last bit
					if(counter > 0)
					{
						
						annotationPixelRanges.add(new AnnotationPixelRange(xPos, xPos += counter * fontWidth, yMin, yMax, counter, startTruncated || !isFirst, endTruncated, a));
					}
				}

				int numResLeft = (imageWidth - xPos) / fontWidth;
				if(numResLeft > 0) // if there's
				{
					
					annotationPixelRanges.add(new AnnotationPixelRange(xPos, imageWidth - 1, yMin, yMax, numResLeft));
				}

				yMin += annotationHeight; // increment the yOffsets by the size of a single annotation
				yMax += annotationHeight;
			}

			if(yMin != imageHeight)
			{
				throw new RuntimeException("I was really hoping the image was going to be " + imageHeight + " tall but we drew stuff to " + yMin);
			}
		}
	}

	private Collection<SortedSet<Annotation<T>>> initNonOverlappingAnnotations(SortedSet<Annotation<T>> collection) {
		AnnotationGroup<T> annotationGroup = getAnnotationGroup();
		if(!annotationGroup.annotationsDoOverlap())
		{
			return Collections.singleton(collection);
		}
		if(!annotationGroup.annotationsMayOverlap())
		{
			System.err.println("Annotations may not overlap but they do for " + annotationGroup);
		}

		Collection<SortedSet<Annotation<T>>> result = new ArrayList<SortedSet<Annotation<T>>>();
		Comparator<Annotation<?>> comparator = AbstractAnnotationGroup.SORT_ANNOTATIONS_COMPARATOR;

		// annotations are sorted by first residue, then last residue, then the string value of the annotation value
		SortedSet<Annotation<T>> theSet;
		result.add(new TreeSet<Annotation<T>>(comparator));

		for(Annotation<T> a : collection)
		{
			boolean addedYet = false;
			Iterator<SortedSet<Annotation<T>>> it = result.iterator();
			do
			{
				theSet = it.next();
				if(!annotationsOverlap(a, theSet))
				{
					theSet.add(a);
					addedYet = true;
				}
			}
			while(!addedYet && it.hasNext());

			if(addedYet == false)
			{
				theSet = new TreeSet<Annotation<T>>(comparator);
				theSet.add(a);
				result.add(theSet);
			}
		}
		return result;
	}

	private boolean annotationsOverlap(Annotation<T> a, SortedSet<Annotation<T>> bs)
	{
		for(Annotation<T> b : bs)
		{
			if(annotationsOverlap(a, b)) return true;
		}
		return false;
	}

	private boolean annotationsOverlap(Annotation<T> a, Annotation<T> b)
	{
		ResidueId a1, a2, b1, b2;

		a1 = a.getSequence().getFirstResidue();
		a2 = a.getSequence().getLastResidue();
		b1 = b.getSequence().getFirstResidue();
		b2 = b.getSequence().getLastResidue();

		return a1.equals(b1) || a2.equals(b2) || b1.isBetween(a1, a2) || b2.isBetween(a1, a2);
	}

	@Override
	protected String getKey() {
		return getAnnotationName().getName();
	}

	protected String getKeyTooltip() {
		AnnotationName annotationName = getAnnotationName();
		return annotationName.getDescription() + "; a " + annotationName.getClassification().getName() + " annotation";
	}

	public boolean canDrawAnnotationsThatOverlap() {
		return false;
	}


}
