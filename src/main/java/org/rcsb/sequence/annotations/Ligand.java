package org.rcsb.sequence.annotations;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.rcsb.sequence.core.ResidueProvider;

/**
 * Simple representation of a ligand
 * @author mulvaney
 *
 */
public class Ligand implements Comparable<Ligand>, Serializable
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private final String structureId;
   private final String chainId;
   private final org.rcsb.sequence.model.ResidueInfo residueInfo;
   
   private static final Map<String, Ligand> ligCache = new HashMap<String, Ligand>();
   
   public static Ligand get(String structureId, String chainId, String monId)
   {
      String key = structureId + chainId + monId;
      Ligand result;
      synchronized(ligCache)
      {
         result = ligCache.get(key);
         if(result == null)
         {
            result = new Ligand(structureId, chainId, monId);
            ligCache.put(key, result);
         }
      }
      return result;
   }
   
   private Ligand(String structureId, String chainId, String monId)
   {
      this.structureId = structureId;
      this.chainId = chainId;
      this.residueInfo = ResidueProvider.getResidue(monId);
   }

   public String getStructureId() {
      return structureId;
   }

   public String getChainId() {
      return chainId;
   }

   public org.rcsb.sequence.model.ResidueInfo getResidueInfo() {
      return residueInfo;
   }
   
   public String getMonId() {
      return residueInfo.getMonId();
   }
   
   private String toString = null;
   public String toString()
   {
      if(toString == null)
      {
         toString = structureId + chainId + "_" + residueInfo.getMonId();
      }
      return toString;
   }

   private int hashCode = -1;
   @Override
   public int hashCode() 
   {
      if(hashCode == -1)
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((chainId == null) ? 0 : chainId.hashCode());
         result = prime * result
               + ((residueInfo == null) ? 0 : residueInfo.hashCode());
         hashCode = result;
      }
      return hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Ligand other = (Ligand) obj;
      if (chainId == null) {
         if (other.chainId != null)
            return false;
      } else if (!chainId.equals(other.chainId))
         return false;
      if (residueInfo == null) {
         if (other.residueInfo != null)
            return false;
      } else if (!residueInfo.equals(other.residueInfo))
         return false;
      return true;
   }

   private static final int BEFORE = -1;
   private static final int EQUAL  =  0;
   
   public int compareTo(Ligand o) {
      if(o == null) return BEFORE;
      if(o == this) return EQUAL;
      
      int comparison;
      
      comparison = structureId.compareTo(o.structureId);
      if(comparison != EQUAL) return comparison;
      
      comparison = chainId.compareTo(o.chainId);
      if(comparison != EQUAL) return comparison;
      
      comparison = residueInfo.getMonId().compareTo(o.residueInfo.getMonId());
      if(comparison != EQUAL) return comparison;
      
      //PdbLogger.debug("equal but non-identical Ligand objects found");
      
      return EQUAL;
   }
   
   
}
