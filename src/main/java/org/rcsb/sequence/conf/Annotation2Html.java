package org.rcsb.sequence.conf;


import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.html.AnnotationSummaryCell;
import org.rcsb.sequence.view.html.CSASummary;
import org.rcsb.sequence.view.html.DomainSummary;
import org.rcsb.sequence.view.html.LigCRSummary;
import org.rcsb.sequence.view.html.SecondaryStructureSummary;
import org.rcsb.sequence.view.image.AnnotationDrawer;
import org.rcsb.sequence.view.image.AuthorSecondaryStructureDrawer;
import org.rcsb.sequence.view.image.BoxAnnotationDrawer;
import org.rcsb.sequence.view.image.LabelledBoxAnnotationDrawer;
import org.rcsb.sequence.view.image.SecondaryStructureDrawer;
import org.rcsb.sequence.view.image.SequenceImage;

public class Annotation2Html {

	public static final Map<String, Class<? extends AnnotationDrawer>> ANNOTATION_TO_RENDERER_MAP;
	
	private static final Map<String, Class<? extends AnnotationSummaryCell<?>>> ANNOTATION_CLASSIFICATION_TO_SUMMARY_TABLE_MAP;

	static
	{


		/*
		 * Put a mapping from your annotation name to your annotation renderer here
		 */
		Map<String, Class<? extends AnnotationDrawer>> a2rMap = 
			new LinkedHashMap<String, Class<? extends AnnotationDrawer>>();
		/*
		 * Map annotation classifications to annotatoin summary html generator class
		 */
		Map<String, Class<? extends AnnotationSummaryCell<?>>> a2sMap = 
			new LinkedHashMap<String, Class<? extends AnnotationSummaryCell<?>>>();

		for (AnnotationName an : AnnotationRegistry.getAllAnnotations()) {
			if ( an.getName().equals("dssp")) {
				a2rMap.put("dssp", SecondaryStructureDrawer.class);    	  
				a2sMap.put("dssp", SecondaryStructureSummary.class);
			}
			else if ( an.getName().equals("stride")){
				a2rMap.put("stride", SecondaryStructureDrawer.class);
				a2sMap.put("stride", SecondaryStructureSummary.class);
			}
			else if  ( an.getName().equals("authorSecStr")) {
				a2rMap.put("authorSecStr", AuthorSecondaryStructureDrawer.class);
				a2sMap.put("authorSecStr", SecondaryStructureSummary.class);
			}
			else if ( an.getName().equals("scop")) {
				a2rMap.put("scop", LabelledBoxAnnotationDrawer.class);
				a2sMap.put("scop", DomainSummary.class);
			}
			else if ( an.getName().equals("cath")) {          
				a2rMap.put("cath", LabelledBoxAnnotationDrawer.class);
				a2sMap.put("cath", DomainSummary.class);
			}
			else if ( an.getName().equals("proteinDomainParser")) {
				a2rMap.put("proteinDomainParser" , LabelledBoxAnnotationDrawer.class);
				a2sMap.put("proteinDomainParser", DomainSummary.class);
			}
			else if ( an.getName().equals("domainParser")) {
				a2rMap.put("domainParser"  , LabelledBoxAnnotationDrawer.class);
				a2sMap.put("domainParser", DomainSummary.class);
			}
			else if ( an.getName().equals("pfam")) {
				a2rMap.put("pfam", LabelledBoxAnnotationDrawer.class);
				a2sMap.put("pfam", DomainSummary.class);
			}
			else if ( an.getName().equals("interpro")) {
				a2rMap.put("interpro", LabelledBoxAnnotationDrawer.class);
				a2sMap.put("interpro", DomainSummary.class);
			}
			else if ( an.getName().equals("csa")) {
				a2rMap.put("csa", BoxAnnotationDrawer.class);
				a2sMap.put("csa", CSASummary.class);
			}
			else if ( an.getName().equals("ligcr")){
				a2rMap.put("ligcr", BoxAnnotationDrawer.class);
				a2sMap.put("ligcr", LigCRSummary.class);
			}
		}
		ANNOTATION_TO_RENDERER_MAP = 
			Collections.unmodifiableMap(a2rMap);
		
		ANNOTATION_CLASSIFICATION_TO_SUMMARY_TABLE_MAP = 
			Collections.unmodifiableMap(a2sMap);
	}


		
		public static boolean hasSummaryTableRow(String an)
		{
			return ANNOTATION_CLASSIFICATION_TO_SUMMARY_TABLE_MAP.containsKey(an);
		}

		public static AnnotationSummaryCell<?> createSummaryTableRowInstance(AnnotationGroup<?> ag)
		{
			
			try {
				Class<? extends AnnotationSummaryCell<?>> cl = ANNOTATION_CLASSIFICATION_TO_SUMMARY_TABLE_MAP.get(ag.getName().getName());
				Constructor<? extends AnnotationSummaryCell<?>> c = cl.getConstructor(AnnotationGroup.class);
				return c.newInstance(ag);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate summary table row for AnnotationGroup " 
						+ ag.getName().getName() + " on chain " + ag.getSequence().getChainId(), e);
			}
		}

		public static AnnotationDrawer createAnnotationRenderer(SequenceImage sequenceImage, AnnotationName an, Sequence s)
		{
			AnnotationDrawer r = null;
			try {
				r = ANNOTATION_TO_RENDERER_MAP.get(an.getName()).getConstructor(SequenceImage.class, Sequence.class, Class.class).newInstance(sequenceImage, s, an.getAnnotationClass());
			} 
			catch (Exception e)
			{
				System.err.println("Could not instantiate Renderer for " + an + " " + an.getName() + " on " + s + " " +  e.getMessage());
				e.printStackTrace();
			}
			return r;
		}
	}
