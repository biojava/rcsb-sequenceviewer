package org.rcsb.sequence.view.html;

import java.util.Collection;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;

public abstract class AnnotationSummaryCell<T> {

   protected final AnnotationGroup<T> ag;
   protected final HtmlElement valueCell;
   protected final HtmlElement   keyCell;
   
   public AnnotationSummaryCell(AnnotationGroup<T> ag) {
      this.ag = ag;
      
      valueCell = new HtmlElement("span");
      Collection<? extends AnnotationValue<T>> av = annotationValues();
      
      // if there's no actual data to display, don't render all the stuff.
      if(av.size() == 0)
      {
         //PdbLogger.warn("There is nothing in the value cell for " + ag.getName().name() + " on " + ag.getSequence().getStructureId() + ag.getSequence().getChainId());
         hasData = false;
         keyCell = new HtmlElement("span");
         return;
      }
      
      hasData = true;
      
      for(AnnotationValue<T> a : av)
      {
         HtmlElement el = new HtmlElement("div");
         renderAnnotation(a, el);
         valueCell.addChild(el);
      }
      
      
      keyCell = new HtmlElement("span");
      {
         HtmlElement keyName, links, hideLink, refLink;
         
         keyName = new HtmlElement("div");
         keyName.appendToContent(ag.getName().getName())
                .appendToContent(" ")
                .appendToContent(ag.getClassification().getName());
         

         
         hideLink = new BracketedLink("javascript:sp.hideAnnotation('" + ag.getName().getName() + "')", 
                                      "hide " + ag.getName().getName() + " annotations",
                                      "hide");

         links = new HtmlElement("span");
         links.addAttribute("style", "font-size:0.9em");
         links.addChild(hideLink);
         
         if(ag.getName().getReference().getPubmed() != null)
         {
            refLink = new BracketedLink("#" + ag.getName().getName() + "RefAnchor",
                                        "reference for " + ag.getName().getName(),
                                        "reference");
            links.addChild(refLink);
         }
         
         keyCell.addChild(keyName);
         keyCell.addChild(links);
      }
   }
   
   public AnnotationName getAnnotationName()
   {
      return ag.getName();
   }
   
   protected Collection<? extends AnnotationValue<T>> annotationValues()
   {
      return ag.getAnnotationValueCount().keySet();
   }

   public HtmlElement getValueCell() 
   {
      return valueCell;
   }

   public HtmlElement getKeyCell() 
   {
      return keyCell;
   }
   
   private final boolean hasData;

   public boolean hasData()
   {
      return hasData;
   }
   
   protected abstract void renderAnnotation(AnnotationValue<T> av, HtmlElement el);
     
   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((ag == null) ? 0 : ag.hashCode());
      return result;
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final AnnotationSummaryCell other = (AnnotationSummaryCell) obj;
      if (ag == null) {
         if (other.ag != null)
            return false;
      } else if (!ag.equals(other.ag))
         return false;
      return true;
   }


}
