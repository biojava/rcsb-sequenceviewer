package org.rcsb.sequence.core;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.html.AnnotationSummaryCell;
import org.rcsb.sequence.view.multiline.AnnotationDrawer;
import org.rcsb.sequence.view.multiline.SequenceImage;

/** Map annotations to the objects that are drawing them
 * 
 * @author Andreas Prlic
 *
 */
public interface AnnotationDrawMapper {

	public void ensureInitialized();
	
	public  AnnotationDrawer createAnnotationRenderer(SequenceImage sequenceImage, AnnotationName an, Sequence s);
	
	public  AnnotationSummaryCell<?> createSummaryTableRowInstance(AnnotationGroup<?> ag);
	
	public  boolean hasSummaryTableRow(String annotationName);
	
}
