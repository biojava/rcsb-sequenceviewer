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

import java.awt.Graphics2D;
import java.awt.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ProteinModification;

public class ProtModLegendDrawer implements Drawer {

	private final int legendHeightPx;
	private final int spacesBetweenLegends;
	private ImageMapData mapData = null;
	private ProtModDrawerUtil modDrawerUtil = null;

	public ProtModLegendDrawer(final int legendHeightPx, final int spacesBetweenLegends) {
	   this.legendHeightPx = legendHeightPx;
	   this.spacesBetweenLegends = spacesBetweenLegends;
	}
	
	public void setModDrawerUtil(ProtModDrawerUtil modDrawerUtil) {
		this.modDrawerUtil = modDrawerUtil;
	}

	public void draw(Graphics2D g2, int yOffset) {
		if (modDrawerUtil==null)
			return;
		
		int oldBendOffset = modDrawerUtil.getCrosslinkLineBendOffset();
		modDrawerUtil.setCrosslinkLineBendOffset(0);
		
		int i=0;
		for (ProteinModification mod : modDrawerUtil.getProtMods()) {
			int yMin = yOffset+(legendHeightPx+spacesBetweenLegends)*i;
			int yMax = yMin+legendHeightPx;
			
			modDrawerUtil.drawProtMod(g2, mod, 2*legendHeightPx, 
					yMin, 3*legendHeightPx, yMax);
			
			ModificationCategory cat = mod.getCategory();
			if (cat.isCrossLink() && cat!=ModificationCategory.CROSS_LINK_1) {
				int yMid = yMin + legendHeightPx/2;
				List<Point> points = Arrays.asList(
						new Point(0, yMid),
						new Point(5*legendHeightPx, yMid)); 
				modDrawerUtil.drawCrosslinks(g2, mod, points);
			}
			
			g2.drawString(printModification(mod), 6*legendHeightPx, yMax);
			
			i++;
		}
		
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
	
	public ImageMapData getHtmlMapData() {
	   return mapData;
	}

	public int getImageHeightPx() {
		if (modDrawerUtil==null)
			return 0;
		
		int n = modDrawerUtil.getProtMods().size();
		return (legendHeightPx + spacesBetweenLegends)*n;
	}
}
