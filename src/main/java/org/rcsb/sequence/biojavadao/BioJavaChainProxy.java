package org.rcsb.sequence.biojavadao;


import static org.rcsb.sequence.model.PolymerType.PROTEIN_ONLY;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.biojava.bio.structure.Compound;
import org.biojava.bio.structure.Group;

import org.biojava.bio.structure.ResidueNumber;
import org.biojava.bio.structure.Structure;
//import org.biojava.bio.structure.StructureTools;

import org.biojava.bio.structure.io.mmcif.model.ChemComp;

import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AbstractSequence;
import org.rcsb.sequence.core.ProtModAnnotationGroup;
import org.rcsb.sequence.core.ResidueIdImpl;
import org.rcsb.sequence.core.ResidueProvider;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.Reference;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.SequenceCollection;


public class BioJavaChainProxy  extends AbstractSequence implements Chain  {

	private  PolymerType polymerType;
	AtomicBoolean instantiated;
	AtomicBoolean annotated;
	
	public BioJavaChainProxy(org.biojava.bio.structure.Chain bj){		
		super(bj.getSeqResSequence());
		System.out.println("created new BiojavaChainProxy for " + bj.getSeqResSequence());
	
		instantiated = new AtomicBoolean();
		instantiated.set(false);
		annotated = new AtomicBoolean();
		annotated.set(false);
		
		this.bj = bj;
		Group g = bj.getAtomGroup(0);
		ChemComp cc = g.getChemComp();
		polymerType = PolymerType.polymerTypeFromString(cc.getPolymerType().name());
	
		
	}

	protected BioJavaChainProxy(String sequence) {
		super(sequence);		
	}

	public Integer getSeqPosition(String pdbResNum, String insCode){
		List<Group> groups = bj.getSeqResGroups();
		int i = -1;
		String code = pdbResNum + insCode;
		
		for (Group g: groups){
			i++;
			if ( g.getPDBCode() != null) {
				if ( g.getPDBCode().equals(code)){
				return i;
				}
			}
		}
		return -1;
	}
	
	public Integer getAtomPosition(Group g){
		int pos = -1;
		for ( Group gr : bj.getAtomGroups() ) {
			pos++;
			if( gr.equals(g))
				return pos;
		}
		return -1;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4278708896903643020L;
	org.biojava.bio.structure.Chain bj = null;


	public void setBJChain(org.biojava.bio.structure.Chain bj){
		this.bj = bj;
	}

	public org.biojava.bio.structure.Chain getBJChain(){
		return bj;
	}
	
	@Override
	protected void ensureResiduesInstantiated() {
		if ( instantiated.get())
			return;
		
		instantiated.set(true);
		
		// build up the SEQRES and ATOM mappings...
		Map<ResidueNumberScheme, Map<String, ResidueId>> residueIdMaps = getResidueIdMaps();
		
		ResidueIdImpl equivResId;

		Character insertionCode;
		ResidueInfo theResidue;
	
		
		String pdbkey;
		
		Map<String, ResidueId> ARRAYMAP  = residueIdMaps.get(ResidueNumberScheme._ARRAY_IDX);
		Map<String, ResidueId> ATOMMAP   = residueIdMaps.get(ResidueNumberScheme.ATOM);
		Map<String, ResidueId> SEQRESMAP = residueIdMaps.get(ResidueNumberScheme.SEQRES);
		
		
		int index = -1;
		
				
		for (Group g : bj.getSeqResGroups()){
			index++;
			
			theResidue    = ResidueProvider.getResidue(g.getPDBName());
			
			ResidueNumber pdbResNum ;
			if ( g.getResidueNumber() != null )
				pdbResNum = g.getResidueNumber();
			else 
				pdbResNum = new ResidueNumber();
			
			if ( pdbResNum.getInsCode() != null)
				insertionCode = pdbResNum.getInsCode() ;
			else
				insertionCode = null;
			
			equivResId = new ResidueIdImpl(ResidueNumberScheme._ARRAY_IDX, this, index, theResidue);
			
			ARRAYMAP.put(String.valueOf(index),  equivResId);
			SEQRESMAP.put((index+1)+"", new ResidueIdImpl(ResidueNumberScheme.SEQRES, this, index+1, theResidue, equivResId));

			// if no pdb id, that's ok			
			int atomPos = getAtomPosition(g);
			if ( atomPos >= 0) {
				int authSeqNum       = pdbResNum.getSeqNum();
				pdbkey = authSeqNum + "" + (insertionCode == null ? "" : insertionCode);
				ATOMMAP.put(pdbkey, new ResidueIdImpl(ResidueNumberScheme.ATOM, this, 
						authSeqNum, 
						insertionCode, theResidue, equivResId));
				//System.out.println("mapped " + atomPos + " " + g );
			}
			else
			{
				System.err.println("BioJavaChainProxy: No PDB residueInfo information for " + g);
			}
			
		}
		
		linkResidues();
	}
	
	private void linkResidues()
	{
		//    for each collection of residueIds
		for( ResidueNumberScheme rns : getResidueIdMaps().keySet() )
		{
			linkResidues(rns);
		}
	}

	private void linkResidues(ResidueNumberScheme rns)
	{

		//PdbLogger.info("ChainImpl: linking " +rns);
		Map<String, ResidueId> ridMap = getResidueIdMaps().get(rns);
		if(ridMap != null)
		{
			// for each residueId..
			Iterator<ResidueId> resIt = ridMap.values().iterator();

			;
			/*
			 * Take the first residue and put it into 'prev'. we don't
			 * need to set it up with a previous residue because it's the 
			 * first in teh chain and the default previous residue is
			 * BEGINNING_OF_CHAIN
			 */
			if(resIt.hasNext())
			{
				ResidueIdImpl prev = (ResidueIdImpl) resIt.next();
				//PdbLogger.info("ChainImpl: first residue: " + prev);
				
				ResidueIdImpl cur = null;

				while(resIt.hasNext())
				{
					cur = (ResidueIdImpl) resIt.next();
					cur.setPrevious(prev);
					prev.setNext(cur);
					prev = cur;
				}
				//.info("last residue: " + cur);
			}

			/*
			 * Now that we are at the end, we don't need to do anything
			 * for the last residue because the default next residue is
			 * END_OF_CHAIN
			 */
		}

	}


	public Integer getEntityId() {
		Structure s = bj.getParent();
		List<Compound> compounds = s.getCompounds();

		int i = 0;
		for ( Compound comp : compounds){
			List<String> chainIds = comp.getChainId();
			if ( chainIds.contains(bj.getName()))
				return i;

			i++;
		}
		return -1;
	}

	public void ensureAnnotated() {
		
		if ( annotated.get())
			return;
		
		annotated.set(true);

		// build up annotation groups for 

		// secondary structure
		// TODO...

		AnnotationClassification cla = AnnotationClassification.secstr;
		Reference ssref1 = new Reference(-1L);
		AnnotationName secName = new AnnotationName(
				cla,
				BioJavaSecStrucAnnotationGroup.annotationName,
				"Author",
				ssref1,
				BioJavaSecStrucAnnotationGroup.class,
				PolymerType.PROTEIN_ONLY);
		
		AnnotationRegistry.registerAnnotation(secName);
		BioJavaSecStrucAnnotationGroup secanno =  new BioJavaSecStrucAnnotationGroup(this, cla, secName);
		try {
			secanno.constructAnnotations();
		} catch (Exception e){
			e.printStackTrace();
		}
		addAnnotationGroup(secanno);
		
		// test		
		AnnotationName scop = new AnnotationName(
				AnnotationClassification.strdom,
				TestAnnotationGroup.annotationName,
				"Structural Classification Of Proteins",
				new Reference(7723011L),
				TestAnnotationGroup.class,
				PROTEIN_ONLY
		);
		AnnotationRegistry.registerAnnotation(scop);
		TestAnnotationGroup test = new TestAnnotationGroup(this,AnnotationClassification.strdom,scop);
		try {
			test.constructAnnotations();
		}catch (Exception e){
			e.printStackTrace();
		}
		addAnnotationGroup(test);
		
		// protein modifications

		AnnotationClassification mrac = AnnotationClassification.protmod;
		Reference mrref = new Reference(-1L);

		AnnotationName mrName = new AnnotationName(
				mrac,
				BJProtModAnnotation.annotationName, 
				BJProtModAnnotation.annotationName, 
				mrref, 
				BJProtModAnnotation.class,
				PolymerType.PROTEIN_ONLY);
		
		AnnotationRegistry.registerAnnotation(mrName);
		
		ProtModAnnotationGroup mrag =
			new BJProtModAnnotation(
					this,
					mrac,
					mrName);
		
		try {
			mrag.constructAnnotations();
		} catch (Exception e) {
			e.printStackTrace();
		}
		addAnnotationGroup(mrag);
		
//		// disulfid bridges..
//
//		AnnotationClassification ac = AnnotationClassification.structuralFeature;
//		Reference ssref = new Reference(-1L);
//
//		AnnotationName ssName = new AnnotationName(
//				ac,
//				BioJavaDisulfideAnnotationGroup.annotationName, 
//				"Disulphide Bonds", 
//				ssref, 
//				BioJavaDisulfideAnnotationGroup.class, 
//				PolymerType.PROTEIN_ONLY);
//
//
//		AnnotationRegistry.registerAnnotation(ssName);
//		
//		BioJavaDisulfideAnnotationGroup disulfg =
//			new BioJavaDisulfideAnnotationGroup(
//					this,
//					ac, 
//					ssName);
//		
//		try {
//			disulfg.constructAnnotations();
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		addAnnotationGroup(disulfg);
		
	}

	public Collection<ResidueNumberScheme> getAvailableResidueNumberSchemes() {
		ensureAnnotated();
		return getResidueIdMaps().keySet();
	}

	public Chain getChain() {
		//crazy!
		return this;
	}

	public String getChainId() {
		return bj.getName();
	}

	public ResidueNumberScheme getDefaultResidueNumberScheme() {
		return ResidueNumberScheme.SEQRES;
	}

	public String getExternalDbCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExternalDbName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Character getPdbChainId() {
		return bj.getName().charAt(0);
	}

	public PolymerType getPolymerType() {

		return polymerType;
	}

	public SequenceCollection getSequenceCollection() {
		// this should be null to avoid circular references...
		return null;
	}

	public String getStructureId() {
		return bj.getParent().getPDBCode();
	}

	public int compareTo(Chain o) {
		// TODO Auto-generated method stub
		return 0;
	}





}
