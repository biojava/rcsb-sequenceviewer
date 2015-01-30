package org.rcsb.sequence.annotations;


import java.util.Set;

import org.biojava.nbio.protmod.ModificationCategory;
import org.biojava.nbio.protmod.ModificationCondition;
import org.biojava.nbio.protmod.ModificationOccurrenceType;
import org.biojava.nbio.protmod.ProteinModification;


public class SimpleSiteModification implements ProteinModification {

    String id;
    ModificationCategory category;
    ModificationCondition condition;
    ModificationOccurrenceType occurrence;
    String description = null;
    private Set<String> keywords;

    public String toString() {
        if (description == null)
            return getId();
        return description;
    }

    public ModificationOccurrenceType getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(ModificationOccurrenceType occurrence) {
        this.occurrence = occurrence;
    }

    public ModificationCategory getCategory() {
        return category;
    }

    public void setCategory(ModificationCategory category) {
        this.category = category;
    }

    public ModificationCondition getCondition() {
        return condition;
    }

    public void setCondition(ModificationCondition condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getFormula() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
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
