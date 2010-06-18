package org.rcsb.sequence.view.image;

import static org.rcsb.sequence.model.ResidueNumberScheme.DBREF;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import org.rcsb.sequence.core.DisulfideAnnotationGroup;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ResourceManager;


/** A drawer that represents a sequence as a simple line.
 * The sequence is always scaled to be displayed in one line.
 * 
 * @author Andreas Prlic
 *
 */
public class SequenceLineDrawer extends AbstractDrawer<Object> {

	private DisulfideAnnotationGroup disulphides = null;
	private boolean hasDisulphides = false;
	private Map<ResidueId, Point> disulphidePositions = Collections.emptyMap();

	private final ResidueNumberScheme rns;


	protected ResourceManager resourceManager;

	// now colors are coming from config file
	private Color residueNoStructure;
	private Color residueWithStructure;
	private Color residueUniProtMismatch;
	private Color residueNonstandard  ;
	private Color disulphideColor     ;


	private float scale;

	public SequenceLineDrawer(SequenceImageIF image, Sequence sequence, ResidueNumberScheme rns)
	{
		super(image, sequence);
		
		scale = 1.0f;
		
		resourceManager = new ResourceManager("sequenceview");

		String col1 = resourceManager.getString("sequencedrawer.residueNoStructure");
		residueNonstandard   = Color.decode(col1); 

		String col2 = resourceManager.getString("sequencedrawer.residueWithStructure");
		residueWithStructure = Color.decode(col2);

		String col3 = resourceManager.getString("sequencedrawer.residueUniProtMismatch");
		residueUniProtMismatch = Color.decode(col3);

		String col4 = resourceManager.getString("sequencedrawer.residueNonstandard");
		residueNonstandard = Color.decode(col4);

		String col5 = resourceManager.getString("sequencedrawer.disulphideColor");
		disulphideColor = Color.decode(col5);

		this.rns = rns;

		setImageHeight(image.getFontHeight());

		disulphides = sequence.getDisulfideAnnotationGroup();
		
		if(disulphides != null && disulphides.getAnnotations().size() > 0)
		{
			hasDisulphides = true;
			disulphidePositions = new HashMap<ResidueId, Point>();
		}
	}

	public SequenceLineDrawer(SequenceImageIF image, Sequence sequence)
	{
		this(image, sequence, sequence.getDefaultResidueNumberScheme());
	}


	public ImageMapData getHtmlMapData() {
		if(mapData == null)
		{
			mapData = new ImageMapData("sequence" + hashCode(), getImageHeight())
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void populateImageMapData() {

					// not implememented yet!
				}
			};
		}
		return mapData;
	}

	public void calcScale(SequenceImageIF image){
		int val = image.getImageWidth() - image.getImageWidthOffset();		
		scale = val / (float) getSequence().getSequenceLength(DBREF);
		
	}

	public int getWidth(){
		
		int width = Math.round(scale);
		
		if (width == 0)
			width = 1;
		
		return width;
		
	}
	
	public int seq2px(int seqPos,SequenceImageIF image){
		
		int xPos = image.getImageWidthOffset();
		
		xPos += (seqPos-1) * getWidth();
		return xPos;
	}


	@Override
	protected void drawData(Graphics2D g2, int yOffset)
	{
		final SequenceImageIF image = getImage();
		
		calcScale(image);
		
		// the size of 1 amino acid in this line...
		int width = getWidth();
		
		//PdbLogger.warn(image.getImageWidth() + " " + yOffset + " " + getSequence().getSequenceLength(DBREF) +" width: " + width + " " );

		final int imageHeight = getImageHeight();
		//      g2.drawString(sequence.getSequenceString(), 0, fontAscent);
		
	
		
		for(ResidueId r : getSequence().getResidueIds(DBREF))
		{
			int xPos = seq2px(r.getSeqId(), image);		

			int height = this.getAnnotationHeight();
			
			// disulphide first
			if(hasDisulphides && disulphides != null && disulphides.getAnnotation(r) != null)
			{
				g2.setColor(disulphideColor);
				g2.fillOval(xPos, yOffset + (imageHeight * 1/10), width, imageHeight * 8/10);				
			}

			g2.setColor(r.hasStructuralData() ? residueWithStructure : residueNoStructure);

			if(r.hasDbrefMismatch())
			{
				g2.setColor(residueUniProtMismatch);
				g2.fillRect(xPos, yOffset + (imageHeight * 1/10),  width,  (imageHeight * 8/10));
			}

			if(r.isNonStandard())
			{
				g2.setColor(residueNonstandard);
				g2.fillRect(xPos, yOffset + (imageHeight * 1/10),  width,  (imageHeight * 8/10));
			}


			
			//PdbLogger.warn("drawing rectanlge" + xPos + " " + yOffset + " " + width + " " + height);
			//g2.drawRect(xPos, yOffset, width, height);
			g2.drawRect(xPos, yOffset + (imageHeight * 1/10),  width,  (imageHeight * 8/10));
		
			//g2.drawString(String.valueOf(r.getResidueInfo().getOneLetterCode()), xPos, yOffset + image.getFontAscent());
		
		}
		
		
	}

	@Override
	protected String getKey() 
	{
		switch(rns)
		{
		case DBREF:
			return getSequence().getExternalDbName();
		case SEQRES:
			return "PDB";
		default:
			return "INTERNAL SEQ";
		}
	}

	public Map<ResidueId, Point> getDisulphidePositions() {
		return disulphidePositions;
	}

	protected ResidueNumberScheme getResidueNumberScheme()
	{
		return rns;
	}



}
