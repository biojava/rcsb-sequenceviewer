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

package org.rcsb.sequence.ptm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.biojava3.protmod.structure.ModifiedCompound;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ColorUtils;
import org.rcsb.sequence.view.html.ColorUtil;
import org.rcsb.sequence.view.multiline.AbstractAnnotationDrawer;
import org.rcsb.sequence.view.multiline.SequenceImage;



public class ModResDrawer extends AbstractAnnotationDrawer<ModifiedCompound> {

	// Calibrate the stroke size
	// (fontWidth of 8 gives a stroke size of 3)
	protected final float strokeSize;
	protected final double xPeriod;
	
	private static final double RELATIVE_HEIGHT = 1.0;
	
	//private SecondaryStructureValue previousSsv = SecondaryStructureValue.empty;
	private boolean previousHelixEndedWithCurveGoingUp = false;
	
	protected static final float STROKE_TO_FONT_WIDTH_RATIO = (3.0F/8.0F); 
	
	
	public ModResDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<ModifiedCompound>> annotationGroupClass) {
		this(image, sequence, annotationGroupClass, (int)((float)image.getFontHeight() * RELATIVE_HEIGHT));
	}

	public ModResDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<ModifiedCompound>> annotationGroupClass, int annotationHeight) {
		super(image, sequence, annotationGroupClass, (int)((float)image.getFontHeight() * RELATIVE_HEIGHT));

		final int fontWidth = image.getFontWidth();
		this.strokeSize = STROKE_TO_FONT_WIDTH_RATIO * fontWidth;
		this.xPeriod = fontWidth / 2;

	}
	 

	//   @Override
	//   protected void initBounds() {
	//      super.initBounds();
	//      
	//      
	//   }

	@Override
	protected boolean displayLabel() {
		return false;
	}

	//   @SuppressWarnings("cast")
	//   @Override
	//   protected int initImageHeight() {
	//      return ;
	//   }

	@Override
	public boolean canDrawAnnotation(AnnotationName anAnnotationName) {
		return anAnnotationName.getClassification() == AnnotationClassification.modres;
	}

	@Override
	protected void drawAnnotationFragment(Graphics2D g2,
			AnnotationValue<ModifiedCompound> annotation, int sequenceLength, int xMin, int yMin, int xMax,
			int yMax, boolean startIsNotStart, boolean endIsNotEnd) {

	
		
		// we will cast to the concrete class because it is an enum and we can switch on it
//		PTMValue ssv = (PTMValue)annotation; // we know this'll work because of canRenderAnnotation()... right?

		drawStrandFragment(g2, xMin, yMin, xMax, yMax, endIsNotEnd);
	}

	protected void drawStrandFragment(Graphics2D g2,
			int xMin, int yMin, int xMax, int yMax, boolean endIsNotEnd)
	{      
				
		// Change to default Strand color
		Color ssColor = Color.black;
		//g2.setColor(ssColor);

		//final int fontWidth = getImage().getFontWidth();
		//int fontWidth = getAnnotationHeight() - 4;
		int fontWidth = (yMax - yMin)/2 ;
		int yHeight = fontWidth;
		int yStart = (yMax - yMin)/2 - yHeight/2 + yMin;
		int yCenter = (yMax - yMin)/2 + yMin;
		int xWidth = xMax - xMin;

		// Create the head of the strand arrow
		Polygon arrowHead = new Polygon();
		arrowHead.addPoint(xMax - fontWidth, yCenter + yHeight);
		arrowHead.addPoint(xMax, yCenter);
		arrowHead.addPoint(xMax - fontWidth, yCenter - yHeight);

		Color light = ColorUtils.lighter(ssColor, 0.7);
		int lineHeight = yMax - yMin;
		Paint origPaint = g2.getPaint();
		GradientPaint gradient = new GradientPaint(0, yMin  ,  ssColor, 0 , yMax - lineHeight/2 , light, true);
		g2.setPaint(gradient); 

		Rectangle strandBody;
		Stroke stroke = new BasicStroke(1.0f);
		g2.setStroke(stroke);
		// Draw arrow head iff strand ends on current line
		if (endIsNotEnd) 
			strandBody = new Rectangle(xMin, yStart, xWidth, yHeight);
		
		else {
			strandBody = new Rectangle(xMin, yStart, xWidth - fontWidth, yHeight);
			g2.fill(arrowHead);
			g2.draw(arrowHead);
		}
	
		// 	Draw strand arrow body
		g2.fill(strandBody);
	
		
		g2.setColor(ssColor);
		
		g2.draw(strandBody);
		
	
	

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

	@Override
	protected void drawSpaceBetweenAnnotations(Graphics2D g2, int sequenceLength,
			int xMin, int yMin, int xMax, int yMax) {
		renderLine(g2, xMin, yMin, xMax, yMax);
	}

}
