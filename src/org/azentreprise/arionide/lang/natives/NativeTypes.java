package org.azentreprise.arionide.lang.natives;

import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.lang.Types;

public class NativeTypes implements Types {

	public static final int VAR = 0x0;
	public static final int REF = 0x1;
	public static final int INT = 0x2;
	public static final int STR = 0x3;
	public static final int OBJ = 0x4;
	
	public TypeManager getTypeManager(int type) {		
		switch(type) {
			case VAR:
				break;
			case REF:
				break;
			case INT:
				break;
			case STR:
				break;
			case OBJ:
				break;
			default:
				throw new IllegalArgumentException("Invalid type");
		}
		
		return null;
	}
}