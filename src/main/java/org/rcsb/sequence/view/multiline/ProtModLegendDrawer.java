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

import org.apache.commons.lang3.StringUtils;
import org.biojava.nbio.protmod.ModificationCategory;
import org.biojava.nbio.protmod.ProteinModification;
import org.rcsb.sequence.util.AnnotationConstants;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;


public class ProtModLegendDrawer implements Drawer {
    private static final int legendHeight = 25;
    private static final int legendOffset = 50;
    private static final int legendSpacing = 20;
    private static final int some_factor = 6;
    private final int imageWidth;
    Set<ProteinModification> protmods;
    String annotationName;
    private Font font;
    private int totalHeight;
    private ImageMapData mapData = null;
    private ProtModDrawerUtil modDrawerUtil;
    private Map<ProteinModification, List<TextLayout>> multiLineText;

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

        if (modDrawerUtil == null)
            return;

        if (protmods == null || protmods.size() < 1)
            return;


        int fontSize = font.getSize();

        int oldBendOffset = modDrawerUtil.getCrosslinkLineBendOffset();
        modDrawerUtil.setCrosslinkLineBendOffset(0);

        int height = legendHeight;
        Color c = g2.getColor();
        g2.setColor(Color.black);
        g2.setFont(font);
        g2.drawString(annotationName + " Legend", legendOffset, yOffset + legendSpacing);
        g2.setColor(c);
        if (multiLineText == null)
            setMultiLineText();

        for (ProteinModification mod : protmods) {

            List<TextLayout> textLayouts = multiLineText.get(mod);

            float lineHeight = 15;

            if ( textLayouts != null) {

                lineHeight = textLayouts.get(0).getAscent()
                        + textLayouts.get(0).getDescent() + textLayouts.get(0).getLeading();

            }

            int yMid = yOffset + height + (int) lineHeight / 2;

            modDrawerUtil.drawProtMod(g2, mod, 2 * fontSize,
                    yMid - fontSize / 2, 3 * fontSize, yMid + fontSize / 2);

            ModificationCategory cat = mod.getCategory();

            if (cat.isCrossLink() && cat != ModificationCategory.CROSS_LINK_1 &&
                    annotationName.equals(AnnotationConstants.proteinModification)) {

                Point p1 = new Point(0, yMid);
                Point p2 = new Point(5 * fontSize, yMid);

                List<Point> points = Arrays.asList(
                        p1,
                        p2);
                modDrawerUtil.drawCrosslinks(g2, mod, points);

                //xPos, xPos += counter * fontWidth, yMin, yMax, counter));
            }


            if ( textLayouts == null)
                continue;

            height += drawMultiLineText(g2, textLayouts, some_factor * fontSize, yOffset + height);
        }

        if (height != totalHeight)
            System.err.println("inconsistent height for " + annotationName + " legend : " + height + " total: " + totalHeight);

        modDrawerUtil.setCrosslinkLineBendOffset(oldBendOffset);

    }


    private int drawMultiLineText(Graphics2D g2, List<TextLayout> textLayouts, int xOffset, int yOffset) {
        int deltaY = 0;
        Color c = g2.getColor();
        g2.setColor(Color.black);
        for (TextLayout textLayout : textLayouts) {

            if ( textLayout == null)
                continue;

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

        if (protmods == null) {

            /// probably a site record ??
            System.err.println("!! ProtModLegendDrawer: protMods == null");
            protmods = new TreeSet<ProteinModification>();


        }



        multiLineText = new HashMap<ProteinModification, List<TextLayout>>(protmods.size());

        for (ProteinModification mod : protmods) {

            StringBuffer b = new StringBuffer();

            if(StringUtils.isNotBlank(mod.getPdbccName())) b.append(mod.getPdbccName());

            if(StringUtils.isNotBlank(mod.getResidName())){
                if(b.length() > 0) b.append(" - ");
                b.append(mod.getResidName());
            }else if(StringUtils.isNotBlank(mod.getPsimodName())) {
                if(b.length() > 0) b.append(" - ");
                b.append(mod.getPsimodName());
            }

            if ( b.length() == 0)
                continue;

            AttributedString attributedString = new AttributedString(b.toString());

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

                totalHeight += textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading() - 1;
            }
        }
    }

    public ImageMapData getHtmlMapData() {

        if (mapData == null) {
            mapData = new ImageMapData(AnnotationConstants.proteinModification + hashCode(), totalHeight) {
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
