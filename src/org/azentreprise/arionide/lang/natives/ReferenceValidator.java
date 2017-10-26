package org.azentreprise.arionide.lang.natives;

import org.azentreprise.arionide.lang.Validator;

public class ReferenceValidator implements Validator {
	public boolean validate(String data) {
		try {
			return Integer.parseInt(data) > -1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}