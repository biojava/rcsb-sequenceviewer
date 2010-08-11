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
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.biojava3.protmod.ProteinModification;
import org.biojava3.protmod.structure.ModifiedCompound;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.multiline.AbstractAnnotationDrawer;
import org.rcsb.sequence.view.multiline.SequenceImage;



public class ModResDrawer extends AbstractAnnotationDrawer<ModifiedCompound> {

	// Calibrate the stroke size
	// (fontWidth of 8 gives a stroke size of 3)
	protected final float strokeSize;
	protected final double xPeriod;
	
	private static final double RELATIVE_HEIGHT = 1.0;
	
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
	
	private Map<ProteinModification, Color> mapModColor = null;
	
	public void setMapCrossLinkColor(Map<ProteinModification, Color> mapModColor) {
		this.mapModColor = mapModColor;
	}

	@Override
	protected boolean displayLabel() {
		return false;
	}

	@Override
	public boolean canDrawAnnotation(AnnotationName anAnnotationName) {
		return anAnnotationName.getClassification() == AnnotationClassification.modres;
	}

	@Override
	protected void drawAnnotationFragment(Graphics2D g2,
			AnnotationValue<ModifiedCompound> annotation, int sequenceLength, int xMin, int yMin, int xMax,
			int yMax, boolean startIsNotStart, boolean endIsNotEnd) {
		ModifiedCompound mc = annotation.value();
		
		Color color = null;
		if (mapModColor!=null ) {
			color = mapModColor.get(mc.getModification());
		}
		
		if (color==null)
			color = Color.red;
		
		g2.setColor(color);
		
		switch (mc.getModification().getCategory()) {
		case CROSS_LINK_1:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 1);
			break;
		case CROSS_LINK_2:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 2);
			break;
		case CROSS_LINK_3:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 3);
			break;
		case CROSS_LINK_4:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 4);
			break;
		case CROSS_LINK_5:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 5);
			break;
		case CROSS_LINK_6:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 6);
			break;
		case CROSS_LINK_7:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 7);
			break;
		default:
			drawModRes(g2, xMin, yMin, xMax, yMax);
		}
	}

	protected void drawModRes(Graphics2D g2, int xMin, int yMin, int xMax, int yMax)
	{
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		double radius = (yMax - yMin) / 2.0;
		
		Polygon polygon = getPolygon(xCenter, yCenter, radius, 3, Math.PI/2);
		g2.fill(polygon);
		g2.draw(polygon);
	}
	
	protected void drawCrossLink(Graphics2D g2, int xMin, int yMin, int xMax, int yMax, int nRes)
	{
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		double radius = (yMax - yMin) / 2.0;
		
		Polygon polygon = getPolygon(xCenter, yCenter, radius, nRes);
//		g2.fill(polygon);
		g2.drawPolygon(polygon);
	}
	
	private Polygon getPolygon(int xCenter, int yCenter, double radius, int nPoint) {
		return getPolygon(xCenter, yCenter, radius, nPoint, Double.NaN);
	}
	
	private Polygon getPolygon(int xCenter, int yCenter, double radius, int nPoint, double startAngle) {
		if (nPoint==1)
			return new Polygon(new int[]{xCenter}, new int[]{yCenter}, 1);
		
		if (Double.isNaN(startAngle))
			startAngle = nPoint%2==0 ? Math.PI/nPoint : -Math.PI/2;
		
		int[] x = new int[nPoint];
		int[] y = new int[nPoint];
		
		for (int i=0; i<nPoint; i++) {
			double angle = startAngle + i * 2 * Math.PI / nPoint;
			x[i] = (int) (xCenter + radius * Math.cos(angle));
			y[i] = (int) (yCenter + radius * Math.sin(angle));
		}
		
		return new Polygon(x, y, nPoint);
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
}
