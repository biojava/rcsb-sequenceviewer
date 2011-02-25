/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Aug 6, 2010
 * Author: Jianjiong Gao 
 *
 */

package org.rcsb.sequence.annotations;

import java.io.Serializable;

import java.util.Iterator;
import java.util.TreeSet;

import org.biojava3.protmod.structure.ModifiedCompound;
import org.biojava3.protmod.structure.StructureGroup;

import org.rcsb.sequence.core.AbstractAnnotationValue;

public class ProtModValue 
extends AbstractAnnotationValue<ModifiedCompound>
implements Serializable, Comparable<ProtModValue> {
   private static final long serialVersionUID = 6085028925723776780L;
   
   private final ModifiedCompound modComp;
   
   public ProtModValue(final ModifiedCompound modComp)
   {
      this.modComp = modComp;
   }
   
   public String getDescription() {
      return  modComp.getDescription() ;
   }

   public Character toCharacter() {
      return ' '; // TODO: how to implement this?
   }
   
   @Override
   public String toString()
   {
      return getDescription();
   }

   public ModifiedCompound value() {
      return modComp;
   }
   
   public int compareTo(ProtModValue aValue) {
	   
	   TreeSet<StructureGroup> groups1 = new TreeSet<StructureGroup>(modComp.getGroups());
	   TreeSet<StructureGroup> groups2 = new TreeSet<StructureGroup>(aValue.modComp.getGroups());
	   
	   Iterator<StructureGroup> it1 = groups1.iterator();
	   Iterator<StructureGroup> it2 = groups2.iterator();
	   
	   while (it1.hasNext() && it2.hasNext()) {
		   StructureGroup g1 = it1.next();
		   StructureGroup g2 = it2.next();
		   if (!g1.equals(g2)) {
			   return g1.compareTo(g2);
		   }
	   }
	   
	   if (it1.hasNext())
		   return 1;
	   
	   if (it2.hasNext())
		   return -1;

           if (modComp!=aValue.modComp)
               return 1;
	   
	   return 0;
   }
   
   
}
