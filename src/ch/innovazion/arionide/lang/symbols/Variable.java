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
	
	private Information initialValue; // Static variable allocation
	private Variable binding;
	
	public Variable() {
		this("Variable", new Information("Constant"));
	}
	
	private Variable(Variable parent) {
		super(parent);
		this.initialValue = parent.initialValue;
	}
	
	public Variable(String name, Information initialValue) {
		super(name);
		this.initialValue = initialValue;
	}
	
	public void setInitialValue(Information initialValue) {
		this.initialValue = initialValue;
	}
	
	public Information getInitialValue() {
		return initialValue;
	}
	
	public void bind(Variable var) {
		this.binding = var;
	}
	
	public Stream<Bit> getRawStream() {
		return initialValue.getRoot().getRawStream();
	}
	
	public List<String> getDisplayValue() {		
		if(binding != null) {
			return Arrays.asList(getLabel(), toString());
		}
	
		return Arrays.asList(getLabel(), toString());
	}
	
	public String toString() {
		return "Initial value: " + initialValue.getRoot().toString();
	}
	
	public int getSize() {
		return initialValue.getRoot().getSize();
	}
	
	public Variable clone() {
		return new Variable(this);
	}
}
