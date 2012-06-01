package org.rcsb.sequence.view.multiline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ProteinModification;
import org.rcsb.sequence.annotations.SecondaryStructureType;
import org.rcsb.sequence.annotations.SecondaryStructureValue;
import org.rcsb.sequence.util.AnnotationConstants;


public class SecStrucLegendDrawer implements Drawer {

	private Font font;
	private final int imageWidth;
	private int totalHeight;
	private static final int legendHeight = 25;
	private static final int legendOffset = 50;
	private static final int legendSpacing = 20;
	private static final int LEGEND_TEXT_SPACER = 10;
	Set<String> secStruc ;
	String annotationName ;
	private static final int some_factor = 6;
	private Map<String, List<TextLayout>> multiLineText;

	public SecStrucLegendDrawer(Font font,
			final int imageWidth, Set<String> secStruc, String annotationName){
		this.imageWidth = imageWidth;
		this.font = font;
		this.secStruc = secStruc;
		this.annotationName = annotationName;

		setMultiLineText();
	}

	@Override
	public int getImageHeightPx() {
		// TODO Auto-generated method stub
		return totalHeight;
	}

	@Override
	public void draw(Graphics2D g2, int yOffset) {

		int height = legendHeight;
		Color c = g2.getColor();
		g2.setColor(Color.black);
		g2.setFont(font);
		g2.drawString(annotationName + " Legend", legendOffset, yOffset+legendSpacing);
		g2.setColor(c);

		if ( multiLineText == null)
			setMultiLineText();

		int fontSize = font.getSize();
		int fontWidth = fontSize;
		double xPeriod = fontWidth / 2;
		float strokeSize = SecondaryStructureDrawer.STROKE_TO_FONT_WIDTH_RATIO * fontSize;
		
		int xMin = 0;
		
		double xStart = xMin + xPeriod;
		double xMax = fontSize * 5;
		
		boolean curveGoesUp = false;
		
		for (String sec : secStruc) {
			
			int yMin = yOffset + height;
			int yMax = yOffset + legendHeight + height;
			final double yExtent = (double) (yMax - yMin)/2;
			final double yStart = yExtent + yMin;
			
			List<TextLayout> textLayouts = multiLineText.get(sec);

			float lineHeight = textLayouts.get(0).getAscent() 
					+ textLayouts.get(0).getDescent() + textLayouts.get(0).getLeading();
			int yMid = yOffset + height + (int)lineHeight/2;

			SecondaryStructureType ssv = SecondaryStructureType.getTypeFromCharCode(sec.charAt(0)); 
			

			if ( sec.startsWith("H") || sec.startsWith("G") || sec.startsWith("I")) {
				
				SecondaryStructureDrawer.drawHelix(g2, strokeSize, xStart, xPeriod, yStart, yMin, yMax, yExtent, 4, ssv, fontWidth, curveGoesUp);
			} else if (sec.startsWith("E") || sec.startsWith("B")){
				SecondaryStructureDrawer.drawStrandFragment(g2, ssv, xMin, yMin, (int)xMax, yMax, false);
			} else if ( sec.startsWith("T")){
				SecondaryStructureDrawer.drawTurnFragment(g2, ssv, 2, xMin, yMin, (int)xMax, yMax, false, true, fontWidth);
			} else if ( sec.startsWith(" ") || sec.startsWith("S")){
				int yHeight = fontWidth / 4;
				SecondaryStructureDrawer.renderLine(g2, ssv, xMin, yMin, (int)xMax, yMax, yHeight);
			}

			height += drawMultiLineText(g2, textLayouts, some_factor*fontSize, yOffset+height) ;

			
			
			
			//if ( height > totalHeight)
			//	totalHeight = height;
			
			if (height!=totalHeight)
				System.err.println("inconsistent height for "+ annotationName + " legend : " + height + " total: " + totalHeight);
			

		}
	}

	@Override
	public ImageMapData getHtmlMapData() {
		// TODO Auto-generated method stub
		return null;
	}

	private void setMultiLineText() {
		BufferedImage tmpImage = new BufferedImage(imageWidth, 1, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2 = tmpImage.createGraphics();
		g2.setFont(font);

		totalHeight = legendHeight;

		int xOffset = some_factor * font.getSize();

		if ( secStruc == null) {
			System.err.println("!! ProtModLegendDrawer: protMods == null");
			secStruc = new TreeSet<String>();
		}

		multiLineText = new HashMap<String,List<TextLayout>>(secStruc.size());

		for (String sec : secStruc) {


			SecondaryStructureType type = SecondaryStructureType.getTypeFromCharCode(sec.charAt(0));

			AttributedString attributedString = new AttributedString(type.name()+":"+ " " + type.shortDescription);


			AttributedCharacterIterator characterIterator = attributedString.getIterator();
			FontRenderContext fontRenderContext = g2.getFontRenderContext();
			LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator,
					fontRenderContext);

			List<TextLayout> list = new ArrayList<TextLayout>();
			multiLineText.put(sec, list);
			////System.out.println(mod.getPdbccId() + " " + list);
			;
			while (measurer.getPosition() < characterIterator.getEndIndex()) {
				TextLayout textLayout = measurer.nextLayout(imageWidth - xOffset );
				list.add(textLayout);

				totalHeight += textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading() - 1;
				totalHeight +=  LEGEND_TEXT_SPACER;
			}
		}
	}

	private int drawMultiLineText(Graphics2D g2, List<TextLayout> textLayouts, int xOffset, int yOffset) {
		int deltaY = 0;
		Color c = g2.getColor();
		g2.setColor(Color.black);
		for (TextLayout textLayout : textLayouts) {
			deltaY += textLayout.getAscent();
			textLayout.draw(g2, xOffset, yOffset + deltaY);
			deltaY += textLayout.getDescent() + textLayout.getLeading();
		}
		g2.setColor(c);
		
		deltaY += LEGEND_TEXT_SPACER;
		return deltaY;
	}
}
