package org.rcsb.sequence.view.html;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;


import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.view.html.SequenceCollectionView.ChainEntityStrategy;
import org.rcsb.sequence.view.html.SequenceCollectionView.ChainSortStrategy;
import org.rcsb.sequence.view.html.SequenceCollectionView.ChainViewStrategy;

/**
 * <p>This class is used to store parameters that configure how the Sequence page is to be displayed.  It is not linked to a
 * particular structure, and is stored in the session.</p>
 * 
 * <p>An instance of this object is accessible from <tt>RemediatedSequenceAction</tt> and <tt>RemediatedChainAction</tt>
 * by calling {@link AbstractSequenceReportAction#getParams()}. Thus is is also accessible from OGNL within Struts2 Jsp pages
 * for example in the following excerpt from <tt>remediatedSequence.jsp</tt>, we directly evaluate {@link ViewParameters#isShowJmol()}
 * via OGNL:
 * <pre>
 * &lt;ww:if test="params.showJmol"&gt;
 *    &lt;applet name="jmol"&gt;
 *       &lt;!-- more applet configuration --&gt;
 *    &lt;/applet&gt;
 * &lt;/ww:if&gt;
 * </pre>
 * <p>In addition, the view parameter can be modified via HTTP GET and POST commands with parameter names beginning with <tt>param.</tt>.  
 * For example, <a href="http://www.pdb.org/pdb/explore/remediatedSequence.do?structureId=1DDT&params.fragmentLength=47">this</a> 
 * URL includes the GET parameter <tt>params.fragmentLength=47</tt> and will therefore load the sequence page with a custom fragment length.</p>
 * 
 * <p>There is no particular magic to modifying these parameters via URL or JSP page -- this is normal Struts2 behaviour.</p>
 * 
 * 
 * @author mulvaney
 *
 */
public final class ViewParameters implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	// defaults
	private static final int CHAINS_PER_PAGE_DEFAULT = 3;
	private static final int FRAGMENT_LENGTH_DEFAULT = 60;
	private static final int FONT_SIZE_DEFAULT       = 12;
	private static final int FONT_SIZE_MAX           = 64;
	private static final int NUM_CHARS_IN_KEY        = 8; // affects the white space on the left side of the image.

	private static final int DEFAULT_JMOL_WIDTH      = 450;
	private static final int DEFAULT_JMOL_HEIGHT     = 450;
	private static final int MAX_JMOL_WIDTH     	 = 2000;
	private static final int MAX_JMOL_HEIGHT    	 = 2000;
	private static final int MIN_JMOL_WIDTH    		 = 50;
	private static final int MIN_JMOL_HEIGHT   		 = 50;
	
	
	
	private static final ResidueNumberScheme DEFAULT_TOP_RULER_RNS = null;
	private static final ResidueNumberScheme DEFAULT_BOTTOM_RULER_RNS = ResidueNumberScheme.ATOM;
	private static final ResidueNumberScheme DEFAULT_SEQUENCE_RNS = ResidueNumberScheme.SEQRES;

	private static final boolean DEFAULT_SHOW_DBREF_RULER = false;
	private static final boolean DEFAULT_SHOW_NEXTBEST_AN = true;

	private static final ChainEntityStrategy DEFAULT_CHAIN_ENTITY_STRATEGY = ChainEntityStrategy.first;
	private static final ChainViewStrategy   DEFAULT_CHAIN_VIEW_STRATEGY   = ChainViewStrategy.all;
	private static final ChainSortStrategy   DEFAULT_CHAIN_SORT_STRATEGY   = ChainSortStrategy.chainTypeThenPdbChainId;

	private static final Collection<AnnotationName> DEFAULT_DISABLED_ANNOTATIONS = Collections.emptySet();
	private static final float DEFAULT_FRAGMENT_BUFFER = 1.8f; // size of gap between fragments relative to font width
	private static final boolean DEFAULT_SHOW_JMOL   = false; // do not show Jmol by default
	private static final boolean DEFAULT_STICKY_JMOL = true; // Jmol is fixed by default

	// parameters
	private ResidueNumberScheme desiredTopRulerRns    = DEFAULT_TOP_RULER_RNS;
	private ResidueNumberScheme desiredBottomRulerRns = DEFAULT_BOTTOM_RULER_RNS;
	private ResidueNumberScheme desiredSequenceRns    = DEFAULT_SEQUENCE_RNS;

	private boolean showDbRefRulerIfPossible         = DEFAULT_SHOW_DBREF_RULER;
	private boolean showNextBestAnnotationIfPossible = DEFAULT_SHOW_NEXTBEST_AN;

	private Collection<AnnotationName> disabledAnnotations = DEFAULT_DISABLED_ANNOTATIONS;
	private Collection<AnnotationName> annotationsToView = AnnotationName.DEFAULT_ANNOTATIONS_TO_VIEW;

	private int fontSize       = FONT_SIZE_DEFAULT;
	private int fragmentLength = FRAGMENT_LENGTH_DEFAULT;
	private int chainsPerPage  = CHAINS_PER_PAGE_DEFAULT;
	private int numCharsInKey  = NUM_CHARS_IN_KEY;

	private float fragmentBuffer = DEFAULT_FRAGMENT_BUFFER;

	private ChainEntityStrategy chainEntityStrategy = DEFAULT_CHAIN_ENTITY_STRATEGY;
	private ChainViewStrategy   chainViewStrategy   = DEFAULT_CHAIN_VIEW_STRATEGY;
	private ChainSortStrategy   chainSortStrategy   = DEFAULT_CHAIN_SORT_STRATEGY;

	private boolean showJmol   = DEFAULT_SHOW_JMOL;
	private boolean stickyJmol = DEFAULT_STICKY_JMOL;

	private int jmolWidth      = DEFAULT_JMOL_WIDTH;
	private int jmolHeight     = DEFAULT_JMOL_HEIGHT;
	
	
	/**
	 * Is Jmol to be displayed?
	 * @return <tt>true</tt> if Jmol is to be displayed
	 */
	public boolean isShowJmol() {
		return showJmol;
	}

	/**
	 * Set whether Jmol should be displayed on the Sequence page
	 * @param showJmol <tt>true</tt> if Jmol is to be displayed, <tt>false</tt> otherwise
	 */
	public void setShowJmol(boolean showJmol) {
		this.showJmol = showJmol;
	}

	/** check if Jmol should automatically slide to the viewable area on the screen, or stay fixed at the location
	 * 
	 * @return <tt>true</tt> if Jmol is at a fixed location.
	 */
	public boolean isStickyJmol() {
		return stickyJmol;
	}

	/** set whether Jmol should be fixed at the location in the page, or automatically float to the viewable area
	 * 
	 * @param stickyJmol <tt>true</tt> if Jmol is at a fixed location.
	 */
	public void setStickyJmol(boolean stickyJmol) {		
		this.stickyJmol = stickyJmol;
	}
	
	/** get the width of the Jmol display
	 * 
	 * @return the width of the Jmol applet
	 */
	public int getJmolWidth() {
		return jmolWidth;
	}
	
	/** Set the width of the Jmol display. Maximum size = 1000. Minimum size = 50
	 *  
	 * @param width of the Jmol applet
	 */

	public void setJmolWidth(int jmolWidth) {
		if (jmolWidth > MAX_JMOL_WIDTH)
			jmolWidth = MAX_JMOL_WIDTH;
		if ( jmolWidth < MIN_JMOL_WIDTH)
			jmolWidth = MIN_JMOL_WIDTH;
		this.jmolWidth = jmolWidth;
	}

	/** get the height of the Jmol display
	 * 
	 * @return the height of the Jmol applet
	 */
	public int getJmolHeight() {
		return jmolHeight;
	}

	/** Set the height of the Jmol display. Maximum size = 1000. Minimum size = 50
	 *  
	 * @param height of the Jmol applet
	 */
	public void setJmolHeight(int jmolHeight) {
		if ( jmolHeight > MAX_JMOL_HEIGHT)
			jmolHeight = MAX_JMOL_HEIGHT;
		if ( jmolHeight < MIN_JMOL_HEIGHT)
			jmolHeight = MIN_JMOL_HEIGHT;
		this.jmolHeight = jmolHeight;
	}

	/**
	 * Get the maximum number of chains to be displayed on each page.
	 * @return
	 */
	public int getChainsPerPage() {
		return chainsPerPage;
	}

	/**
	 * Set the maximum number of chains to be displayed on each page
	 * @param chainsPerPage
	 */
	public void setChainsPerPage(int chainsPerPage) {
		this.chainsPerPage = chainsPerPage;
	}

	/**
	 * <p>Get a collection of desired {@link AnnotationName}s to be viewed on the page.</p>
	 * <p><em>NB Instances of ViewParameters do not have any information as to what <tt>Annotation</tt>s are 
	 * available for a given structure.</em> In other words, this method may return results that include <tt>Annotation</tt>s
	 * not present on the structure.</p>
	 * @return a <tt>Collection</tt> of <tt>AnnotationName</tt>s representing desired annotations
	 */
	public Collection<AnnotationName> getAnnotations() {
		return Collections.unmodifiableCollection(annotationsToView);
	}

	/**
	 * <p>Set the collection of desired {@link AnnotationName}s to be viewed on the page</p>
	 * @param desiredAnnotations
	 */
	public void setAnnotations(
			Collection<AnnotationName> desiredAnnotations) {
		//System.out.println("ViewParameters setAnnotations ... " + desiredAnnotations.size());
		this.annotationsToView = desiredAnnotations;
	}

	/**
	 * <p>Set the collection of desired {@link AnnotationName}s to be viewed on the page, from a comma-delimited list in a <tt>String</tt></p>
	 * @param an
	 */
	public void setAnnotationsStr(String an)
	{
		annotationsToView = new TreeSet<AnnotationName>(); // we will explicitly state all annotations

		String[] ans = an.split(",");
		AnnotationName aAn;

		for(String aAnStr : ans)
		{
			aAn = AnnotationRegistry.getAnnotationByName(aAnStr);
			annotationsToView.add(aAn);
			disabledAnnotations.remove(aAn);
		}
	}

	/**
	 * Get a collection of explicitly disabled {@link AnnotationName}s.
	 * @return
	 */
	public Collection<AnnotationName> getDisabledAnnotations() {
		return disabledAnnotations;
	}

	/**
	 * Set the collection of explicitly disabled {@link AnnotationName}s.
	 * @param disabledAnnotations
	 */
	public void setDisabledAnnotations(
			Collection<AnnotationName> disabledAnnotations) {
		this.disabledAnnotations = disabledAnnotations;
	}

	/**
	 * Set the collection of explicitly disabled {@link AnnotationName}s from a comma-delimited list in a <tt>String</tt>
	 * @param an
	 */
	public void setDisabledAnnotationsStr(String an)
	{
		if(annotationsToView == AnnotationName.DEFAULT_ANNOTATIONS_TO_VIEW)
		{
			annotationsToView = Collections.emptySet();
		}
		disabledAnnotations = new TreeSet<AnnotationName>();

		String[] ans = an.split(",");
		AnnotationName aAn;

		for(String aAnStr : ans)
		{
			aAn = AnnotationRegistry.getAnnotationByName(aAnStr);
			disabledAnnotations.add(aAn);
			annotationsToView.remove(aAn);
		}
	}   

	/**
	 * Get the font size to be used when rendering sequence images
	 * @return
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * Set the font size to be used when rendering sequence images
	 * @param fontSize
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = Math.min(fontSize, FONT_SIZE_MAX);
	}

	/**
	 * Get the fragment length (i.e. the number of residues on each line)
	 * @return
	 */
	public int getFragmentLength() {
		return fragmentLength;
	}

	/**
	 * Set the fragment lenght (i.e. the number of residues on each line)
	 * @param fragmentLength
	 */
	public void setFragmentLength(int fragmentLength) {
		this.fragmentLength = fragmentLength;
	}

	/**
	 * Get the available {@link ChainEntityStrategy}s.
	 * @return
	 */
	public ChainEntityStrategy[] getChainEntityStrategies()
	{
		return ChainEntityStrategy.values();
	}

	/**
	 * Get the current {@link ChainEntityStrategy}
	 * @return
	 */
	public ChainEntityStrategy getChainEntityStrategy() {
		return chainEntityStrategy;
	}

	/**
	 * Set the {@link ChainEntityStrategy}
	 * @param chainEntityStrategy
	 */
	public void setChainEntityStrategy(ChainEntityStrategy chainEntityStrategy) {
		this.chainEntityStrategy = chainEntityStrategy;
	}

	/**
	 * Get the current {@link ChainEntityStrategy} as a <tt>String</tt>
	 * @return
	 */
	public String getChainEntityStrategyStr()
	{
		return getChainEntityStrategy().name();
	}

	/**
	 * Set the {@link ChainEntityStrategy} from a <tt>String</tt>
	 * @param chainEntityStrategy
	 */
	public void setChainEntityStrategyStr(String chainEntityStrategy)
	{
		setChainEntityStrategy(ChainEntityStrategy.valueOf(chainEntityStrategy));
	}


	/**
	 * Get the available {@link ChainSortStrategy}
	 * @return
	 */
	public ChainSortStrategy[] getChainSortStrategies()
	{
		return ChainSortStrategy.values();
	}

	/**
	 * Get the current {@link ChainSortStrategy}
	 * @return
	 */
	public ChainSortStrategy getChainSortStrategy() {
		return chainSortStrategy;
	}

	/**
	 * Set the {@link ChainSortStrategy}
	 * @param chainSortStrategy
	 */
	public void setChainSortStrategy(ChainSortStrategy chainSortStrategy) {
		this.chainSortStrategy = chainSortStrategy;
	}

	/**
	 * Get the current {@link ChainSortStrategy} as a <tt>String</tt>
	 * @return
	 */
	public String getChainViewStrategyStr()
	{
		return getChainViewStrategy().name();
	}

	/**
	 * Set the {@link ChainSortStrategy} from a <tt>String</tt>
	 * @return
	 */
	public void setChainViewStrategyStr(String chainViewStrategy)
	{
		setChainViewStrategy(ChainViewStrategy.valueOf(chainViewStrategy));
	}
	/**
	 * Get the available {@link ChainViewStrategy}s
	 * @return
	 */
	public ChainViewStrategy[] getChainViewStrategies()
	{
		return ChainViewStrategy.values();
	}

	/**
	 * Get the current {@link ChainViewStrategy}
	 * @return
	 */
	public ChainViewStrategy getChainViewStrategy() {
		return chainViewStrategy;
	}

	/**
	 * Set the {@link ChainViewStrategy}
	 * @param chainViewStrategy
	 */
	public void setChainViewStrategy(ChainViewStrategy chainViewStrategy) {
		this.chainViewStrategy = chainViewStrategy;
	}

	/**
	 * Get the current {@link ChainViewStrategy} as a <tt>String</tt>
	 * @return
	 */
	public String getChainSortStrategyStr()
	{
		return getChainSortStrategy().name();
	}

	/**
	 * Set the {@link ChainViewStrategy} from a <tt>String</tt>
	 * @param chainSortStrategy
	 */
	public void setChainSortStrategyStr(String chainSortStrategy)
	{
		setChainSortStrategy(ChainSortStrategy.valueOf(chainSortStrategy));
	}

	/**
	 * Get the {@link ResidueNumberScheme} desired for the ruler displayed below the sequence
	 * @return
	 */
	public ResidueNumberScheme getDesiredBottomRulerRns() {
		return desiredBottomRulerRns;
	}

	/**
	 * Get the {@link ResidueNumberScheme} desired for the ruler displayed below the sequence as a <tt>String</tt>
	 * @return
	 */
	public String getDesiredBottomRulerRnsStr() {
		return desiredBottomRulerRns.name();
	}

	/**
	 * Set the {@link ResidueNumberScheme} desired for the ruler displayed below the sequence
	 * @param desiredBottomRulerRns
	 */
	public void setDesiredBottomRulerRns(ResidueNumberScheme desiredBottomRulerRns) {
		this.desiredBottomRulerRns = desiredBottomRulerRns;
	}

	/**
	 * Set the {@link ResidueNumberScheme} desired for the ruler displayed below the sequence from a <tt>String</tt> value
	 * @param desiredBottomRulerRns
	 */
	public void setDesiredBottomRulerRnsStr(String desiredBottomRulerRns) {
		setDesiredBottomRulerRns(ResidueNumberScheme.valueOf(desiredBottomRulerRns));
	}

	/**
	 * Get the {@link ResidueNumberScheme} desired for the sequence
	 * @return
	 */
	public ResidueNumberScheme getDesiredSequenceRns() {
		return desiredSequenceRns;
	}

	/**
	 * Get the {@link ResidueNumberScheme} desired for the sequence as a <tt>String</tt>
	 * @return
	 */
	public String getDesiredSequenceRnsStr() {
		return desiredSequenceRns.name();
	}

	/**
	 * Set the {@link ResidueNumberScheme} desired for the sequence
	 * @return
	 */
	public void setDesiredSequenceRns(ResidueNumberScheme desiredSequenceRns) {
		this.desiredSequenceRns = desiredSequenceRns;
	}

	/**
	 * Set the {@link ResidueNumberScheme} desired for the sequence from a <tt>String</tt>
	 * @return
	 */
	public void setDesiredSequenceRnsStr(String desiredSequenceRns) {
		setDesiredSequenceRns(ResidueNumberScheme.valueOf(desiredSequenceRns));
	}


	/**
	 * Get the {@link ResidueNumberScheme} desired for the ruler displayed above the sequence
	 * @return
	 */
	public ResidueNumberScheme getDesiredTopRulerRns() {
		return desiredTopRulerRns;
	}


	/**
	 * Get the {@link ResidueNumberScheme} desired for the ruler displayed above the sequence as a <tt>String</tt>
	 * @return
	 */
	public String getDesiredTopRulerRnsStr() {
		return desiredTopRulerRns.name();
	}

	/**
	 * Set the {@link ResidueNumberScheme} desired for the ruler displayed above the sequence
	 * @return
	 */
	public void setDesiredTopRulerRns(ResidueNumberScheme desiredTopRulerRns) {
		this.desiredTopRulerRns = desiredTopRulerRns;
		this.showDbRefRulerIfPossible = ResidueNumberScheme.DBREF == desiredTopRulerRns;
	}

	/**
	 * Set the {@link ResidueNumberScheme} desired for the ruler displayed above the sequence from a <tt>String</tt>
	 * @return
	 */
	public void setDesiredTopRulerRnsStr(String desiredTopRulerRns) {
		setDesiredTopRulerRns(ResidueNumberScheme.valueOf(desiredTopRulerRns));
	}

	/**
	 * Should a ruler be displayed above the sequence (in addition to below it)?
	 * @return
	 */
	public boolean isShowDbRefRulerIfPossible() {
		return showDbRefRulerIfPossible;
	}

	/**
	 * Should a ruler be displayed above the sequence (in addition to below it)?
	 * @param showDbRefRulerIfPossible
	 */
	public void setShowDbRefRulerIfPossible(boolean showDbRefRulerIfPossible) {
		this.showDbRefRulerIfPossible = showDbRefRulerIfPossible;
		this.desiredTopRulerRns = showDbRefRulerIfPossible ? ResidueNumberScheme.DBREF : null;
	}

	/**
	 * Get the 'fragment buffer'.  This is the size of the whitespace between sequence fragments in 
	 * a sequence image relative to the font size.
	 * @return
	 */
	public float getFragmentBuffer() {
		return fragmentBuffer;
	}

	/**
	 * Set the 'fragment buffer'.  This is the size of the whitespace between sequence fragments in 
	 * a sequence image relative to the font size.
	 * @param fragmentBuffer
	 */
	public void setFragmentBuffer(float fragmentBuffer) {
		this.fragmentBuffer = fragmentBuffer;
	}

	/**
	 * Should the next best annotation be used if a desired annotation is not available, or should 
	 * no annotation be displayed?
	 * @return <tt>true</tt> if the next best annotation should be found, <tt>false</tt> if nothing should replace a missing annotation
	 */
	public boolean isShowNextBestAnnotationIfPossible() {
		return showNextBestAnnotationIfPossible;
	}

	/**
	 * Should the next best annotation be used if a desired annotation is not available, or should 
	 * no annotation be displayed?
	 * @param showNextBestAnnotation <tt>true</tt> if the next best annotation should be found, <tt>false</tt> if nothing should replace a missing annotation
	 */
	public void setShowNextBestAnnotation(boolean showNextBestAnnotation) {
		this.showNextBestAnnotationIfPossible = showNextBestAnnotation;
	}

	public int getNumCharsInKey() {
		return numCharsInKey;
	}

	public void setNumCharsInKey(int numCharsInKey) {
		this.numCharsInKey = numCharsInKey;
	}

	@Override
	public String toString()
	{
		final String TAB = "; ";

		StringBuilder retValue = new StringBuilder();

		retValue.append("ViewParameters: ")
		.append("desiredTopRulerRns = ").append(this.desiredTopRulerRns).append(TAB)
		.append("desiredBottomRulerRns = ").append(this.desiredBottomRulerRns).append(TAB)
		.append("desiredSequenceRns = ").append(this.desiredSequenceRns).append(TAB)
		.append("annotationsToView = ").append(this.annotationsToView).append(TAB)
		.append("fontSize = ").append(this.fontSize).append(TAB)
		.append("fragmentLength = ").append(this.fragmentLength).append(TAB)
		.append("chainsPerPage = ").append(this.chainsPerPage).append(TAB)
		.append("chainEntityStrategy = ").append(this.chainEntityStrategy).append(TAB)
		.append("chainViewStrategy = ").append(this.chainViewStrategy).append(TAB)
		.append("chainSortStrategy = ").append(this.chainSortStrategy);

		return retValue.toString();
	}
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((chainEntityStrategy == null) ? 0 : chainEntityStrategy.hashCode());
		result = PRIME * result + ((chainSortStrategy == null) ? 0 : chainSortStrategy.hashCode());
		result = PRIME * result + ((chainViewStrategy == null) ? 0 : chainViewStrategy.hashCode());
		result = PRIME * result + chainsPerPage;
		result = PRIME * result + ((annotationsToView == null) ? 0 : annotationsToView.hashCode());
		result = PRIME * result + ((desiredBottomRulerRns == null) ? 0 : desiredBottomRulerRns.hashCode());
		result = PRIME * result + ((desiredSequenceRns == null) ? 0 : desiredSequenceRns.hashCode());
		result = PRIME * result + ((desiredTopRulerRns == null) ? 0 : desiredTopRulerRns.hashCode());
		result = PRIME * result + fontSize;
		result = PRIME * result + fragmentLength;
		result = PRIME * result + (showDbRefRulerIfPossible ? 1231 : 1237);
		result = PRIME * result + (showNextBestAnnotationIfPossible ? 1231 : 1237);
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
		final ViewParameters other = (ViewParameters) obj;
		if (chainEntityStrategy == null) {
			if (other.chainEntityStrategy != null)
				return false;
		} else if (!chainEntityStrategy.equals(other.chainEntityStrategy))
			return false;
		if (chainSortStrategy == null) {
			if (other.chainSortStrategy != null)
				return false;
		} else if (!chainSortStrategy.equals(other.chainSortStrategy))
			return false;
		if (chainViewStrategy == null) {
			if (other.chainViewStrategy != null)
				return false;
		} else if (!chainViewStrategy.equals(other.chainViewStrategy))
			return false;
		if (chainsPerPage != other.chainsPerPage)
			return false;
		if (annotationsToView == null) {
			if (other.annotationsToView != null)
				return false;
		} else if (!annotationsToView.equals(other.annotationsToView))
			return false;
		if (desiredBottomRulerRns == null) {
			if (other.desiredBottomRulerRns != null)
				return false;
		} else if (!desiredBottomRulerRns.equals(other.desiredBottomRulerRns))
			return false;
		if (desiredSequenceRns == null) {
			if (other.desiredSequenceRns != null)
				return false;
		} else if (!desiredSequenceRns.equals(other.desiredSequenceRns))
			return false;
		if (desiredTopRulerRns == null) {
			if (other.desiredTopRulerRns != null)
				return false;
		} else if (!desiredTopRulerRns.equals(other.desiredTopRulerRns))
			return false;
		if (fontSize != other.fontSize)
			return false;
		if (fragmentLength != other.fragmentLength)
			return false;
		if (showDbRefRulerIfPossible != other.showDbRefRulerIfPossible)
			return false;
		if (showNextBestAnnotationIfPossible != other.showNextBestAnnotationIfPossible)
			return false;
		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		ViewParameters cloned = (ViewParameters) super.clone();

		cloned.annotationsToView = new TreeSet<AnnotationName>(annotationsToView);
		cloned.disabledAnnotations = new TreeSet<AnnotationName>(disabledAnnotations);

		return cloned;
	}


}
