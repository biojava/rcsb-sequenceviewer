package org.rcsb.sequence.view.multiline;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Sequence;


public class LabelledBoxAnnotationDrawer<T> extends BoxAnnotationDrawer<T> {
   
   public LabelledBoxAnnotationDrawer(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<T>> annotationGroupClass) {
      super(image, sequence, annotationGroupClass);
   }

   @Override
   protected boolean displayLabel() {
      return true;
   }
   
}
