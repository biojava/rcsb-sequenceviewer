package org.rcsb.sequence.view.multiline;

import java.awt.Graphics2D;


import org.rcsb.sequence.annotations.SecondaryStructureType;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Sequence;

public class AuthorSecondaryStructureDrawer extends
      SecondaryStructureDrawer 
{
   public AuthorSecondaryStructureDrawer(SequenceImage image,
                                           Sequence sequence,
                                           Class<? extends AnnotationGroup<String>> annotationGroupClass) 
   {
      super(image, sequence, annotationGroupClass);
   }

   @ Override
   protected void drawSpaceBetweenAnnotations(Graphics2D g2, int sequenceLength, int xMin, int yMin, int xMax, int yMax) {
	   
	   final int fontWidth = getImage().getFontWidth();
	   
      drawNoSSFragment(g2, SecondaryStructureType.empty, xMin-fontWidth/2, yMin, xMax-fontWidth/2, yMax);
   }
}
