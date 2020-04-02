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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/*
 * Runtime variable
 */
public class Variable extends AtomicValue {

	private static final long serialVersionUID = 8748976008265242711L;
	
	private String name;
	private Node initialValue = new Numeric(); // Static variable allocation
	
	public Variable() {
		label(null);
	}
	
	public void label(String name) {
		this.name = (name != null && !name.isEmpty()) ? name : "Lambda variable";
	}
	
	public void setInitialValue(Node initialValue) {
		this.initialValue = initialValue;
	}
	
	public Node getInitialValue() {
		return initialValue;
	}
	
	public Stream<Bit> getRawStream() {
		return initialValue.getRawStream();
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList(name, "(" + initialValue.getSize() + " bit(s))");
	}
	
	public Variable clone() {
		Variable clone = new Variable();
		
		clone.initialValue = initialValue.clone();
		
		return clone;
	}
}
