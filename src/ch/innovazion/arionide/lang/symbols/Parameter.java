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

import java.io.Serializable;
import java.util.List;

public class Parameter implements Serializable {

	public static final long serialVersionUID = -2821188218676151203L;

	private String name;
	private ParameterValue value;
	private boolean frozen;
	private boolean textOnly;
	
	public Parameter(String name) {
		this(name, new Information(name));
	}
	
	public Parameter(String name, ParameterValue value) {
		this(name, value, false, false);
	}
	
	private Parameter(String name, ParameterValue value, boolean frozen, boolean textOnly) {
		this.name = name;
		this.value = value;
		this.frozen = frozen;
		this.textOnly = textOnly;
	}
	
	public Parameter asConstant(Node value) {
		Information information = new Information(name);
		information.setRootNode(value);
		this.value = information;
		return this;
	}
	
	public Parameter asVariable(Node value) {
		Variable var = new Variable(name, value);
		this.value = var;
		return this;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ParameterValue getValue() {
		return this.value;
	}
	
	public void setValue(ParameterValue value) {
		this.value = value;
	}
	
	public Parameter setTextOnly(boolean textOnly) {
		this.textOnly = textOnly;
		return asConstant(new Text());
	}
	
	public boolean isTextOnly() {
		return textOnly;
	}
	
	public Parameter setFrozen(boolean frozen) {
		this.frozen = frozen;
		return this;
	}
	
	public boolean isFrozen() {
		return frozen;
	}
	
	public List<String> getDisplayValue() {
		return value.getDisplayValue();
	}
	
	public String toString() {		
		return name + ": <" + String.join(", ", value.getDisplayValue()) + ">";
	}
	
	public boolean equals(java.lang.Object other) {
		if(other instanceof Parameter) {
			Parameter casted = (Parameter) other;
			return this.name == casted.name;
		} else {
			return false;
		}
	}
	
	public Parameter clone() {
		return new Parameter(name, value.clone(), frozen, textOnly);
	}
}