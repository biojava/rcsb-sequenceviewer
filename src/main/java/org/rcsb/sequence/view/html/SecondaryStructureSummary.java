package org.rcsb.sequence.view.html;

import java.util.Collection;
import java.util.LinkedList;

import org.rcsb.sequence.annotations.SecondaryStructureType;
import org.rcsb.sequence.annotations.SecondaryStructureValue;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;


public class SecondaryStructureSummary extends AnnotationSummaryCell<String> {

	protected int helixCount = 0;
	protected int helixResidueCount = 0;
	protected int strandCount = 0;
	protected int strandResidueCount = 0;
	protected int totalResidues = 0;

	public SecondaryStructureSummary(AnnotationGroup<String> ag) {
		super(ag);
		
	}

	@Override
	protected Collection<SecondaryStructureValue> annotationValues() {
		// make sure the values are reset
		helixCount = 0;
		helixResidueCount = 0;
		strandCount = 0;
		strandResidueCount = 0;
		totalResidues = 0;
		
		//    first we need to count how many helices/sheets there are

		Integer val;
		
		// we need to combine stats for GHI into 'helices' and EB into 'sheets'
		for(SecondaryStructureType ssv : SecondaryStructureType.allTypes)
		{
			//System.out.println(ssv);
			//System.out.println(ag.getAnnotationValueCount().get(ssv));
			switch(ssv)
			{
			case G:
			case H:
				helixCount         += (val = ag.getAnnotationValueCount().get(new SecondaryStructureValue(ssv))) == null ? 0 : val;
				helixResidueCount  += (val = ag.getResiduesPerAnnotationValue().get(new SecondaryStructureValue(ssv))) == null ? 0 : val;
				break;
			case I:
				helixCount         += (val = ag.getAnnotationValueCount().get(new SecondaryStructureValue(ssv))) == null ? 0 : val;
				helixResidueCount  += (val = ag.getResiduesPerAnnotationValue().get(new SecondaryStructureValue(ssv))) == null ? 0 : val;
				break;
			case B:
			case E:
				strandCount        += (val = ag.getAnnotationValueCount().get(new SecondaryStructureValue(ssv))) == null ? 0 : val;
				strandResidueCount += (val = ag.getResiduesPerAnnotationValue().get(new SecondaryStructureValue(ssv))) == null ? 0 : val;
				break;
			case S:
			case T:
			case empty:
			case error:
			default :
				// do nothing
			}
					
			//System.out.println("   ssvalue: " + ssv + " helixCount: " + helixCount + " helixResidueCount: " + helixResidueCount );
		
		}

		totalResidues = ag.getSequence().getSequenceLength();

		Collection<SecondaryStructureValue> annotationValues = new LinkedList<SecondaryStructureValue>();
		if(helixCount  > 0)  annotationValues.add   ( new SecondaryStructureValue( SecondaryStructureType.H));
		if(strandCount > 0) annotationValues.add( new SecondaryStructureValue( SecondaryStructureType.E));

		//System.out.println("SecondaryStructureSummary: helixCount : " + helixCount + " helixResidueCount: " + helixResidueCount + " totalResidues: " + totalResidues);
		
		return annotationValues;
	}

	//   @SuppressWarnings("cast")
	@Override
	protected void renderAnnotation(AnnotationValue<String> av, HtmlElement el) {
		// convention: first char of value in annotations is always secstruc...
		SecondaryStructureType ssv = SecondaryStructureType.getTypeFromCharCode(av.value().charAt(0));
		
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
			System.err.println("Only helix and sheet allowed, but got " + ssv);
			return;
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

//	protected static final Collection<SecondaryStructureValue> AV_COL;
//	static
//	{
//		Collection<SecondaryStructureValue> foo = new LinkedList<SecondaryStructureValue>();
//		
//		//foo.add(SecondaryStructureType.H);
//		//foo.add(SecondaryStructureType.E);
//		AV_COL = Collections.unmodifiableCollection(foo);
//	}
}
