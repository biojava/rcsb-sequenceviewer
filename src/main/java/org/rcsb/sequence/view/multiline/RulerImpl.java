package org.rcsb.sequence.view.multiline;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Iterator;

import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;

public class RulerImpl extends SequenceDrawer implements Ruler {

   protected boolean aboveOrBelow = false; // false is below
   
   private boolean extraTicksForInsertionCodes = true;
   
   // now they are coming from a config file
   private int     majorTickInterval;
   private int     minorTickInterval;
   
   private int rulerLineYpos;
   
   protected int majorTickHeight;
   protected int minorTickHeight;
   
   private Stroke rulerStroke;
   
   private int yPosOfText;
   
   public RulerImpl(SequenceImageIF image, Sequence sequence, ResidueNumberScheme rns, boolean shouldGoAbove)
   {
      super(image, sequence, rns);
      
      majorTickInterval = Integer.parseInt(resourceManager.getString("rulerimpl.majorTickInterval"));
      minorTickInterval = Integer.parseInt(resourceManager.getString("rulerimpl.minorTickInterval"));
      
      setShouldGoAbove(shouldGoAbove);
      setImageHeight(majorTickHeight + rulerLineYpos + image.getSmallFontHeight());
      
      final int fontWidth = image.getFontWidth();
      this.rulerLineYpos   = fontWidth / 10; //fontMetrics.getLeading(); not working properly on linux
      this.majorTickHeight = fontWidth / 3; // fontMetrics.getDescient(); not so good on linux either
      this.minorTickHeight = fontWidth / 4;
      
      this.rulerStroke  = new BasicStroke(image.getFontSize() / 12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
      
      this.yPosOfText = majorTickHeight + image.getSmallFontAscent() + 1; // one for luck!
      
     
   }

   protected void calcScale(SequenceImageIF image){
	   // here: don;t do anything, the size of one Amino Acid is the fontWidth
   }
   
   @Override
   protected void drawData(Graphics2D g2, int yOffset) 
   {
      final Sequence sequence = getSequence();
      
     // System.out.println("RulerImpl: drawing sequence " + sequence);
      
      final SequenceImageIF image = getImage();
      final ResidueNumberScheme rns = getResidueNumberScheme();
      
      calcScale(image);
      
      g2.setFont(image.getSmallFont());
      
      // TODO : Implement additional ticks for insertion codes
//      final boolean hasInsertionCodes = rns.hasInsertionCodes();
      final boolean rulerUsesNonDefaultRns = rns != sequence.getDefaultResidueNumberScheme();
      
     // System.out.println("ruler used RNS: " + rulerUsesNonDefaultRns + " " + rns);
      
      int xPos  = image.getImageWidthOffset();
      boolean isFirstResidue = true;
      
      if(aboveOrBelow) // above == true
      {
         modifyHeightsForRulerAbove();
      }
            
      g2.setStroke(rulerStroke);
      
      Iterator<ResidueId> rIt = sequence.getResidueIds().iterator();
      while(rIt.hasNext())
//      for(ResidueId r : sequence.getResidueIds())
      {         
         ResidueId r = rIt.next();
         ResidueId orig = r;
         // change to the correct rns if necessary
         if(rulerUsesNonDefaultRns)
         {
        	 
      //  System.out.println("Ruler get equivalent ResID: " + rns + " " + r + " " + r.getEquivalentResidueId(rns));;
            r = r.getEquivalentResidueId(rns);
            
         }
         
         if(r == null)
         {
            // if null... (only possible if rulerUsesNonDefaultRns == true)
            if(!rulerUsesNonDefaultRns)
            {
               throw new RuntimeException("Found null residue in collection!");
            }
            
            // ...we should do nothing
         }
         
       
         // if it's the first residue of a sequence
         else if( orig.getPrevious() == null || orig.getPrevious().isBeginningOfChainMarker() )
         {
        	// System.out.println("RulerImpl: begginning of chain: " + r + " " + r.getResidueNumberScheme() + " " + r.getPrevious());
            renderHalfLine(g2, r, xPos, false, true, yOffset);
            isFirstResidue = false;
         }
         
         // if it's the last residue of a sequence
         else if( orig.getNext() == null || orig.getNext().isBeginningOfChainMarker() )
         {
            renderHalfLine(g2, r, xPos, true, true, yOffset);
         }
         
         // major interval? first, last, seqId % major == 0
         else if( isFirstResidue || !rIt.hasNext() || r.getSeqId() % majorTickInterval == 0 )
         {
            renderMajorTick(g2, r, xPos, yOffset);
            isFirstResidue = false;
         }
         
         // minor interval? seqId % minor
         else if( r.getSeqId() % minorTickInterval == 0 )
         {
            renderMinorTick(g2, r, xPos, yOffset);
         }

        
         // just draw an unmarked line
         else
         {
            renderLine(g2, r, xPos, yOffset);
         }
         
         xPos += getWidth(); 
      }
      
   }
   
   protected int  getWidth(){
	// nb this is the residue font width
	   return (getImage().getFontWidth() );   
   }
   
   
   
   protected void modifyHeightsForRulerAbove()
   {
      final int imageHeight = getImageHeight();
      rulerLineYpos   = imageHeight - rulerLineYpos   - 1;
      yPosOfText      = imageHeight - getImage().getSmallFontMetrics().getDescent() - majorTickHeight;
      
     
   }

   protected void renderMajorTick(Graphics2D g2, ResidueId r, int xPos, int yOffset)
   {
      renderLine(g2, r, xPos, yOffset);
      renderTick(g2, r, xPos, majorTickHeight, yOffset);
      renderText(g2, r, xPos, r.getSeqIdWithInsertionCode(), yOffset);
   }

   protected void renderMinorTick(Graphics2D g2, ResidueId r, int xPos, int yOffset)
   {
      renderLine(g2, r, xPos, yOffset);
      renderTick(g2, r, xPos, minorTickHeight, yOffset);
   }
   
   protected void renderTick(Graphics2D g2, ResidueId r, int xPos, int tickHeight, int yOffset)
   {
      int tickXpos  = xPos + getWidth() / 2 ;
      
      if(tickXpos <= endPosOfPreviousText)
      {
         System.err.println("Not adding tick for residue " + r.toString() + " as it overlaps the previous text");
         return;
      }
      
      int tickYpos2 = aboveOrBelow ? rulerLineYpos - tickHeight : rulerLineYpos + tickHeight;
//      g2.drawLine(rulerLineXpos, tickXpos, rulerLineXpos + tickHeight, tickXpos);
      g2.drawLine(tickXpos, rulerLineYpos + yOffset, tickXpos, tickYpos2 + yOffset);
   }
   
   protected void renderLine(Graphics2D g2, ResidueId r, int xPos, int yOffset)
   {
//      g2.drawLine(rulerLineXpos, xPos, rulerLineXpos, xPos + fontWidth);
      g2.drawLine(xPos, rulerLineYpos + yOffset, xPos + getWidth(), rulerLineYpos + yOffset);
   }
   
   protected void renderHalfLine(Graphics2D g2, ResidueId r, int xPos, boolean firstHalf, boolean withTickAndText, int yOffset)
   {
      final int fontWidth = getWidth();
      int x1;
      int x2;
      if(firstHalf)
      {
         x1 = xPos;
         x2 = xPos + (fontWidth / 2);
      }
      else
      {
         x1 = xPos + (fontWidth / 2);
         x2 = xPos + fontWidth;
      }
      g2.drawLine(x1, rulerLineYpos + yOffset, x2, rulerLineYpos + yOffset);
      
      if(withTickAndText)
      {
         renderTick(g2, r, xPos, majorTickHeight, yOffset);
         renderText(g2, r, xPos, r.getSeqIdWithInsertionCode(), yOffset);
      }
   }
   
   private int endPosOfPreviousText = -1;
   
   protected void renderText(Graphics2D g2, ResidueId r, int xPos, String text, int yOffset)
   {
      final SequenceImageIF image = getImage();
      final int imageWidthOffset = image.getImageWidthOffset();
      final int imageWidth = image.getImageWidth();
      final int sizeOfText = image.getSmallFontWidth() * text.length();
      
      int xPosOfText = xPos + ((image.getFontWidth() - sizeOfText) / 2);
      
      // make sure text doesn't go out-of-bounds
      if(xPosOfText < imageWidthOffset)
      {
         xPosOfText = imageWidthOffset;
      }
      if(xPosOfText + sizeOfText > imageWidth)
      {
         xPosOfText = imageWidth - sizeOfText - 1;
      }
      
      if(xPosOfText <= endPosOfPreviousText || sizeOfText + imageWidthOffset > imageWidth)
      {
         System.err.println("Ignoring text string " + text + " on ruler because it would overlap the previous text");
         return;
      }
      
      g2.drawString(text, xPosOfText, yPosOfText + yOffset);
      
      endPosOfPreviousText = xPosOfText + sizeOfText;
   }

   
   public ImageMapData getHtmlMapData() {
      if(mapData == null)
      {
         mapData = new ImageMapData("ruler" + hashCode(), getImageHeight())
         {
            private static final long serialVersionUID = 1L;
   
            @Override
            public void populateImageMapData() {
               String description;
               
               switch(getResidueNumberScheme())
               {
               case SEQRES:
                  description = "Integer residue identifier derived from ordinal of each residue's position in SEQRES entries in the PDB file";
                  break;
               case ATOM:
                  description = "Residue identifiers from PDB ATOM records in the PDB file";
                  break;
               case DBREF:
                  description = "Integer residue identifier derived from ordinal of each residue's position in the " 
                     + getSequence().getExternalDbName() + " sequence database";
                  break;
               case _ARRAY_IDX:
               default:
                  description = "This ruler is intended for internal use only";
                  break;
               }
               
               addImageMapDataEntry(new Entry(0, getImage().getImageWidth() - 1, description, null));
            }
         };
      }
      return mapData;
   }

   public boolean areExtraTicksForInsertionCodes() {
      return extraTicksForInsertionCodes;
   }

   public void extraTicksForInsertionCodes(boolean extraTicksIsTrue) {
      this.extraTicksForInsertionCodes = extraTicksIsTrue;
   }

   public int getMajorTickInterval() {
      return majorTickInterval;
   }

   public int getMinorTickInterval() {
      return minorTickInterval;
   }

   public void setMajorTickInterval(int interval) {
      this.majorTickInterval = interval;
   }

   public void setMinorTickInterval(int interval) {
      this.minorTickInterval = interval;
   }

   public void setShouldGoAbove(boolean aboveIsTrue) {
      this.aboveOrBelow = aboveIsTrue;
   }

   public boolean shouldGoAboveSequence() {
      return this.aboveOrBelow;
   }

   public boolean shouldGoBelowSequence() {
      return !shouldGoAboveSequence();
   }
   
   @Override
   protected String getKey() {
      String key;
      
      switch(getResidueNumberScheme())
      {
      case DBREF:
         key = getSequence().getExternalDbName();
         break;
      case ATOM:
         key = "PDB";
         break;
      case SEQRES: 
    	  key = "SEQRES";
    	  break;
      case _ARRAY_IDX:
      default:
         key = "BAD";
      }
      
      return key;
   }

}
