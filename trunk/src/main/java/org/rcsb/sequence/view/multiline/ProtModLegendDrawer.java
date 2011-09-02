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
import org.rcsb.sequence.util.AnnotationConstants;


public class ProtModLegendDrawer implements Drawer {
	private Font font;
	private final int imageWidth;
	private int totalHeight;
	private ImageMapData mapData = null;
	private ProtModDrawerUtil modDrawerUtil;
	private Map<ProteinModification, List<TextLayout>> multiLineText;
	private static final int legendHeight = 25;
	private static final int legendOffset = 50;
	private static final int legendSpacing = 20;
	
	
	private static final int some_factor = 6;
	
	Set<ProteinModification> protmods;
	
	String annotationName ;
	
	public ProtModLegendDrawer(ProtModDrawerUtil modDrawerUtil, Font font,
			final int imageWidth, Set<ProteinModification> protMods, String annotationName) {
		
		this.modDrawerUtil = modDrawerUtil;
		this.imageWidth = imageWidth;
		this.font = font;
		this.protmods = protMods;
		this.annotationName = annotationName;
		
		setMultiLineText();
		
	}
	
	public void draw(Graphics2D g2, int yOffset) {
				
		if (modDrawerUtil==null)
			return;
		
		if ( protmods == null || protmods.size() < 1)
			return;
		
	
		int fontSize = font.getSize();
		
		int oldBendOffset = modDrawerUtil.getCrosslinkLineBendOffset();
		modDrawerUtil.setCrosslinkLineBendOffset(0);
		
		int height = legendHeight;
		Color c = g2.getColor();
		g2.setColor(Color.black);
		g2.setFont(font);
		g2.drawString(annotationName + " Legend", legendOffset, yOffset+legendSpacing);
		g2.setColor(c);
		if ( multiLineText == null)
			setMultiLineText();
		
		for (ProteinModification mod : protmods ) {
			
			List<TextLayout> textLayouts = multiLineText.get(mod);
			
			float lineHeight = textLayouts.get(0).getAscent() 
					+ textLayouts.get(0).getDescent() + textLayouts.get(0).getLeading();
			int yMid = yOffset + height + (int)lineHeight/2;
			
			modDrawerUtil.drawProtMod(g2, mod, 2*fontSize, 
					yMid-fontSize/2, 3*fontSize, yMid+fontSize/2);
			
			ModificationCategory cat = mod.getCategory();
			
			if (cat.isCrossLink() && cat!=ModificationCategory.CROSS_LINK_1 && 
					annotationName.equals(AnnotationConstants.proteinModification)) {
				
				Point p1 = new Point(0, yMid);
				Point p2 = new Point(5*fontSize, yMid);
				
				List<Point> points = Arrays.asList(
						p1,
						p2); 
				modDrawerUtil.drawCrosslinks(g2,  mod, points);
				
				//xPos, xPos += counter * fontWidth, yMin, yMax, counter));
			}
			
						
			height += drawMultiLineText(g2, textLayouts, some_factor*fontSize, yOffset+height) ;
		}
		
		if (height!=totalHeight)
			System.err.println("inconsistent height for "+ annotationName + " legend : " + height + " total: " + totalHeight);
		
		modDrawerUtil.setCrosslinkLineBendOffset(oldBendOffset);
		
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
	    return deltaY;
	}
	
	private void setMultiLineText() {
		BufferedImage tmpImage = new BufferedImage(imageWidth, 1, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2 = tmpImage.createGraphics();
		g2.setFont(font);
		
		totalHeight = legendHeight;
		
		int xOffset = some_factor * font.getSize();
		
		if ( protmods == null) {
			System.err.println("!! ProtModLegendDrawer: protMods == null");
			protmods = new TreeSet<ProteinModification>();
		}
			
		multiLineText = new HashMap<ProteinModification,List<TextLayout>>(protmods.size());
		
		for (ProteinModification mod : protmods) {
			AttributedString attributedString = new AttributedString(mod.getDescription());
			
			
			AttributedCharacterIterator characterIterator = attributedString.getIterator();
			FontRenderContext fontRenderContext = g2.getFontRenderContext();
		    LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator,
		        fontRenderContext);
		    
		    List<TextLayout> list = new ArrayList<TextLayout>();
		    multiLineText.put(mod, list);
		    //System.out.println(mod.getPdbccId() + " " + list);
		    while (measurer.getPosition() < characterIterator.getEndIndex()) {
		      TextLayout textLayout = measurer.nextLayout(imageWidth - xOffset );
		      list.add(textLayout);
		      
		      totalHeight += textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading() - 1;
		    }
		}
	}
	
	public ImageMapData getHtmlMapData() {
		
		if(mapData == null)
		{
			mapData = new ImageMapData(AnnotationConstants.proteinModification+ hashCode(), totalHeight)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void populateImageMapData() {

					//int yOffset = 0;
					//int height = totalHeight;
					
					//for (ProteinModification mod : modDrawerUtil.getProtMods()) 
					addImageMapDataEntry(new Entry(0, imageWidth, AnnotationConstants.proteinModification, null)); // for now
						

				}
			};
		}
		
		//System.out.println("ProtModLegendDrawer : mapData: " + mapData.getImageMapDataEntries());
	   return mapData;
	}

	public int getImageHeightPx() {
		return totalHeight;
	}
}
