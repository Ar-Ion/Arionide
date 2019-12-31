/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.lang.natives;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.lang.DummyValidator;
import ch.innovazion.arionide.lang.TypeManager;
import ch.innovazion.arionide.lang.Types;
import ch.innovazion.arionide.lang.Validator;

public class NativeTypes implements Types {
	
	public static final int INTEGER = 0x1;
	public static final int TEXT = 0x2;
	public static final int STRUCTURE = 0x3;
	
	private final TypeManager structureTypeManager = new StructureTypeManager();
	private final TypeManager integerTypeManager = new IntegerTypeManager();
	private final TypeManager textTypeManager = new TextTypeManager();

	private final Validator structureValidator = new StructureValidator();
	private final Validator integerValidator = new IntegerValidator();
	private final Validator textValidator = new DummyValidator();

	public TypeManager getTypeManager(int type) {		
		switch(type) {
			case STRUCTURE:
				return this.structureTypeManager;
			case INTEGER:
				return this.integerTypeManager;
			case TEXT:
				return this.textTypeManager;
		}
		
		return null;
	}
	
	public Validator getValidator(int type) {
		switch(type) {
		case STRUCTURE:
			return this.structureValidator;
		case INTEGER:
			return this.integerValidator;
		case TEXT:
			return this.textValidator;
		}
		
		return null;
	}
	
	public List<String> getAvailableTypes() {
		return Arrays.asList("Structure", "Integer", "Text");
	}
}