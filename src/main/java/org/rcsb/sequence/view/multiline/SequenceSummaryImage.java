package org.rcsb.sequence.view.multiline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.core.SequenceSegmentImpl;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.SegmentedSequence;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.oneline.SequenceLineDrawer;

public class SequenceSummaryImage extends AbstractSequenceImage
{

   /**
    * Constructor for creating images of a {@link SegmentedSequence}. Each <tt>SequenceSegment</tt> in the
    * <tt>SegmentedSequence</tt> is treated as an individual sequence and stacked.
    * 
    * @param sequence
    * @param annotationsToView
    * @param rnsOfBottomRuler
    * @param rnsOfTopRuler
    * @param fontSize
    * @param fragmentBuffer
    * @param numCharsInKey
    */
   public SequenceSummaryImage(SegmentedSequence sequence, Collection<AnnotationName> annotationsToView,
         ResidueNumberScheme rnsOfBottomRuler, ResidueNumberScheme rnsOfTopRuler, int fontSize, float fragmentBuffer, int numCharsInKey)
   {
      this(sequence.getSequenceSegments(), annotationsToView, rnsOfBottomRuler, rnsOfTopRuler, fontSize, fragmentBuffer, numCharsInKey);

   }

   /**
    * Create a sequence image for a given {@link Sequence}. The whole sequence is rendered on one horizontal line
    * without line breaks. If the <tt>Sequence</tt> is too long, consider requesting a {@link SegmentedSequence} from
    * the <tt>Sequence</tt>
    * 
    * @param sequence
    * @param annotationsToView
    * @param rnsOfBottomRuler
    * @param rnsOfTopRuler
    * @param fontSize
    * @param fragmentBuffer
    * @param numCharsInKey
    * @see Sequence#getSegmentedSequence(int, ResidueNumberScheme)
    */
   public SequenceSummaryImage(Sequence sequence, Collection<AnnotationName> annotationsToView, ResidueNumberScheme rnsOfBottomRuler,
         ResidueNumberScheme rnsOfTopRuler, int fontSize, float fragmentBuffer, int numCharsInKey)
   {

      this(Collections.singletonList(sequence), annotationsToView, rnsOfBottomRuler, rnsOfTopRuler, fontSize, fragmentBuffer, numCharsInKey);

   }

   public SequenceSummaryImage(List<? extends Sequence> sequences, Collection<AnnotationName> annotationsToView,
         ResidueNumberScheme rnsOfBottomRuler, ResidueNumberScheme rnsOfTopRuler, int fontSize, float fragmentBuffer, int numCharsInKey)
   {
      // unify the sequence to a single string!

      Sequence seq0 = sequences.get(0);
      // PdbLogger.warn(seq0.getClass().getName());

      // TODOL get default length from config!
      int defaultLength = 60;

      if (seq0 instanceof SequenceSegmentImpl)
      {
         SequenceSegmentImpl segseq = (SequenceSegmentImpl) seq0;
         defaultLength = segseq.getSequenceLength();
         seq0 = segseq.getFullSequence().getOriginalSequence();
         // PdbLogger.warn("here we are!" + seq0.getSequenceLength());
      }

      initImage(Collections.singletonList(seq0), fontSize, fragmentBuffer, numCharsInKey);

      imageWidth = imageWidthOffset + fontWidth * defaultLength;

      // PdbLogger.warn("using num chars In Key " + numCharsInKey);
      // PdbLogger.warn("seq length:" + seq0.getSequenceLength());

      int yOffset = 0;

      // add Drawers for the sequence summary view...
      SequenceLineDrawer sequenceDrawer = null;

      // the line should always be relative to UniProt...
      sequenceDrawer = new SequenceLineDrawer(this, seq0, ResidueNumberScheme.DBREF);

      yOffset += addRenderable(sequenceDrawer, SEQUENCE);
      yOffset += addRenderable(new LineRuler(this, seq0, ResidueNumberScheme.DBREF, false), LOWER_RULER);

      if (sequenceDrawer != null)
      {
         this.imageHeight = yOffset;
      }
      else
      {
         System.err.println("problem during creation of SequenceImage: sequenceDrawer == null!");
         System.err.println("Uh oh -- the sequence summary image for " + sequences + " didn't get drawn right");

         this.imageHeight = 0;
      }
   }

   public int addRenderable(Drawer r, String key)
   {
      orderedRenderables.add(r);
      if (! key.equals(SPACER)) allMaps.put(key, r.getHtmlMapData());
      return r.getImageHeightPx();
   }

   /**
    * <p>
    * Gets the image encoded as a <tt>byte[]</tt>.
    * </p>
    * 
    * @return
    */
   public byte[] getImageBytes()
   {
      // PdbLogger.warn("sequence summary image get image bytes..." );
      if (imageBytes == null)
      {
         BufferedImage result = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

         Graphics2D g2 = result.createGraphics();

         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         g2.setBackground(Color.white);
         g2.clearRect(0, 0, imageWidth, imageHeight);

         int yOffset = 0;

         // iterate through all the drawers, telling them to draw at the given y offset on the graphics object
         for (Drawer r : orderedRenderables)
         {
            r.draw(g2, yOffset);
            yOffset += r.getImageHeightPx();

         }

         imageBytes = bufferedImageToByteArray(result, imageWidth);
      }
      return imageBytes;
   }
}
