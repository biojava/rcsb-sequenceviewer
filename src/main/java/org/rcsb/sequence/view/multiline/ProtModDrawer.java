/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Aug 9, 2010
 * Author: Jianjiong Gao 
 *
 */

package org.rcsb.sequence.view.multiline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biojava3.protmod.ProteinModification;
import org.biojava3.protmod.structure.ModifiedCompound;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;



public class ProtModDrawer extends AbstractAnnotationDrawer<ModifiedCompound> {

	// Calibrate the stroke size
	// (fontWidth of 8 gives a stroke size of 3)
	protected final float strokeSize;
	protected final double xPeriod;
	
	private ProtModDrawerUtil modDrawerUtil = null;
	
	private Map<ModifiedCompound, List<Point>> crosslinkPositions;
	
	private static final double RELATIVE_HEIGHT = 1.0;
	
	protected static final float STROKE_TO_FONT_WIDTH_RATIO = (3.0F/8.0F); 
	
	Set<ProteinModification> protMods = null;
	
	public ProtModDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<ModifiedCompound>> annotationGroupClass) {
		this(image, sequence, annotationGroupClass, (int)((float)image.getFontHeight() * RELATIVE_HEIGHT));
	}

	public ProtModDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<ModifiedCompound>> annotationGroupClass, int annotationHeight) {
		super(image, sequence, annotationGroupClass, (int)((float)image.getFontHeight() * RELATIVE_HEIGHT));

		final int fontWidth = image.getFontWidth();
		this.strokeSize = STROKE_TO_FONT_WIDTH_RATIO * fontWidth;
		this.xPeriod = fontWidth / 2;
		crosslinkPositions = new HashMap<ModifiedCompound, List<Point>>();
	}
	
	public void setModDrawerUtil(ProtModDrawerUtil modDrawerUtil) {
		this.modDrawerUtil = modDrawerUtil;
	}

	@Override
	protected boolean displayLabel() {
		return true;
	}

	@Override
	public boolean canDrawAnnotation(AnnotationName anAnnotationName) {
		return anAnnotationName.getClassification() == AnnotationClassification.structuralFeature;
	}

	@Override
	protected void drawAnnotationFragment(Graphics2D g2,
			AnnotationValue<ModifiedCompound> annotation, int sequenceLength, int xMin, int yMin, int xMax,
			int yMax, boolean startIsNotStart, boolean endIsNotEnd) {
		
		ModifiedCompound mc = annotation.value();
		
		if (modDrawerUtil==null)
			modDrawerUtil = new ProtModDrawerUtil();
		
		
		ProteinModification mod = mc.getModification();
		
		modDrawerUtil.drawProtMod(g2,  mod, xMin, yMin, xMax, yMax);
		
		if (mod.getCategory().isCrossLink()) {
		
			List<Point> points = crosslinkPositions.get(mc);
			
			if (points==null) {
			
				points = new ArrayList<Point>();
			
				crosslinkPositions.put(mc, points);
		
			}
			
			points.add(new Point((xMin+xMax)/2,(yMin+yMax)/2));
		}
	}
	
	public Map<ModifiedCompound, List<Point>> getCrosslinkPositions() {
		return crosslinkPositions;
	}

	@Override
	protected void drawSpaceBetweenAnnotations(Graphics2D g2, int sequenceLength,
			int xMin, int yMin, int xMax, int yMax) {
		renderLine(g2, xMin, yMin, xMax, yMax);
	}

	private void renderLine(Graphics2D g2,
			int xMin, int yMin, int xMax, int yMax)
	{
		//    Change to default NoSS color
		g2.setColor(Color.black);

		int yHeight = getImage().getFontWidth() / 4;
		int yStart = (yMax - yMin)/2 + yMin - yHeight/2;
		int xWidth = xMax - xMin;

		// Draw a line from xMin to xMax
		Rectangle rect = new Rectangle(xMin, yStart, xWidth, yHeight);
		g2.fill(rect);
	}

	public Set<ProteinModification> getProtMods() {
		return protMods;
	}

	public void setProtMods(Set<ProteinModification> protMods) {
		this.protMods = protMods;
	}
	
	public boolean canDrawAnnotationsThatOverlap() {
		return false;
	}
	
	
}
