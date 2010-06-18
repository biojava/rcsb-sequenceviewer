package org.rcsb.sequence.view.html;
//package org.rcsb.sequence.view.htmlg2d;
//
//import java.awt.Point;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.pdb.util.logging.PdbLogger;
//import org.rcsb.sequence.ResidueId;
//import org.rcsb.sequence.ResidueNumberScheme;
//import org.rcsb.sequence.Sequence;
//import org.rcsb.sequence.SequenceSegment;
//import org.rcsb.sequence.conf.AnnotationName;
//import org.rcsb.util.CollectionTransformer;
//
//import static org.rcsb.sequence.conf.Annotation2Html.ANNOTATION_TO_RENDERER_MAP;
//
//public class FragmentView implements Serializable
//{
//   private static final long serialVersionUID = 1L;
//   private final Map<AnnotationName, Renderer> annotationMapData = new LinkedHashMap<AnnotationName, Renderer>();
//   private final Collection<Renderable> orderedRenderables = new ArrayList<Renderable>();
//   private Collection<HtmlMapData> orderedMapData = new ArrayList<HtmlMapData>();
//   private final SequenceRenderer sequenceRenderer;
//   private transient Ruler upperRuler;
//   private transient final Ruler lowerRuler;
//   private final String fragmentId;
//   private int yOffset = 0;
//   private final int imageWidth;
//   
////   private int yOffset;
//   
//   public int getYOffset() {
//      return yOffset;
//   }
//
//   FragmentView(final SequenceSegment sequenceFragment, final int fragmentIndex, final Collection<AnnotationName> annotationsToRender, final ResidueNumberScheme topRuler, final ResidueNumberScheme bottomRuler, final Integer fontSize, int yOffset)
//   {
//      Renderer r;
//      Ruler ru;
//      
//      this.yOffset = yOffset;
//      fragmentId = sequenceFragment.getChainId() + fragmentIndex;
//      
//      // annotations
//      for(AnnotationName an : annotationsToRender)
//      {
//         try {
//            r = ANNOTATION_TO_RENDERER_MAP.get(an).newInstance();
//            setSequenceFontSizeOffsetAndThenGenerateHtml(r, sequenceFragment, fontSize, an);
//            
//            annotationMapData.put(an, r);
//         } 
//         catch (Exception e)
//         {
//            PdbLogger.error("Could not instantiate Renderer for " + an + " on " + sequenceFragment, e);
//         }
//      }
//      
//
//      // top ruler
//      // may be null
//      if(topRuler != null)
//      {
//         ru = new RulerImpl();
//         ru.setResidueNumberScheme(topRuler);
//         ru.setShouldGoAbove(true);
//         setSequenceFontSizeOffsetAndThenGenerateHtml(ru, sequenceFragment, fontSize, null);
//         upperRuler = ru;
//      }
//      
//      
//      // sequence
//      // may not be null
//      r = new SequenceRenderer();
//      r.setResidueNumberScheme(sequenceFragment.getDefaultResidueNumberScheme());
//      setSequenceFontSizeOffsetAndThenGenerateHtml(r, sequenceFragment, fontSize, null);
//      sequenceRenderer = (SequenceRenderer)r;
//      imageWidth = r.getImageWidthPx();
//      
//      // bottom ruler
//      // may NOT be null
//      ru = new RulerImpl();
//      ru.setResidueNumberScheme(bottomRuler);
//      ru.setShouldGoAbove(false);
//      setSequenceFontSizeOffsetAndThenGenerateHtml(ru, sequenceFragment, fontSize, null);
//      lowerRuler = ru;
//      
//      initOrderedMapData();
//      PdbLogger.debug(sequenceFragment.getStructureId() + " fragment " + fragmentId + " generated");
//   }
//   
//   private void initOrderedMapData()
//   {
//      PdbLogger.debug("In initOrderedMapData for " + fragmentId);
//      for(AnnotationName an : AnnotationName.values())
//      {
//         if(annotationMapData.containsKey(an))
//         {
//            orderedRenderables.add(annotationMapData.get(an));
//         }
//      }
//      
//      if(upperRuler != null)
//      {
//         orderedRenderables.add(upperRuler);
//      }
//      orderedRenderables.add(sequenceRenderer);
//      orderedRenderables.add(lowerRuler);
//      
//      PdbLogger.debug("Done making collection");
//      
//      orderedMapData = RENDERABLE_TO_HTML_MAP_TRANSFORMER.transform(orderedRenderables);
//      
//      PdbLogger.debug("Transformed collection");
//   }
//   
//   private static final CollectionTransformer<Renderable, HtmlMapData> RENDERABLE_TO_HTML_MAP_TRANSFORMER = new CollectionTransformer<Renderable, HtmlMapData>()
//   {
//      public HtmlMapData transform(Renderable arg0) {
//         HtmlMapData result;
//         if(arg0 == null)
//         {
//            result = HtmlMapData.EMPTY_HTML_DATA_MAP;
//         }
//         else
//         {
//            result = arg0.getHtmlMapData();
//         }
//         return result;
//      }
//   };
//   
//   private void setSequenceFontSizeOffsetAndThenGenerateHtml(final Renderable r, 
//         final Sequence sequence, final Integer fontSize, AnnotationName an)
//   {
////      r.setYOffset(yOffset);
//      r.setSequence(sequence);
//      if(an != null && r instanceof Renderer)
//      {
//         ((Renderer)r).setAnnotation(an);
//      }
//      if(fontSize != null)
//      {
//         r.setFontSize(fontSize);
//      }
//      r.generateDataForHtml();
//      PdbLogger.debug("Generated data for html in " + fragmentId + " with annotation " + an);
//      yOffset += r.getImageHeightPx();
//   }
//
////   public Collection<HtmlMapData> getOrderedMapData() {
////      return Collections.unmodifiableCollection(orderedMapData);
////   }
//
//   public Collection<Renderable> getOrderedRenderables() {
//      return orderedRenderables;
//   }
//
//   private HtmlMapData getMapData(Renderable r)
//   {
//      return r == null ? null : r.getHtmlMapData();
//   }
//   
//   public HtmlMapData getLowerRulerMapData() {
//      return getMapData(lowerRuler);
//   }
//
//   public HtmlMapData getSequenceMapData() {
//      return getMapData(sequenceRenderer);
//   }
//
//   public HtmlMapData getUpperRulerMapData() {
//      return getMapData(upperRuler);
//   }
//
//   public String getFragmentId() {
//      return fragmentId;
//   }
//   
//   int getAnnotationHeightPx(AnnotationName an)
//   {
//      return annotationMapData.get(an).getImageHeightPx();
//   }
//   
//   int getSequenceHeightPx()
//   {
//      return sequenceRenderer.getImageHeightPx();
//   }
//   
//   int getUpperRulerHeightPx()
//   {
//      return upperRuler.getImageHeightPx();
//   }
//   
//   int getLowerRulerHeightPx()
//   {
//      return lowerRuler.getImageHeightPx();
//   }
//   
//   HtmlMapData getAnnotationMapData(AnnotationName an)
//   {
//      Renderable r = annotationMapData.get(an);
//      return r == null ? HtmlMapData.EMPTY_HTML_DATA_MAP : r.getHtmlMapData();
//   }
////   
////   SequenceRenderer getSequenceRenderer()
////   {
////      return sequenceRenderer;
////   }
////   
////   Ruler getUpperRuler()
////   {
////      return upperRuler;
////   }
////   
////   Ruler getLowerRuler()
////   {
////      return lowerRuler;
////   }
//
//   Map<ResidueId, Point> getDisulphidePositions()
//   {
//      return sequenceRenderer.getDisulphidePositions();
//   }
//   
//   int getDisulphideYPosNudgePx()
//   {
//      return sequenceRenderer.getImageHeightPx() / 2;
//   }
////   public int getNewYOffset() {
////      return yOffset;
////   }
////   
//
//   public int getImageWidth() {
//      return imageWidth;
//   }
//}
