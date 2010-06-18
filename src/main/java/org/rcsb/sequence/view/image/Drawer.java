package org.rcsb.sequence.view.image;

import java.awt.Graphics2D;



public interface Drawer { 
   
   public abstract int getImageHeightPx();
   public abstract void draw(Graphics2D g2, int yOffset);
   public abstract ImageMapData getHtmlMapData();
   
}
