package org.rcsb.sequence.biojavadao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biojava.bio.structure.Compound;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureException;
import org.rcsb.sequence.model.Chain;
import org.rcsb.sequence.model.PolymerType;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.SegmentedSequence;
import org.rcsb.sequence.model.SequenceCollection;

public class BioJavaSequenceCollection implements SequenceCollection {
	Structure s;
	
	public BioJavaSequenceCollection(){
		s = null;
	}
	
	public void setStructure(Structure s){
		this.s = s;
	}
	public int chainCount() {
		return s.size();
	}

	public boolean containsChain(String chainId) {
		return s.hasChain(chainId);
	}

	public void destroy() {
		s = null;

	}

	public Chain getChain(String chainId) {
		try {
		org.biojava.bio.structure.Chain c = s.getChainByPDB(chainId);
		
		Chain bjc = new BioJavaChainProxy(c);
		return bjc;
		} catch ( StructureException e){
			e.printStackTrace();
			return null;
		}
	}

	public Chain getChainByPDBID(String chainId) {
		try {
			org.biojava.bio.structure.Chain c = s.getChainByPDB(chainId);
			
			Chain bjc = new BioJavaChainProxy(c);
			return bjc;
			} catch ( StructureException e){
				e.printStackTrace();
				return null;
			}
	}


	public Map<String, Chain> getChains() {
		Map<String, Chain> m = new HashMap<String, Chain>();
		
		for ( org.biojava.bio.structure.Chain c : s.getChains()){
			Chain bjc = new BioJavaChainProxy(c);
			m.put(c.getName(), bjc);
		}
		
		return m;
	}

	public Collection<Chain> getChains(Integer entityId) {
		List<Chain> chains = new ArrayList<Chain>();
		
		List<Compound> ccs = s.getCompounds();
		if ( ccs.size() < entityId)
			return null;
		Compound comp = ccs.get(entityId);
				
		List<org.biojava.bio.structure.Chain> bjchains= comp.getChains();
		
		for ( org.biojava.bio.structure.Chain bjchain: bjchains){
			Chain proxiedC = new BioJavaChainProxy(bjchain);
			chains.add(proxiedC);
		}
		
		return chains;
	}

	public Map<String, Chain> getFirstChainFromEachEntityMap() {
		Map<String,Chain> chains = new HashMap<String,Chain>();
		
		for ( Compound c : s.getCompounds() ) {
			List<org.biojava.bio.structure.Chain> bjchains= c.getChains();
			if ( bjchains.size() > 0){
				Chain bjc = new BioJavaChainProxy(bjchains.get(0));
				chains.put(bjchains.get(0).getName(),bjc);
			}
		}
			
		return chains;
	}

	
	public String getStructureId() {
		return s.getPDBCode();
	}

	public String getStructureTitle() {
		return s.getPDBHeader().getTitle();
	}
	
	public Map<PolymerType, Collection<Chain>> getPolymerTypeChainMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public SegmentedSequence getSegmentedSequence(ResidueNumberScheme rns,
			String chainId, int fragmentLength) {
		// TODO Auto-generated method stub
		return null;
		
	}

	

}
