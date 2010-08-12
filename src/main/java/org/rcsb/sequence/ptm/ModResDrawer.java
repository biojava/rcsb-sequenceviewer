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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	private Map<ProteinModification, Color> mapModColor = null;
	
	private Map<ModifiedCompound, List<Point>> crosslinkPositions;
	
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
		crosslinkPositions = new HashMap<ModifiedCompound, List<Point>>();
	}
	
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
		
		g2.setStroke(new BasicStroke(2));
		
		switch (mc.getModification().getCategory()) {
		case CROSS_LINK_1:
			drawCrossLink1(g2, xMin, yMin, xMax, yMax);
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
		
		if (mc.getModification().getCategory().isCrossLink()) {
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

	protected void drawModRes(Graphics2D g2, int xMin, int yMin, int xMax, int yMax)
	{
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		double radius = 0.4 * (yMax - yMin);
		
		Polygon polygon = getAsterisk(xCenter, yCenter, 6, radius, radius*0.2, 0);
		g2.fill(polygon);
	}
	
	protected Polygon getAsterisk(int xCenter, int yCenter, int n, double largeRadius, double smallRadius, double startAngle) {		
		double pie = 2 * Math.PI / n;
		
		int[] x = new int[3*n];
		int[] y = new int[3*n];
		
		for (int i=0; i<n; i++) {
			double angle = startAngle + i * pie;
			double theta1 = angle - pie / 4;
			x[3*i] = (int) (xCenter + smallRadius * Math.cos(theta1));
			y[3*i] = (int) (yCenter + smallRadius * Math.sin(theta1));
			
			x[3*i+1] = (int) (xCenter + largeRadius * Math.cos(theta1));
			y[3*i+1] = (int) (yCenter + largeRadius * Math.sin(theta1));
			
			double theta2 = angle + pie / 4;
			x[3*i+2] = (int) (xCenter + largeRadius * Math.cos(theta2));
			y[3*i+2] = (int) (yCenter + largeRadius * Math.sin(theta2));
		}
		
		return new Polygon(x, y, 3*n);
	}
	
	protected void drawCrossLink1(Graphics2D g2, int xMin, int yMin, int xMax, int yMax)
	{
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		
		int radius = (yMax - yMin) / 4;
		
		g2.drawOval(xCenter-radius, yCenter-radius, 2*radius, 2*radius);
	}
	
	protected void drawCrossLink(Graphics2D g2, int xMin, int yMin, int xMax, int yMax, int nRes)
	{
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		double radius = (yMax - yMin) / 2.0;
		
		Polygon polygon = getPolygon(xCenter, yCenter, radius, nRes, Math.PI/2);
		g2.drawPolygon(polygon);
	}
	
	private Polygon getPolygon(int xCenter, int yCenter, double radius, int nPoint, double startAngle) {
		if (nPoint==1)
			return new Polygon(new int[]{xCenter}, new int[]{yCenter}, 1);
		
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
