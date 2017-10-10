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

public class SpecificationElement implements Serializable {
	private static final long serialVersionUID = -2821188218676151203L;

	private final String name;
	private final int type;
	private String value;
	
	public SpecificationElement(String name, int type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
