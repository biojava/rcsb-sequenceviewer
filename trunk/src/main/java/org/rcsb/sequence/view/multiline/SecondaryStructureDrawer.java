package org.rcsb.sequence.view.multiline;

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


import org.rcsb.sequence.annotations.SecondaryStructureType;
import org.rcsb.sequence.annotations.SecondaryStructureValue;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ColorWheelUtil;
import org.rcsb.sequence.util.ColorUtils;



public class SecondaryStructureDrawer extends AbstractAnnotationDrawer<String> {

	// Calibrate the stroke size
	// (fontWidth of 8 gives a stroke size of 3)
	protected final float strokeSize;
	protected final double xPeriod;
	
	private static final double RELATIVE_HEIGHT = 1.8;
	
	private SecondaryStructureType previousSsv = SecondaryStructureType.empty;
	private boolean previousHelixEndedWithCurveGoingUp = false;
	
	protected static final float STROKE_TO_FONT_WIDTH_RATIO = (3.0F/8.0F); 
	
	
	public SecondaryStructureDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<String>> annotationGroupClass) {
		this(image, sequence, annotationGroupClass, (int)((float)image.getFontHeight() * RELATIVE_HEIGHT));
	}

	public SecondaryStructureDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<String>> annotationGroupClass, int annotationHeight) {
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
		return anAnnotationName.getClassification() == AnnotationClassification.secstr;
	}

	@Override
	protected void drawAnnotationFragment(Graphics2D g2,
			AnnotationValue<String> annotation, int sequenceLength, int xMin, int yMin, int xMax,
			int yMax, boolean startIsNotStart, boolean endIsNotEnd) {

	
		
		// we will cast to the concrete class because it is an enum and we can switch on it
		SecondaryStructureValue ssvAnno = (SecondaryStructureValue)annotation; // we know this'll work because of canRenderAnnotation()... right?

		SecondaryStructureType ssv = ssvAnno.getType();
		
		// draw connecting line if previous fragment was helical and this one isn't
		if(SecondaryStructureType.isHelical(previousSsv) && ! SecondaryStructureType.isHelical(ssv))
		{
			renderLine(g2, previousSsv, xMin - getImage().getFontWidth()/2 +1, yMin, xMin, yMax);
		}

		switch(ssv)
		{
		case G:
		case H:
		case I:
			drawHelixFragment(g2, ssv, sequenceLength, xMin, yMin, xMax, yMax, startIsNotStart, endIsNotEnd, true);
			break;
		case E:
		case B:
			drawStrandFragment(g2, ssv, xMin, yMin, xMax, yMax, endIsNotEnd);
			break;
		case T:
			drawTurnFragment(g2, ssv, sequenceLength, xMin, yMin, xMax, yMax, startIsNotStart, endIsNotEnd);
			break;
		case S:
		case empty:
			drawNoSSFragment(g2, ssv, xMin, yMin, xMax, yMax);
			break;
		case error:
		default:
			throw new RuntimeException("Problem with SecondaryStructureValue: " + ssv);
		}

		setPreviousSsv(ssv);
	}

	private void setPreviousSsv(SecondaryStructureType ssv)
	{
		previousSsv = ssv;
		if(! SecondaryStructureType.isHelical(ssv)) previousHelixEndedWithCurveGoingUp = false;      
	}

	protected void drawHelixFragment(Graphics2D g2,
			SecondaryStructureType ssv, int sequenceLength, 
			int xMin, int yMin, int xMax, int yMax,
			boolean startIsNotStart, boolean endIsNotEnd, boolean makeAbuttingHelicesLookContiguous)
	{
		final int fontWidth = getImage().getFontWidth();

		boolean curveGoesUp = !previousHelixEndedWithCurveGoingUp;

		// Parameters for Helix Bezier curve
		double xStart = xMin + xPeriod;
		final double yExtent = (double) (yMax - yMin)/2;
		final double yStart = yExtent + yMin;

		// If Helix does not end on this line,
		// then draw it for an extra residue to the right.
		// This gives the illusion that the helix connects to the next line.
		if (endIsNotEnd) 
		{
			sequenceLength++;
		}

		// If Helix starts at the beginning of this line,
		// then draw it for an extra residue to the left.
		// This gives the illusion that the helix connects to the previous line.
		if (startIsNotStart) 
		{
			xStart -= fontWidth;
			sequenceLength++;
		}

		if(makeAbuttingHelicesLookContiguous && SecondaryStructureType.isHelical(previousSsv))
		{
			// we know that there is at least one residue's worth of space before xMin.

			// we also know whether the previous helix ended up or down.
			// if(previousHelixEndedEarly == true) we know it finished by coming up.

			// so rather than having a flat section between the two helix types, let's have 
			// a contiguous helix.

			// clear one half of a resiude (i.e. the flat line to the residue boundary)
			// ...

			// nudge the start back one
			xStart -= fontWidth;
			sequenceLength++;
		}
		else
		{
			renderLine(g2, ssv, xMin, yMin, (int)(xStart + (strokeSize/2)), yMax);
		}

		// Initialize the first Bezier curve of the helix
		//		  CubicCurve2D.Double helix  = new CubicCurve2D.Double(xStart, yStart, xStart + xPeriod/2, yMax + xPeriod, xStart + xPeriod/2, yMin - xPeriod, xStart + xPeriod, yStart);

		g2.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

		Color helixColor = SST_TO_COLOR_MAP.get(ssv);
		g2.setColor(helixColor);

		Shape helix;

		final double yTrough = yMax + yExtent/3;
		final double yPeak   = yMin - yExtent/3;

		Color light = ColorUtils.lighter(helixColor, 0.7);
		int lineHeight = yMax - yMin;
		Paint origPaint = g2.getPaint();
		GradientPaint gradient = new GradientPaint(0, yMin  ,  helixColor, 0 , yMax - lineHeight/2 , light,true);
		g2.setPaint(gradient); 
		
		for(int i = 0; i < sequenceLength - 1; i++)
		{
			helix = new QuadCurve2D.Double(xStart, yStart, xStart + xPeriod, curveGoesUp ? yTrough : yPeak, xStart + fontWidth, yStart);
			g2.draw(helix);
			xStart += fontWidth;
			curveGoesUp = !curveGoesUp;
		}

		previousHelixEndedWithCurveGoingUp = !curveGoesUp;

		// this line tidies up the capping at the end of a helix ... it's slightly hacky.
		float bitOfStrokeWidth = strokeSize/3;
		renderLine(g2, ssv, (int)(xStart - bitOfStrokeWidth), yMin, (int)(xStart + bitOfStrokeWidth), yMax);
	}

	protected void drawStrandFragment(Graphics2D g2,
			SecondaryStructureType ssv,
			int xMin, int yMin, int xMax, int yMax, boolean endIsNotEnd)
	{      
				
		// Change to default Strand color
		Color ssColor = SST_TO_COLOR_MAP.get(ssv) ;
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

	protected void drawTurnFragment(Graphics2D g2,
			SecondaryStructureType ssv, int sequenceLength,
			int xMin, int yMin, int xMax, int yMax,
			boolean startIsNotStart, boolean endIsNotEnd)
	{
		final int fontWidth = getImage().getFontWidth();

		// if the turn is only one residue long, that is silly. just render a black line and be done with it
		if(!(startIsNotStart || endIsNotEnd) && sequenceLength == 1)
		{
			drawNoSSFragment(g2, SecondaryStructureType.empty, xMin, yMin, xMax, yMax);
			return;
		}

		// Change to default helix colors
		g2.setColor(SST_TO_COLOR_MAP.get(ssv));

		// Calibrate the stroke size
		// (fontWidth of 8 gives a stroke size of 3)
		float strokeSize = (3.0F/8.0F) * fontWidth;
		g2.setStroke(new BasicStroke(strokeSize));

		// Parameters for Turn Bezier curve
		double xStart = xMin + fontWidth / 2;
		double yStart = (double) (yMax - yMin)/2 + yMin;
		double xEnd = xStart + fontWidth * (sequenceLength - 1);

		// If turn ends or begins a line, 
		// then double its length and only display half of the curve.
		if (startIsNotStart) {
			xStart = xMin + fontWidth / 2 - fontWidth * sequenceLength * 2;
		}
		if (endIsNotEnd) {
			xEnd = xStart + fontWidth * sequenceLength * 2;
		}

		// Initialize and draw the Turn Bezier curve
		CubicCurve2D.Double turn  = new CubicCurve2D.Double(xStart, yStart, xStart, yMin, xEnd, yMin, xEnd, yStart);
		g2.draw(turn);

		// Parameters for lines connecting to adjacent fragments
		int lineHeight = fontWidth / 4;
		int lineStart = (yMax - yMin)/2 + yMin - lineHeight/2;
		int lineWidth = fontWidth / 2 + fontWidth / 4;	

		// Draw lines to connect turn to adjacent fragments
		Rectangle connectingLineOne = new Rectangle(xMin, lineStart, lineWidth - lineHeight, lineHeight);
		Rectangle connectingLineTwo = new Rectangle(xMax - fontWidth/2, lineStart, fontWidth/2, lineHeight);

		if (!startIsNotStart) {
			g2.fill(connectingLineOne);
		}
		if (!endIsNotEnd) {
			g2.fill(connectingLineTwo);	
		}

		// Draw a white box under the Turn Bezier curve
		// to ensure smooth transition to connecting lines.
		Rectangle whiteBoxOne = new Rectangle(xMin, lineStart+lineHeight, xMax-xMin, lineHeight);
		g2.setColor(Color.white);
		g2.fill(whiteBoxOne);

	}

	protected void drawNoSSFragment(Graphics2D g2,
			SecondaryStructureType ssv,
			int xMin, int yMin, int xMax, int yMax)
	{
		renderLine(g2, ssv, xMin, yMin, xMax, yMax);
	}

	private void renderLine(Graphics2D g2,
			SecondaryStructureType ssv,
			int xMin, int yMin, int xMax, int yMax)
	{
		Color c = SST_TO_COLOR_MAP.get(ssv);
		if(c == null) c = Color.BLACK;

		//    Change to default NoSS color
		g2.setColor(c);

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
		// blank previous ssv
		previousSsv = null;

		// draw nothing
	}

	//
	// AP: getting the colors changed to use the ones from ColoUtils.java

	public static final Map<SecondaryStructureType, Color> SST_TO_COLOR_MAP;
	static
	{
		Map<SecondaryStructureType, Color> foo = new HashMap<SecondaryStructureType, Color>();
		foo.put(SecondaryStructureType.G, ColorWheelUtil.pdbRED.brighter().brighter());
		foo.put(SecondaryStructureType.H, ColorWheelUtil.pdbRED);
		foo.put(SecondaryStructureType.I, Color.red.darker().darker());

		foo.put(SecondaryStructureType.E, ColorWheelUtil.pdbYELLOW);
		foo.put(SecondaryStructureType.B, ColorWheelUtil.pdbYELLOW.darker().darker());

		foo.put(SecondaryStructureType.T, ColorWheelUtil.pdbPURPLE);

		foo.put(SecondaryStructureType.empty, Color.black);
		foo.put(SecondaryStructureType.S, Color.black);

		foo.put(SecondaryStructureType.error, Color.pink);
		SST_TO_COLOR_MAP = Collections.unmodifiableMap(foo);
	}

}
