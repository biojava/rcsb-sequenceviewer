package org.rcsb.sequence.view.html;


import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;

public class CSASummary extends AnnotationSummaryCell<Integer>
{

   public CSASummary(AnnotationGroup<Integer> ag) {
      super(ag);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void renderAnnotation(AnnotationValue av, HtmlElement el) {
      //CatalyticResidueValue crv =  (CatalyticResidueValue)av;
      
      el.appendToContent(String.valueOf(av.value()));
   }

}
