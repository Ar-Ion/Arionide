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
package org.azentreprise.arionide.project;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HierarchyElement implements Serializable {
	private static final long serialVersionUID = -5901117492235888923L;

	private final int id;
	protected final List<HierarchyElement> children;

	public HierarchyElement(int id, List<HierarchyElement> children) {
		this.id = id;
		this.children = children;
	}
	
	public List<HierarchyElement> getChildren() {
		return Collections.unmodifiableList(this.children);
	}

	public int getID() {
		return this.id;
	}
	
	public boolean equals(Object other) {
		if(other instanceof HierarchyElement) {
			return this.id == ((HierarchyElement) other).id;
		}
		
		return false;
	}
	
	public int hashCode() {
		return this.id;
	}
	
	public String toString() {
		if(this.children != null) {
			return this.id + "{" + String.join(",", this.children.stream().map(HierarchyElement::toString).collect(Collectors.toList())) + "}";
		} else {
			return "null";
		}
	}
}