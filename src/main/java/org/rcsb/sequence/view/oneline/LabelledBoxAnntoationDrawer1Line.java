package org.rcsb.sequence.view.oneline;

import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.multiline.BoxAnnotationDrawer;

import org.rcsb.sequence.view.multiline.SequenceImage;

public class LabelledBoxAnntoationDrawer1Line<T> extends BoxAnnotationDrawer<T> {

	
	public static final int annotationHeight = 20;
	
	public LabelledBoxAnntoationDrawer1Line(SequenceImage image, Sequence sequence, Class<? extends AnnotationGroup<T>> annotationGroupClass) {
		super(image, sequence, annotationGroupClass);
	}

	@Override
	protected boolean displayLabel() {
		return true;
	}


	public int getAnnotationHeight(){
		int h = super.getAnnotationHeight();
		int ret = Math.max(h,annotationHeight);

		return ret;
	}

	public void setAnnotationHeight(int h){
		super.setAnnotationHeight(Math.max(h,annotationHeight));
	}

	protected int getImageHeight(){
		int h = super.getImageHeight();

		int a = getAnnotationHeight();

		return Math.max(a,h);
	}

	public int getImageHeightPx(){
		int h = super.getImageHeightPx();
		return Math.max(h,annotationHeight);
	}
	
	protected int getLabelYpos() {
		int y = super.getLabelYpos();
		 return Math.max(y,getAnnotationHeight() / 2);
	}
	
}
