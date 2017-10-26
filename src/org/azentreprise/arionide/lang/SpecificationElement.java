/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.lang;

import java.io.Serializable;

public class SpecificationElement implements Serializable, Cloneable {
	private static final long serialVersionUID = -2821188218676151203L;

	private String name;
	private int type;
	private String value;
	
	protected SpecificationElement(SpecificationElement model) {
		this.name = model.name;
		this.type = model.type;
		this.value = model.value;
	}
	
	public SpecificationElement(String name, int type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getValue() {
		String realValue = this.value;
		
		if(realValue != null) {
			int index = realValue.indexOf("$$$");
			if(index > -1) {
				realValue = realValue.substring(index + 3);
			}
		}
		
		return realValue;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		String displayValue = this.value;
		
		if(displayValue == null) {
			displayValue = "undefined";
		}
		
		int index = displayValue.indexOf("$$$");
		
		if(index > -1) {
			displayValue = displayValue.substring(0, index);
		}
		
		return this.name + ": " + displayValue;
	}
	
	public boolean equals(Object other) {
		if(other instanceof SpecificationElement) {
			SpecificationElement casted = (SpecificationElement) other;
			return this.name == casted.name && this.type == casted.type;
		} else {
			return false;
		}
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}