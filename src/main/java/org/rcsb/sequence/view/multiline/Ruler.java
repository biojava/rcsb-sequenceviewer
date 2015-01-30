package org.rcsb.sequence.view.multiline;

public interface Ruler extends Drawer {

    public abstract int getMajorTickInterval();

    public abstract void setMajorTickInterval(int interval); // defaults to 10

    public abstract int getMinorTickInterval();

    public abstract void setMinorTickInterval(int interval); // defaults to 5

    public abstract void extraTicksForInsertionCodes(boolean extraTicksIsTrue);

    public abstract boolean areExtraTicksForInsertionCodes();
//   public abstract int getRulerFontSize();

    public abstract boolean shouldGoBelowSequence();

    public abstract boolean shouldGoAboveSequence();

    public abstract void setShouldGoAbove(boolean aboveIsTrue);

}
