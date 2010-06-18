package org.rcsb.sequence.view.image;

public interface Ruler extends Drawer {

   public abstract void setMajorTickInterval(int interval); // defaults to 10
   public abstract void setMinorTickInterval(int interval); // defaults to 5
   
   public abstract int getMajorTickInterval();
   public abstract int getMinorTickInterval();
   
   public abstract void extraTicksForInsertionCodes(boolean extraTicksIsTrue);
   
   public abstract boolean areExtraTicksForInsertionCodes();
//   public abstract int getRulerFontSize();
   
   public abstract boolean shouldGoBelowSequence();
   public abstract boolean shouldGoAboveSequence();
   
   public abstract void setShouldGoAbove(boolean aboveIsTrue);   
   
}
