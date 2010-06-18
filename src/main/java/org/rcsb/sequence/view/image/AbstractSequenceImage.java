package org.rcsb.sequence.view.image;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.MapOfCollections;
import org.rcsb.sequence.util.ResourceManager;


public abstract class AbstractSequenceImage implements SequenceImageIF {
	protected  int fontSize;
	protected  int fragmentBufferPx;
	protected  int imageWidthOffset;
	protected  int imageOffsetBuffer;

	protected Font font;
	protected FontMetrics fontMetrics = null;
	protected Font smallFont;
	protected FontMetrics smallFontMetrics;

	protected  int smallFontSize;
	protected  int smallFontHeight;
	protected  int smallFontAscent;
	protected  int smallFontWidth;

	protected  int fontHeight;
	protected  int fontAscent;
	protected  int fontWidth;
	protected  int numCharsInKey;

	public static final String SEQUENCE    = "sequence";
	public static final String LOWER_RULER = "lowerRuler";
	public static final String UPPER_RULER = "upperRuler";
	public static final String SPACER      = "spacer";

	protected List<? extends Sequence> sequences;
	protected byte[] imageBytes = null;
	protected int imageHeight;
	protected int imageWidth;
	protected final List<Drawer> orderedRenderables = new ArrayList<Drawer>();
	protected final MapOfCollections<String, ImageMapData> allMaps = new MapOfCollections<String, ImageMapData>();

	protected static final String FONT_NAME ;
	protected static final float RELATIVE_DISULPHIDE_LINE_THICKNESS ;
	static {
		ResourceManager rm = new ResourceManager("sequenceview");
		FONT_NAME = rm.getString("fontname");
		RELATIVE_DISULPHIDE_LINE_THICKNESS = Float.parseFloat(rm.getString("sequenceimage.relativeDisulphideLineThickness"));
	}

	private static final Graphics2D FOR_FONT_METRICS;
	static {

		BufferedImage bi = new BufferedImage(50,50,BufferedImage.SCALE_SMOOTH);
		FOR_FONT_METRICS = bi.createGraphics();
		FOR_FONT_METRICS.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		FOR_FONT_METRICS.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		
		//FOR_FONT_METRICS.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));  

	}


	protected void initImage(List<? extends Sequence> sequences,int fontSize, float fragmentBuffer,int numCharsInKey){
		// set up parameters
		this.sequences = sequences;
		this.fontSize = fontSize;
		this.fragmentBufferPx = (int)(fontSize * fragmentBuffer);

		this.font = new Font(FONT_NAME, Font.BOLD, fontSize);

		this.fontMetrics = FOR_FONT_METRICS.getFontMetrics(this.font);
		this.fontHeight  = fontMetrics.getHeight();
		this.fontAscent  = fontMetrics.getAscent();
		this.fontWidth   = fontMetrics.charWidth('A')+2;
		//this.fontWidth   = fontMetrics.getMaxAdvance();

		this.smallFontSize = (int) (fontSize * 0.75);

		this.smallFont = new Font(FONT_NAME, Font.BOLD, smallFontSize);

		this.smallFontMetrics = FOR_FONT_METRICS.getFontMetrics(this.smallFont);
		this.smallFontHeight  = smallFontMetrics.getHeight();
		this.smallFontAscent  = smallFontMetrics.getAscent();
		this.smallFontWidth   = smallFontMetrics.charWidth('A')+2;
		
		this.numCharsInKey = numCharsInKey;
		
		this.imageWidthOffset  = (FOR_FONT_METRICS.getFontMetrics(smallFont).charWidth('A')+1) * numCharsInKey;
		this.imageOffsetBuffer = 0;

		// imageWidth = space for the key + font width in pixels * maximum number of residues
		this.imageWidth = imageWidthOffset + fontWidth * sequences.get(0).getSequenceLength();
		
		
		
	}

	protected static byte[] bufferedImageToByteArray(BufferedImage bi, int imageWidth)
	{
		byte[] result = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(65535);
		try {
			ImageIO.write(bi, "png", baos);
			result = baos.toByteArray();
		} catch (IOException e) {
			System.err.println("Problem generating image " +  e.getMessage());
			e.printStackTrace();
		} 
		return result;
	}


	/** add a Renderable drawer
	 * 
	 * @param r the Drawer
	 * @param key the name of the drawer
	 * @return the height required by drawer
	 */
	abstract  int addRenderable(Drawer r, String key);


	public int getFontSize() {
		return this.fontSize;
	}

	public int getFragmentBufferPx() {
		return fragmentBufferPx;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public Font getFont() {
		return font;
	}

	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}

	public Font getSmallFont() {
		return smallFont;
	}

	public FontMetrics getSmallFontMetrics() {
		return smallFontMetrics;
	}

	public int getSmallFontSize() {
		return smallFontSize;
	}

	public int getSmallFontHeight() {
		return smallFontHeight;
	}

	public int getSmallFontAscent() {
		return smallFontAscent;
	}

	public int getSmallFontWidth() {
		return smallFontWidth;
	}

	public int getFontHeight() {
		return fontHeight;
	}

	public int getFontAscent() {
		return fontAscent;
	}

	public int getFontWidth() {
		return fontWidth;
	}

	public int getNumCharsInKey() {
		return numCharsInKey;
	}

	public int getImageWidthOffset() {
		return imageWidthOffset;
	}

	public int getImageOffsetBuffer() {
		return imageOffsetBuffer;
	}

}
