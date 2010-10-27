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

import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ProteinModification;

public class ProtModLegendDrawer implements Drawer {
	private Font font;
	private final int imageWidth;
	private int totalHeight;
	private ImageMapData mapData = null;
	private ProtModDrawerUtil modDrawerUtil;
	private Map<ProteinModification, List<TextLayout>> multiLineText;

	public ProtModLegendDrawer(ProtModDrawerUtil modDrawerUtil, Font font,
			final int imageWidth) {
		this.modDrawerUtil = modDrawerUtil;
		this.imageWidth = imageWidth;
		this.font = font;
		setMultiLineText();
	}

	
	
	public void draw(Graphics2D g2, int yOffset) {
		if (modDrawerUtil==null)
			return;
		
		
		int fontSize = font.getSize();
		
		int oldBendOffset = modDrawerUtil.getCrosslinkLineBendOffset();
		modDrawerUtil.setCrosslinkLineBendOffset(0);
		
		int height = 0;
		
		for (ProteinModification mod : modDrawerUtil.getProtMods()) {
			
			List<TextLayout> textLayouts = multiLineText.get(mod);
			
			float lineHeight = textLayouts.get(0).getAscent() 
					+ textLayouts.get(0).getDescent() + textLayouts.get(0).getLeading();
			int yMid = yOffset + height + (int)lineHeight/2;
			
			modDrawerUtil.drawProtMod(g2, mod, 2*fontSize, 
					yMid-fontSize/2, 3*fontSize, yMid+fontSize/2);
			
			ModificationCategory cat = mod.getCategory();
			if (cat.isCrossLink() && cat!=ModificationCategory.CROSS_LINK_1) {
				Point p1 = new Point(0, yMid);
				Point p2 = new Point(5*fontSize, yMid);
				List<Point> points = Arrays.asList(
						p1,
						p2); 
				modDrawerUtil.drawCrosslinks(g2, mod, points);
				
				//xPos, xPos += counter * fontWidth, yMin, yMax, counter));
			}
			
			
			
			height += drawMultiLineText(g2, textLayouts, 6*fontSize, yOffset+height);
		}
		
		if (height!=totalHeight)
			System.err.println("inconsistent height for ptm legend : " + height + " total: " + totalHeight);
		
		modDrawerUtil.setCrosslinkLineBendOffset(oldBendOffset);
		
	}
	
	private String printModification(ProteinModification mod) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(mod.getCategory());

		Set<String> keywords = mod.getKeywords();
		if (keywords!=null && !keywords.isEmpty()) {
			sb.append("; ");
			for (String keyword : keywords) {
				sb.append(keyword);
				sb.append(", ");
			}
			sb.delete(sb.length()-2,sb.length());
		}
		
		String resid = mod.getResidId();
		if (resid != null) {
			sb.append("; ");
			sb.append("RESID:");
			sb.append(resid);
			String residname = mod.getResidName();
			if (residname != null) {
				sb.append(" (");
				sb.append(residname);
				sb.append(')');
			}
		}
		
		return sb.toString();
	}
	
	private int drawMultiLineText(Graphics2D g2, List<TextLayout> textLayouts, int xOffset, int yOffset) {
	    int deltaY = 0;
	    for (TextLayout textLayout : textLayouts) {
	      deltaY += textLayout.getAscent();
	      textLayout.draw(g2, xOffset, yOffset + deltaY);
	      deltaY += textLayout.getDescent() + textLayout.getLeading();
	    }
	    return deltaY;
	}
	
	private void setMultiLineText() {
		BufferedImage tmpImage = new BufferedImage(imageWidth, 1, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2 = tmpImage.createGraphics();
		g2.setFont(font);
		
		totalHeight = 0;
		
		int xOffset = 6 * font.getSize();
		
		multiLineText = new HashMap<ProteinModification,List<TextLayout>>(modDrawerUtil.getProtMods().size());
		
		for (ProteinModification mod : modDrawerUtil.getProtMods()) {
			AttributedString attributedString = new AttributedString(printModification(mod));
			
			
			AttributedCharacterIterator characterIterator = attributedString.getIterator();
			FontRenderContext fontRenderContext = g2.getFontRenderContext();
		    LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator,
		        fontRenderContext);
		    
		    List<TextLayout> list = new ArrayList<TextLayout>();
		    multiLineText.put(mod, list);
		    //System.out.println(mod.getPdbccId() + " " + list);
		    while (measurer.getPosition() < characterIterator.getEndIndex()) {
		      TextLayout textLayout = measurer.nextLayout(imageWidth - xOffset);
		      list.add(textLayout);
		      
		      totalHeight += textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading();
		    }
		}
	}
	
	public ImageMapData getHtmlMapData() {
		
		if(mapData == null)
		{
			mapData = new ImageMapData("modification" + hashCode(), totalHeight)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void populateImageMapData() {

					//int yOffset = 0;
					//int height = totalHeight;
					
					//for (ProteinModification mod : modDrawerUtil.getProtMods()) 
					addImageMapDataEntry(new Entry(0, imageWidth, "Protein Modification", null)); // for now
						

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
