package org.rcsb.sequence.view.multiline;

import java.awt.Graphics2D;


import org.rcsb.sequence.annotations.SecondaryStructureValue;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Sequence;

public class AuthorSecondaryStructureDrawer extends
      SecondaryStructureDrawer 
{
   public AuthorSecondaryStructureDrawer(SequenceImage image,
                                           Sequence sequence,
                                           Class<? extends AnnotationGroup<Character>> annotationGroupClass) 
   {
      super(image, sequence, annotationGroupClass);
   }

   @Override
   protected void drawSpaceBetweenAnnotations(Graphics2D g2, int sequenceLength, int xMin, int yMin, int xMax, int yMax) {
      drawNoSSFragment(g2, SecondaryStructureValue.empty, xMin, yMin, xMax, yMax);
   }
}
