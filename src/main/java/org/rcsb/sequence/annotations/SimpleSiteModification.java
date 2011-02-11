package org.rcsb.sequence.annotations;


import java.util.Set;

import org.biojava3.protmod.ModificationCategory;
import org.biojava3.protmod.ModificationCondition;
import org.biojava3.protmod.ModificationOccurrenceType;
import org.biojava3.protmod.ProteinModification;



public class SimpleSiteModification implements ProteinModification{
	
		String id;
		ModificationCategory category;
		ModificationCondition condition;
		ModificationOccurrenceType occurrence;
		String description = null;
		private Set<String> keywords;
		
		
		public void setDescription(String desc){
			this.description = desc;
		}
		public String toString(){
			if ( description == null)
				return getId();
			return description;
		}
		
		public void setKeywords(Set<String> keywords) {
			this.keywords = keywords;
		}

		public void setId(String id) {
			this.id = id;
		}

		public ModificationOccurrenceType getOccurrence() {
			return occurrence;
		}

		public void setOccurrence(ModificationOccurrenceType occurrence) {
			this.occurrence = occurrence;
		}

		public void setCategory(ModificationCategory category) {
			this.category = category;
		}

		public void setCondition(ModificationCondition condition) {
			this.condition = condition;
		}

		
		public ModificationCategory getCategory() {
			return category;
		}

		
		public ModificationCondition getCondition() {
			return condition;
		}

		
		public String getDescription() {
			return description;
		}

		
		public String getFormula() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getId() {
			return id;
		}

		
		public Set<String> getKeywords() {
			return keywords;
		}

		
		public ModificationOccurrenceType getOccurrenceType() {
			return occurrence;
		}

		
		public String getPdbccId() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getPdbccName() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getPsimodId() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getPsimodName() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getResidId() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getResidName() {
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getSystematicName() {
			// TODO Auto-generated method stub
			return null;
		}

	

}
