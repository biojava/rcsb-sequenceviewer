package org.rcsb.sequence.model;




/**
 * Flyweight pattern class for <tt>ResidueInfo</tt> information. More correctly it is for 'chemical componenets', since this data is obtained
 * from the chemical component dictionary
 * @author mulvaney
 * @see <a href="http://deposit.pdb.org/cc_dict_tut.html">Chemical component dictionary site</a>
 *
 */
public interface ResidueInfo {

	public boolean equals(Object obj) ;

	public String getFormula();

	/**
	 * Get this <tt>ResidueInfo</tt>'s formula weight
	 * @return
	 */
	public Float getFormulaWeight();

	/**
	 * Get this <tt>ResidueInfo</tt>'s {@link ResidueType}
	 * @return
	 */

	public ResidueType getType();

	public boolean isNonstandard();

	public String getName();

	public String getParentMonId();

	public String getMonId();

	public Character getOneLetterCode();

}