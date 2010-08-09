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

package org.rcsb.sequence.ptm;

import java.io.Serializable;

import org.biojava3.protmod.structure.ModifiedCompound;

import org.rcsb.sequence.core.AbstractAnnotationValue;

public class CrosslinkValue 
extends AbstractAnnotationValue<ModifiedCompound>
implements Serializable {
   
   /**
    * 
    */
   private static final long serialVersionUID = 4075350521307278791L;
   public final ModifiedCompound modComp;
   
   public CrosslinkValue(final ModifiedCompound modComp)
   {
      this.modComp = modComp;
   }
   
   public String getDescription() {
      return "Crosslink:\n" + modComp.toString();
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
   
}
