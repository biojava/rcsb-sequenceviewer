package org.rcsb.sequence.view.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ColorUtils;
import org.rcsb.sequence.util.ResourceManager;
import org.rcsb.sequence.view.html.ColorUtil;

public class BoxAnnotationDrawer<T> extends AbstractAnnotationDrawer<T> {

	protected static final int NUM_PEAKS ;
	
	static {
		ResourceManager rm = new ResourceManager("sequenceview");
		NUM_PEAKS = Integer.parseInt(rm.getString("boxannotationdrawer.nrPeaks"));
	}
	
	public BoxAnnotationDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<T>> annotationGroupClass) {
		super(image, sequence, annotationGroupClass, (int) (image.getFontHeight() * 1.2));
	}

	@Override
	protected void drawAnnotationFragment(Graphics2D g2, AnnotationValue<T> annotation, 
			int sequenceLength, int xMin, int yMin, int xMax, int yMax, boolean startIsNotStart, boolean endIsNotEnd) 
	{
		
		//PdbLogger.warn("drawing annotation " + annotation);
		
		
		
		g2.setColor( ColorUtil.getArbitraryColor(annotation) );

		g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	
		Composite comp = g2.getComposite();
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));

		int yScaler = (yMax - yMin) / 6;
		yMin += yScaler;
		yMax -= yScaler;

		final int fontWidth = getImage().getFontWidth();
		final int rectXmin = startIsNotStart ? xMin + fontWidth/2 : xMin;
		final int rectXmax = endIsNotEnd     ? xMax - fontWidth/2 : xMax;

		Color coreColor = ColorUtil.getArbitraryColor(annotation);
		Color light     = ColorUtils.lighter(coreColor, 0.7);
		
		int lineHeight = yMax - yMin;

		Paint origPaint = g2.getPaint();
		GradientPaint gradient = new GradientPaint(0, yMin  ,  light, 0 , yMax - lineHeight/2 , coreColor,true);
		g2.setPaint(gradient); 
		
		RoundRectangle2D rec = new RoundRectangle2D.Float(rectXmin, yMin, rectXmax - rectXmin, yMax - yMin,fontWidth,fontWidth);

		g2.fill(rec);
		
		g2.setPaint(origPaint);
		g2.setColor(coreColor);
		//g2.drawLine(rectXmin, yMin, rectXmax, yMin);
		//g2.drawRoundRect(rectXmin, yMin, rectXmax - rectXmin, yMax - yMin,fontWidth,fontWidth);
		
		Stroke stroke = new BasicStroke(1.0f);
		g2.setStroke(stroke);
		g2.draw(rec);
		g2.setPaint(gradient);
		
		// make sure the RoundRect does not overlap the Truncated symbol...
		if(startIsNotStart)
		{
				
			Polygon trunk = generateTruncatedSymbol(xMin, yMin, xMax, yMax, true,  NUM_PEAKS, fontWidth) ;
			g2.fillPolygon( trunk);
			
			
			
		}
		if(endIsNotEnd)
		{
			
			g2.fillPolygon( generateTruncatedSymbol(xMin, yMin, xMax, yMax, false, NUM_PEAKS, fontWidth) );
		}

		g2.setComposite(comp);
	}

	

	protected Polygon generateTruncatedSymbol(int xMin, final int yMin, int xMax, 
			final int yMax, final boolean leading, int numPeaks, final int fontWidth)
	{
		
		
		/*
		 * SHAPE IS LIKE THIS:
		 * 
		 * ____
		 * |  /
		 * |  \
		 * |  /
		 * |  \
		 * ----
		 * 
		 * OR INVERTED IN X AXIS.
		 */
		Polygon result = new Polygon();

		// set up positions of things

		// numPeaks should be odd so that we can start and end on a peak
		if(numPeaks % 2 == 0) ++numPeaks;


		// modify x bounds depending on whether we're rendering a leading or trailing jaggy.
		// and also make the variables that describe the x position of things
		final int flatSideX, roughSideXpeak;
		if(leading)
		{
			xMax = xMin + fontWidth;
			flatSideX = xMax;
			roughSideXpeak = xMin;
		}
		else
		{
			xMin = xMax - fontWidth;
			flatSideX = xMin;
			roughSideXpeak = xMax;
		}

		final int roughSideXtrough = (xMin + xMax) / 2; // trough is always at midpoint
		final float peakDist = ((float)(yMax - yMin)) / (float)(numPeaks - 1);  // this is the distance between a peak and a trough


		// draw the shape

		// 1. flat side
		result.addPoint(flatSideX, yMax);
		result.addPoint(flatSideX, yMin);

		// 2. rough side
		int curX = -1;
		float curY = yMin;
		for(int i = 0; i < numPeaks; i++)
		{
			curX = i % 2 == 0 ? roughSideXpeak : roughSideXtrough;
			result.addPoint(curX, Math.round(curY));
			curY += peakDist;
		}
		assert curX == roughSideXpeak : "Because we ensured numPeaks was odd earlier, curX should be at a peak now";

		// 3. join the last point of the rough side to the first point of the flat side
		result.addPoint(flatSideX, yMax);

		return result;
	}

	@Override
	protected void drawSpaceBetweenAnnotations(Graphics2D g2, int sequenceLength, int xMin, int yMin, int xMax, int yMax) {
		// do nothing
	}

	@Override
	protected boolean displayLabel() {
		return false;
	}

	@Override
	public boolean canDrawAnnotationsThatOverlap() {
		return true;
	}

}
