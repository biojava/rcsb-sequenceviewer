package org.rcsb.sequence.view.html;

import org.rcsb.sequence.annotations.Ligand;

import org.rcsb.sequence.conf.Annotation2Jmol;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;

public class LigCRSummary extends AnnotationSummaryCell<Ligand> {

   public LigCRSummary(AnnotationGroup<Ligand> ag) {
      super(ag);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void renderAnnotation(AnnotationValue av, HtmlElement el) {
      //LigandContactResidueValue lcrv = (LigandContactResidueValue)av;
      
      HtmlElement colouredHetId = new HtmlElement("span");
      colouredHetId.addAttribute("style", "background-color: " + ColorUtil.getArbitraryHexColor(av.value()));
      colouredHetId.addAttribute("onclick", Annotation2Jmol.getOnclick(ag, av));
      colouredHetId.addAttribute("class", "clickableIfJmol");
      colouredHetId.appendToContent("&nbsp;")
                   .appendToContent(av.value().toString())
                   .appendToContent("&nbsp;");
      el.addChild(colouredHetId);
      
      HtmlElement desc = new HtmlElement("span");
      desc.appendToContent("Interaction with ligand")
          .appendToContent("&nbsp;");
      
      el.addChild(desc);
      el.addChild(colouredHetId);
   }

}
