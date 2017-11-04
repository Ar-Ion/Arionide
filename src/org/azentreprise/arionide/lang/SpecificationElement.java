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

public abstract class SpecificationElement implements Serializable {
	private static final long serialVersionUID = -2821188218676151203L;

	private String name;
	private String value;
	
	public SpecificationElement(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	protected String getRawValue() {
		return this.value;
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
	
	public boolean equals(java.lang.Object other) {
		if(other instanceof SpecificationElement) {
			SpecificationElement casted = (SpecificationElement) other;
			return this.name == casted.name;
		} else {
			return false;
		}
	}
	
	public abstract SpecificationElement clone();
}