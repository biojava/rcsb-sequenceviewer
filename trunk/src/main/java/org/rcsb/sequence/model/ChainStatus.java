package org.rcsb.sequence.model;

import java.io.Serializable;


/**
 * Enumerates the possible states of a {@link Chain}
 * @author mulvaney
 *
 */
public enum ChainStatus implements Serializable{

   instantiated, building, residues, dbRef, done, destroyed;
   
}
