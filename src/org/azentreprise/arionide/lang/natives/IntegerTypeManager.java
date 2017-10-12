package org.azentreprise.arionide.lang.natives;

import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.TypeManager;

public class IntegerTypeManager implements TypeManager {
	
	private static final String decimal = "Decimal";
	private static final String binary = "Binary";
	private static final String hexadecimal = "Hexadecimal";
	
	public List<String> getSuggestions(CoreDataManager cdm) {
		return null;
	}

	public List<String> getActions(CoreDataManager cdm) {
		return Arrays.asList(decimal, binary, hexadecimal);
	}

	public void onAction(String action) {
		switch(action) {
		case decimal:
			break;
		case binary:
			break;
		case hexadecimal:
			break;
		}
	}
}