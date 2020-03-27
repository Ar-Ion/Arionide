/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.lang.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Enumeration extends AtomicValue {
	
	private static final long serialVersionUID = 1209877426348042694L;

	private final List<String> nameMapping = new ArrayList<>();
	private final Map<String, Information> possibleValues = new HashMap<>();

	private Information value;
	
	public void addPossibleValue(String name) {
		nameMapping.add(name);
		possibleValues.put(name, new Information());
	}
	
	/*
	 * Method should be accessed only by EnumerationManager
	 */
	public Information getValue(String name) {
		return possibleValues.get(name);
	}
	
	public void setValue(String name) {
		this.value = possibleValues.get(name);
	}
	
	public Information getValue() {
		if(value != null) {
			return value;
		} else if(!nameMapping.isEmpty()) {
			return possibleValues.get(nameMapping.get(0));
		} else {
			throw new IllegalStateException("Empty enumerations cannot be resolved");
		}
	}
	
	protected Stream<Bit> getRawStream() {
		return getValue().getRawStream();
	}

	public AtomicValue clone() {
		Enumeration enumeration = new Enumeration();
		enumeration.nameMapping.addAll(nameMapping);
		enumeration.possibleValues.putAll(possibleValues);
		return enumeration;
	}
}