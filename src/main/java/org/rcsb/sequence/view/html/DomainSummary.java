 package org.rcsb.sequence.view.html;

import org.rcsb.sequence.annotations.DomainDefinitionValue;
import org.rcsb.sequence.conf.Annotation2Jmol;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;

public class DomainSummary extends AnnotationSummaryCell<String> {
   
   public DomainSummary(AnnotationGroup<String> ag) {
      super(ag);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void renderAnnotation(AnnotationValue av, HtmlElement el) {
      HtmlElement colouredDomId, domDesc;
      DomainDefinitionValue ddv =  (DomainDefinitionValue)av;
      Integer numFrags;
      
      colouredDomId = new HtmlElement("span");
      colouredDomId.addAttribute("style", "background-color: " + ColorUtil.getArbitraryHexColor(ddv.value()));
      colouredDomId.addAttribute("onclick", Annotation2Jmol.getOnclick(ag, av));
      colouredDomId.addAttribute("class", "clickableIfJmol");
      colouredDomId.appendToContent("&nbsp;")
                   .appendToContent(av.value().toString())
                   .appendToContent("&nbsp;");
      el.addChild(colouredDomId);
      
      
      domDesc = new HtmlElement("span");
      domDesc.appendToContent("&nbsp;");
      if(ddv.getDescription() != null && !ddv.getDescription().equals(ddv.value()))
      {
         domDesc.appendToContent(ddv.getDescription())
                .appendToContent(": ");
      }
      domDesc.appendToContent( ag.getResiduesPerAnnotationValue().get(ddv).toString() )
             .appendToContent( " residues" );
      
      if((numFrags = ag.getAnnotationValueCount().get(ddv)) > 1)
      {
         domDesc.appendToContent(" in ")
                .appendToContent(numFrags.toString())
                .appendToContent(" fragments");
      }
      el.addChild(domDesc);
      
      if(ddv.isExternalData() && !"#".equals(ddv.getUrl()))
      {
         el.addChild(new LinkOut(ddv.getUrl(), "Link to " + ddv.getPredictionMethod() + " site"));
      }
   
   }
}
