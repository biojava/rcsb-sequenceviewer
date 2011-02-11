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
 * Created on Aug 26, 2010
 * Author: Jianjiong Gao 
 *
 */

package org.rcsb.sequence.view.multiline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;


import java.util.List;

import java.util.Set;

import org.biojava3.core.util.SoftHashMap;
import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ProteinModification;
import org.rcsb.sequence.util.ColorUtils;

public class ProtModDrawerUtil {


	static SoftHashMap<ProteinModification,Color> cache = new SoftHashMap<ProteinModification, Color>(0);

	private float relativeThickness;
	private int yBendOffset;

	private static int colorCount = -1; 

	ProtModDrawerUtil() {

		this.relativeThickness = 2;
		this.yBendOffset = 5;
	}

	void setCrosslinkLineThickness(float relativeThickness) {
		this.relativeThickness = relativeThickness;
	}

	float getCrosslinkLineThickness() {
		return relativeThickness;
	}

	void setCrosslinkLineBendOffset(int yBendOffset) {
		this.yBendOffset = yBendOffset;
	}

	int getCrosslinkLineBendOffset() {
		return yBendOffset;
	}



	void drawProtMod(Graphics2D g2,
			ProteinModification mod,
			int xMin, int yMin, int xMax, int yMax) {

		Color color = getColor(mod);

		g2.setColor(color);

		g2.setStroke(new BasicStroke(2));

		switch (mod.getCategory()) {
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
		case CROSS_LINK_8_OR_LARGE:
			drawCrossLink(g2, xMin, yMin, xMax, yMax, 8);
			break;
		default:
			drawModRes(g2, xMin, yMin, xMax, yMax);
		}
	}

	public static Color getColor(	ProteinModification mod) {

		Color c = (Color) cache.get(mod);
		if ( c != null)
			return c;



		Color color = getMapModColor(mod);

		if (color==null)
			color = Color.blue;

		return color;
	}

	private void drawModRes(Graphics2D g2, int xMin, int yMin, int xMax, int yMax)
	{
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		double radius = 0.4 * (yMax - yMin);

		Polygon polygon = getAsterisk(xCenter, yCenter, 6, radius, radius*0.2, 0);
		g2.fill(polygon);

		Color c = g2.getColor();		
		Color tmp = ColorUtils.darker(c, 0.3);		
		g2.setColor(tmp);		
		g2.draw(polygon);
		g2.setColor(c);
	}

	private Polygon getAsterisk(int xCenter, int yCenter, int n, double largeRadius, double smallRadius, double startAngle) {		
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

	private void drawCrossLink1(Graphics2D g2, int xMin, int yMin, int xMax, int yMax) {
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;

		int radius = (yMax - yMin) / 4;

		g2.drawOval(xCenter-radius, yCenter-radius, 2*radius, 2*radius);

		Color c = g2.getColor();		
		Color tmp = ColorUtils.darker(c, 0.3);		
		g2.setColor(tmp);
		g2.setStroke(new BasicStroke(1));
		g2.drawOval(xCenter-radius, yCenter-radius, 2*radius, 2*radius);
		g2.setStroke(new BasicStroke(2));
		g2.setColor(c);
	}

	private void drawCrossLink(Graphics2D g2, int xMin, int yMin, int xMax, int yMax, int nRes) {
		int xCenter = (xMin + xMax) / 2;
		int yCenter = (yMin + yMax) / 2;
		double radius = (yMax - yMin) / 2.0;

		Polygon polygon = getPolygon(xCenter, yCenter, radius, nRes, Math.PI/2);

		g2.drawPolygon(polygon);

		Color c = g2.getColor();		
		Color tmp = ColorUtils.darker(c, 0.3);		
		g2.setColor(tmp);
		g2.setStroke(new BasicStroke(1));
		g2.drawPolygon(polygon);
		g2.setStroke(new BasicStroke(2));
		g2.setColor(c);


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

	void drawCrosslinks(Graphics2D g2,  ProteinModification mod,
			List<Point> points)
	{


		Point pa, pb;
		Shape bond;

		int prevYPos = 0, yBend = 0;
		boolean lineGoesAbove = true;

		//				setDashed(g2, relativeThickness, mod);

		Color color = getColor(mod);
		g2.setColor(color);

		int n = points.size();

		for (int i=0; i<n-1; i++) {
			pa = points.get(i);
			pb = points.get(i+1);;

			int y1, y2;

			// if both cysteines are on the same line we need to
			// a. decide (based on if there are other disulphides on the same line)
			// whether to put the connecting line above or below the sequence
			// b. nudge the points accordingly
			if (pa.y == pb.y)
			{
				if (lineGoesAbove)
				{
					yBend = -1 * yBendOffset;
				}
				else
				{
					yBend = yBendOffset;
				}
				y1 = pa.y;
				y2 = y1 + yBend;

				bond = new CubicCurve2D.Double(pa.x, y1, pa.x, y2, pb.x, y2, pb.x, y1);

				lineGoesAbove = pa.y == prevYPos && !lineGoesAbove; // invert for next time on same line
				prevYPos = pa.y;
			}
			else
			{
				bond = new Line2D.Double(pa.x, pa.y, pb.x, pb.y);
			}

			ModificationCategory cat = mod.getCategory();

			if (cat.isCrossLink() && cat!=ModificationCategory.CROSS_LINK_1 ) {
				g2.draw(bond);

				Color c = g2.getColor();		
				Color tmp = ColorUtils.darker(c, 0.3);		
				g2.setColor(tmp);
				g2.setStroke(new BasicStroke(1));
				g2.draw(bond);
				g2.setStroke(new BasicStroke(2));
				g2.setColor(c);
			}

		}

	}

	private void setDashed(Graphics2D g2, float relativeThickness, ProteinModification mod) {
		//		final float relativeThickness = getFontSize() * RELATIVE_DISULPHIDE_LINE_THICKNESS;
		float[] dashed;
		switch (mod.getCategory()) {
		case CROSS_LINK_2:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			};
			break;
		case CROSS_LINK_3:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			}
			;
			break;
		case CROSS_LINK_4:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			}
			;
			break;
		case CROSS_LINK_5:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			}
			;
			break;
		case CROSS_LINK_6:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			}
			;
			break;
		case CROSS_LINK_7:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			}
			;
			break;
		case CROSS_LINK_8_OR_LARGE:
			dashed = new float[] {
					relativeThickness*4, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness, 
					relativeThickness, relativeThickness
			}
			;
			break;
		default:
			return;
		}

		BasicStroke dashedStroke = new BasicStroke(relativeThickness, BasicStroke.CAP_BUTT, 
				BasicStroke.JOIN_MITER, 10.0f, dashed, 0.0f);
		g2.setStroke(dashedStroke);

	}

	private static Color getNextColor(){
		colorCount++;
		Color color = colors[colorCount%colors.length];
		if ( colorCount >= colors.length)
			colorCount = -1;

		return color;
	}



	private static Color getMapModColor( ProteinModification mymod) {
		if (mymod == null)
			return null;

		Color c = cache.get(mymod);
		if ( c != null) {
			return c;
		}

		if ( mymod.getCategory() == ModificationCategory.CROSS_LINK_2 ) {
			Set<String>kws = mymod.getKeywords();
			for (String kw : kws){
				if  ( kw.startsWith("disulfide")){
					c = ColorUtils.orange;
					cache.put(mymod, c);
					return c;
				}
			}
		}


		c = getNextColor();
		cache.put(mymod,c);

		return c;

	}



	public static final Color[] colors = ColorUtils.colorWheel; 


}
