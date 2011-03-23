package org.rcsb.sequence.annotations;

public enum SecondaryStructureType {

	empty(' ', "no secondary structure assigned"),
	H("alpha helix"),
	E("extended strand, participates in beta ladder", "beta strand"),
	T("hydrogen bonded turn", "turn"),
	B("residue in isolated beta-bridge", "beta bridge"),
	S("bend"),
	G("3-helix (3/10 helix)", "3/10-helix"),
	I("5-helix (pi helix)", "pi helix"),
	error('!', "error");
	
	public static final SecondaryStructureType[] allTypes ;
	
	static {
		 allTypes = new SecondaryStructureType[values().length];

		int counter = -1;
		for (SecondaryStructureType type : values()){
			counter++;
			allTypes[counter] = type;
		}
	}
	public final char code;
	public final String description;
	public final String shortDescription;

	SecondaryStructureType(char code, String description)
	{
		this.code = code;
		this.description = description;
		this.shortDescription = description;
	}
	SecondaryStructureType(String description, String shortDescription)
	{
		this.code = this.name().charAt(0);
		this.description = description;
		this.shortDescription = shortDescription;
	}
	SecondaryStructureType(String description)
	{
		this(description, description);
	}

	public static SecondaryStructureType getTypeFromCharCode(Character code)
	{
		SecondaryStructureType result = error;

		for(SecondaryStructureType t : allTypes)
		{
			if(t.code == code)
			{
				result = t;
				break;
			}
		}

		return result;
	}
	
	public static boolean isHelical(SecondaryStructureType ssv)
	{
		return ssv == SecondaryStructureType.H || ssv == SecondaryStructureType.G || ssv == SecondaryStructureType.I;
	}

}
