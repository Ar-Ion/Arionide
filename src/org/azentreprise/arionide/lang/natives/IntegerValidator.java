/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.lang.natives;

import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.Validator;

public class IntegerValidator implements Validator {
	
	private static final Map<Character, String> validators = new HashMap<>();
	
	static {
		validators.put('d', "^(-)?[0-9]+$");
		validators.put('h', "^(-)?[A-Fa-f0-9]+$");
		validators.put('b', "^(-)?[01]+$");
	}
	
	public boolean validate(String data) {
		if(data.startsWith(SpecificationElement.VAR)) {
			return true;
		} else if(data != null && data.length() > 0) {
			int index = data.indexOf(SpecificationElement.ALIAS);
			
			if(index > -1) {
				data = data.substring(index + 3);
			}
			
			String validator = validators.get(data.charAt(0));
			return validator != null && data.substring(1).matches(validator);
		}
		
		return false;
	}
}