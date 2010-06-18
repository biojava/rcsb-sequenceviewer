package org.rcsb.sequence.view.image;

import java.awt.Graphics2D;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;

public class SpacerDrawer implements Drawer {

   private final int spacerHeightPx;
   private final ImageMapData mapData;

   public SpacerDrawer(final int spacerHeightPx)
   {
      this.spacerHeightPx = spacerHeightPx;
      this.mapData = new ImageMapData("spacer", spacerHeightPx)
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void populateImageMapData() {
            // do nothing
         }
      };
   }
   
   public boolean canRenderAnnotation(AnnotationName annotationName) {
      unsupported();
      return false;
   }

   public void generateDataForHtml() {
      unsupported();
   }

   public void draw(Graphics2D g2, int offset) {
      // do nothing
   }

   public int getFontSize() {
      unsupported();
      return 0;
   }
   
   public ImageMapData getHtmlMapData() {
      return mapData;
   }

   public int getImageHeightPx() {
      return spacerHeightPx;
   }

   public int getImageWidthPx() {
      unsupported();
      return 0;
   }

   public int getNumResidues() {
      unsupported();
      return -1;
   }

   public ResidueNumberScheme getResidueNumberScheme() {
      unsupported();
      return null;
   }

   public int getResidueWidthPx() {
      unsupported();
      return -1;
   }

   public Sequence getSequence() {
      unsupported();
      return null;
   }

   public void setFontSize(int size) {
      unsupported();
   }

   public void setResidueNumberScheme(ResidueNumberScheme rns) {
      unsupported();
   }

   public void setSequence(Sequence sequence) {
      unsupported();
   }
   
   private void unsupported()
   {
      throw new UnsupportedOperationException("This instance of Renderable is a spacer -- never rendered. Adding any properties to it is not supported.");
   }

}
