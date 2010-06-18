package org.rcsb.sequence.view.html;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.rcsb.sequence.annotations.SecondaryStructureValue;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;


public class SecondaryStructureSummary extends AnnotationSummaryCell<Character> {

	protected int helixCount = 0, helixResidueCount = 0, strandCount = 0, strandResidueCount = 0, totalResidues = 0;

	public SecondaryStructureSummary(AnnotationGroup<Character> ag) {
		super(ag);
	}

	@Override
	protected Collection<SecondaryStructureValue> annotationValues() {
		//    first we need to count how many helices/sheets there are

		Integer val;

		// we need to combine stats for GHI into 'helices' and EB into 'sheets'
		for(SecondaryStructureValue ssv : SecondaryStructureValue.values())
		{
			switch(ssv)
			{
			case G:
			case H:
				helixCount         += (val = ag.getAnnotationValueCount().get(ssv)) == null ? 0 : val;
				helixResidueCount  += (val = ag.getResiduesPerAnnotationValue().get(ssv)) == null ? 0 : val;
			case I:
				helixCount         += (val = ag.getAnnotationValueCount().get(ssv)) == null ? 0 : val;
				helixResidueCount  += (val = ag.getResiduesPerAnnotationValue().get(ssv)) == null ? 0 : val;
				break;
			case B:
			case E:
				strandCount        += (val = ag.getAnnotationValueCount().get(ssv)) == null ? 0 : val;
				strandResidueCount += (val = ag.getResiduesPerAnnotationValue().get(ssv)) == null ? 0 : val;
				break;
			case S:
			case T:
			case empty:
			case error:
			default :
				// do nothing
			}
		}


		totalResidues = ag.getSequence().getSequenceLength();

		Collection<SecondaryStructureValue> annotationValues = new LinkedList<SecondaryStructureValue>(AV_COL);
		if(helixCount  == 0) annotationValues.remove(SecondaryStructureValue.H);
		if(strandCount == 0) annotationValues.remove(SecondaryStructureValue.E);

		return annotationValues;
	}

	//   @SuppressWarnings("cast")
	@Override
	protected void renderAnnotation(AnnotationValue<Character> av, HtmlElement el) {
		SecondaryStructureValue ssv = (SecondaryStructureValue)av;
		StringBuilder someContent;
		int resCount, elementCount, percent;
		String ssName, elementName;

		switch(ssv)
		{
		case H:
			ssName = "helical";
			elementName = "helices";
			resCount = helixResidueCount;
			elementCount = helixCount;
			break;
		case E:
			ssName = "beta sheet";
			elementName = "strands";
			resCount = strandResidueCount;
			elementCount = strandCount;
			break;
		default:
			throw new RuntimeException("Only helix and sheet allowed");
		}

		percent = (int)(100 * (float)resCount / (float)totalResidues);
		someContent = new StringBuilder();
		someContent.append(percent)
		.append('%')
		.append(' ')
		.append(ssName)
		.append(' ')
		.append('(')
		.append(elementCount)
		.append(' ')
		.append(elementName)
		.append(';')
		.append(' ')
		.append(resCount)
		.append(" residues)");

		el.replaceContent(someContent);
	}

	protected static final Collection<SecondaryStructureValue> AV_COL;
	static
	{
		Collection<SecondaryStructureValue> foo = new LinkedList<SecondaryStructureValue>();
		foo.add(SecondaryStructureValue.H);
		foo.add(SecondaryStructureValue.E);
		AV_COL = Collections.unmodifiableCollection(foo);
	}
}
