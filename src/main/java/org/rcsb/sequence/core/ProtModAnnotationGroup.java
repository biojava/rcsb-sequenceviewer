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
 * Created on Aug 8, 2010
 * Author: Jianjiong Gao 
 *
 */

package org.rcsb.sequence.core;


import java.util.Set;

import org.biojava.nbio.protmod.structure.ModifiedCompound;
import org.rcsb.sequence.model.AnnotationGroup;


public interface ProtModAnnotationGroup extends AnnotationGroup<ModifiedCompound> {

    public Set<ModifiedCompound> getModCompounds();
}
