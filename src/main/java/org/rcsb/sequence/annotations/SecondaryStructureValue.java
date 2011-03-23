package org.rcsb.sequence.annotations;

import java.io.Serializable;

import org.rcsb.sequence.model.AnnotationValue;

public class SecondaryStructureValue 
	implements AnnotationValue<String>, 
		Serializable, 
		Comparable<SecondaryStructureValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8186554313136644422L;

	public final char code;


	SecondaryStructureType ssType ;
	SecondaryStructureValue(String code)
	{

		ssType = SecondaryStructureType.getTypeFromCharCode(code.charAt(0));

		this.code = ssType.code;

	}

	public SecondaryStructureValue(SecondaryStructureType ssType) {
		this.ssType = ssType;
		this.code = ssType.code;

	}	
	//   SecondaryStructureValue(String description, String shortDescription)
	//   {
	//      this.code = this.name().charAt(0);
	//      this.description = description;
	//      this.shortDescription = shortDescription;
	//   }
	//   SecondaryStructureValue(String description)
	//   {
	//      this(description, description);
	//   }

	public SecondaryStructureType getType(){
		return ssType;
	}

	@Override
	public String toString() {
		return String.valueOf(code);
	}
	
	public String value() {
		return String.valueOf(code);
	}


	
	public String getUrl() {
		return "#";
	}
	public boolean isExternalData() {
		return false;
	}
	public String getDescription() {
		return ssType.description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((ssType == null) ? 0 : ssType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecondaryStructureValue other = (SecondaryStructureValue) obj;
		if (code != other.code)
			return false;
		if (ssType != other.ssType)
			return false;
		return true;
	}

	public int compareTo(SecondaryStructureValue arg0) {
		
		if ( this.equals(arg0))
			return 0;
		
		
		SecondaryStructureValue other = (SecondaryStructureValue) arg0;
		return this.ssType.compareTo(other.getType());
	}

	public Character toCharacter() {
		return code;
	}

	
	
	
	
}
