package org.azentreprise.arionide.lang;

public class Function {
	
	private final int functionUID;
	private final int superiorUID;
	private final short properties;
	private final int[] inheritance;
	
	public Function(int functionUID, int superiorUID, short properties, int[] inheritance) {
		this.functionUID = functionUID;
		this.superiorUID = superiorUID;
		this.properties = properties;
		this.inheritance = inheritance;
	}
}