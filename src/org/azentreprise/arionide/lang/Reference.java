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

import java.util.List;

public class Reference extends SpecificationElement {

	private static final long serialVersionUID = -3387831385820354646L;

	private List<Data> parameters;

	public Reference(String name, String value, List<Data> parameters) {
		super(name, value);
		this.parameters = parameters;
	}
	
	public List<Data> getParameters() {
		return this.parameters;
	}
	
	public void setParameters(List<Data> parameters) {
		this.parameters = parameters;
	}
	
	public String toString() {
		return super.toString() + " <" + String.join("; ", this.parameters.stream().map(Data::toString).toArray(String[]::new)) + ">";
	}
	
	public boolean equals(Object other) {
		if(other instanceof Reference) {
			Reference casted = (Reference) other;
			return super.equals(other) && this.parameters.equals(casted.parameters);
		} else {
			return false;
		}
	}
	
	public Reference clone() {
		return new Reference(this.getName(), this.getRawValue(), this.parameters);
	}
}