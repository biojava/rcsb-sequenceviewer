package org.rcsb.sequence.view.multiline;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.regex.Pattern;

import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceSegment;

public abstract class AbstractDrawer<T> implements Drawer {

	private final SequenceImageIF image;
	private final Sequence sequence;
	private final int maxSeqLen;

	private int imageHeight = -1;
	private int annotationHeight = -1; // ordinarily will be same as imageHeight

	protected ImageMapData mapData = null;

	private static final Color GREY = Color.GRAY;
	
	public void draw(Graphics2D g2, int yOffset) 
	{
		g2.setFont(image.getFont());
		g2.setColor(Color.black);

		// pretty drawing...
		g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	
		//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));  

		drawData(g2, yOffset);
		drawKey(g2, yOffset);
	}

	protected void drawKey(final Graphics2D g2, final int yOffset) {
		drawKey(g2, 0, yOffset, image.getImageWidthOffset() - 1, imageHeight + yOffset);
	}

	protected void drawKey(Graphics2D g2, int xMin, int yMin, int xMax, int yMax) 
	{
		g2.clearRect(xMin, yMin, xMax-xMin, yMax-yMin); // secondary structure renderer oversteps its bounds by design... this wipes that problem out

		String key = shortenKey(getKey());
		if(key == null || key.length() == 0) return;

		final int keyLenPx = key.length() * image.getSmallFontWidth();
		final int startXpos = Math.max(xMin, xMax - keyLenPx - image.getImageOffsetBuffer());
		final int startYpos = yMin + image.getSmallFontAscent() + ((imageHeight - image.getSmallFontHeight()) / 2);
		Color tmpCol = g2.getColor();
		Font tmpFont = g2.getFont();
		g2.setFont(image.getSmallFont());
		g2.setColor(GREY);
		g2.drawString(key, startXpos, startYpos);
		g2.setColor(tmpCol);
		g2.setFont(tmpFont);
	}

	private static final Pattern NON_WORD_PATTERN = Pattern.compile("\\W");

	private String shortenKey(String key)
	{
		if(key == null) return null;
		final int numCharsInKey = image.getNumCharsInKey();

		if(key.length() > numCharsInKey)
		{
			//System.err.println("Key string too long for available space: " + key);
			String[] words = NON_WORD_PATTERN.split(key);
			StringBuilder newKey = new StringBuilder(numCharsInKey);
			if(words.length > 0)
			{
				int i = 0, keyLen, wordLen;
				do
				{
					keyLen = newKey.length();
					wordLen = words[i].length();
					if(keyLen + wordLen + 1 > numCharsInKey)
					{
						if(keyLen == 0)
						{
							newKey.append(words[i].substring(0, Math.min(wordLen - 1, numCharsInKey - 3)))
							.append("..");
						}
						break;
					}

					if(keyLen > 0) newKey.append(' ');
					newKey.append(words[i]);
				}
				while(++i < words.length);
			}
			key = newKey.toString();
		}

		return key;
	}

	protected abstract String getKey();
	protected abstract void drawData(final Graphics2D g2, final int yOffset);

	protected static int numInstances = 0;
	public AbstractDrawer(SequenceImageIF image, Sequence sequence)
	{
		this.image = image;

		this.sequence = sequence;
		
		//System.out.println("AbstractDrawer : " + sequence);
		
		if(sequence instanceof SequenceSegment)
		{
			
			maxSeqLen = ((SequenceSegment)sequence).getMaxLength();
			//System.out.println("AbstractDrawer SequenceSegment maxSeqLen: " + maxSeqLen + " " + sequence.getSequenceLength());
			if(maxSeqLen > 0 && sequence.getSequenceLength() > maxSeqLen) 
			{
				System.err.println("AbstractDrawer: Sequence is too long! " + sequence.getStructureId() + " chain: " + sequence.getChainId());
				//sequenceTooLong();
			}
			else if( isTooShort(sequence) )
			{
				System.err.println("AbstractDrawer: Sequence is too short! "  + sequence.getStructureId() + " chain: " + sequence.getChainId());
				//sequenceTooShort();
			}
		}
		else
		{
			maxSeqLen = sequence.getSequenceLength();
			//System.out.println(maxSeqLen);
		}

		numInstances++;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		numInstances--;
	}

	// a sequence is too short if it is a SequenceSegment that is shorter than the max length and is not the last segment
	private boolean isTooShort(Sequence s)
	{
		boolean result = false;
		if (s.getSequenceLength() < maxSeqLen && s instanceof SequenceSegment) {
			SequenceSegment bs = (SequenceSegment) s;
			
			//System.out.println("AbstractDrawer is too short? " + bs.getFragmentIdx() + " num fragments: " + bs.getNumFragments());
			if( bs.getFragmentIdx() < bs.getNumFragments() ) 
			{
				result = true;
			}
		}
		return result;
	}

	public int getImageHeightPx() 
	{
		return imageHeight;
	}

	public int getNumResidues() {
		return sequence.getSequenceLength();
	}

	public Sequence getSequence() {
		return sequence;
	}

	public int getMaxSeqLen() {
		return maxSeqLen;
	}

	// as usually throwing runtimeExceptions is evil and breaks displays on the website...
	private void sequenceTooLong()
	{
		throw new RuntimeException("Sequence too long! Sequence is " + sequence.getSequenceLength() + " long but max is " + maxSeqLen);
	}
	// as usually throwing runtimeExceptions is evil and breaks displays on the website...
	private void sequenceTooShort()
	{
		throw new RuntimeException("Sequence too short! Sequence is " + sequence.getSequenceLength() + " long but should be " + maxSeqLen);
	}

	protected int getImageHeight() {
		return imageHeight;
	}

	protected int getAnnotationHeight() {
		return annotationHeight;
	}

	protected void setAnnotationHeight(int annotationHeight) {
		this.annotationHeight = annotationHeight;
	}

	protected void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	protected SequenceImageIF getImage() {
		return image;
	}

}
