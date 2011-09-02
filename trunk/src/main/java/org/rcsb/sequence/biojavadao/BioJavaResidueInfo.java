package org.rcsb.sequence.biojavadao;

import org.biojava.bio.structure.io.mmcif.model.ChemComp;
import org.rcsb.sequence.model.ResidueInfo;
import org.rcsb.sequence.model.ResidueType;

public class BioJavaResidueInfo implements ResidueInfo {

	private ChemComp cc ;
	
	public BioJavaResidueInfo(){
		cc = null;
	}
	
	public void setChemComp(ChemComp cc ){
		this.cc = cc;
	}
	
	
	public String getFormula() {
		return cc.getFormula();
	}

	public Float getFormulaWeight() {
		return Float.parseFloat(cc.getFormula_weight());
	}

	public String getMonId() {
		return cc.getId();
	}

	public String getName() {
		return cc.getName();
	}

	public Character getOneLetterCode() {
		return cc.getOne_letter_code().charAt(0);
	}

	public String getParentMonId() {
			return cc.getMon_nstd_parent_comp_id();
	}

	public ResidueType getType() {
		org.biojava.bio.structure.io.mmcif.chem.ResidueType rtype = cc.getResidueType();
		return ResidueType.getResidueTypeFromString(rtype.name());
	}

	public boolean isNonstandard() {
	
		return (! cc.isStandard());
	}

}
