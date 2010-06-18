package org.rcsb.sequence.view.image;

import java.awt.Font;
import java.awt.FontMetrics;

/** The interface for SequenceImage classes
 * 
 * Used by Drawer to access the data from the sequence image
 * 
 * @author Andreas Prlic
 *
 */
public interface SequenceImageIF {
	
	public Font getFont();
	
	public int getFontSize();
	
	public int getImageHeight() ;

	public int getImageWidth() ;

	public FontMetrics getFontMetrics() ;

	public Font getSmallFont() ;

	public FontMetrics getSmallFontMetrics();

	public int getSmallFontSize() ;

	public int getSmallFontHeight() ;

	public int getSmallFontAscent() ;

	public int getSmallFontWidth() ;

	public int getFontHeight() ;

	public int getFontAscent() ;

	public int getFontWidth() ;

	public int getNumCharsInKey() ;

	public int getImageWidthOffset() ;

	public int getImageOffsetBuffer() ;

}
