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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Enumeration extends AtomicValue {
	
	private static final long serialVersionUID = 1209877426348042694L;

	private final List<String> nameMapping = new ArrayList<>();
	private final Map<String, Node> possibleValues = new HashMap<>();

	private Node value;
	
	public Enumeration() {
		super("Enumeration");
	}
	
	private Enumeration(Enumeration parent) {
		super(parent);
		nameMapping.addAll(parent.nameMapping);
		possibleValues.putAll(parent.possibleValues);
	}
	
	/*
	 * Those two methods should be accessed by a parameter creator
	 */
	public void addPossibleValue(String name) {
		addPossibleValue(name, new Node(name));
	}
	
	public void addPossibleValue(String name, Node value) {
		nameMapping.add(name);
		possibleValues.put(name, value);
	}
	
	public void removePossibleValue(String name) {
		nameMapping.remove(name);
		possibleValues.remove(name);
	}
	

	public Node getValue(String name) {
		return possibleValues.get(name);
	}
	
	public void setValue(String name) {
		this.value = possibleValues.get(name);
	}
	
	public List<String> getNames() {
		return Collections.unmodifiableList(nameMapping);
	}
	
	public Node getValue() {
		if(value != null) {
			return value;
		} else if(!nameMapping.isEmpty()) {
			return possibleValues.get(nameMapping.get(0));
		} else {
			throw new IllegalStateException("Empty enumerations cannot be resolved");
		}
	}
	
	public int getSize() {
		if(value != null) {
			return value.getSize();
		} else {
			return 0;
		}
	}
	
	protected Stream<Bit> getRawStream() {
		return getValue().getRawStream();
	}

	public Enumeration clone() {
		return new Enumeration(this);
	}
}