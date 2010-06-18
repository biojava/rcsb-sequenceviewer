package org.rcsb.sequence.view.image;


import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;

public class LineRuler extends RulerImpl {

	float scale;
	
	public LineRuler(SequenceImageIF image, Sequence sequence,
			ResidueNumberScheme rns, boolean shouldGoAbove) {
		super(image, sequence, rns, shouldGoAbove);
		
		scale = 1.0f;
		
	}
	
	
	public void calcScale(SequenceImageIF image){
		int val = image.getImageWidth() - image.getImageWidthOffset();		
		scale = val / (float) getSequence().getSequenceLength();
		
	}

	public int getWidth(){
		
		int width = Math.round(scale);
		
		if (width == 0)
			width = 1;
		
		return width;
		
	}
	
	

}
