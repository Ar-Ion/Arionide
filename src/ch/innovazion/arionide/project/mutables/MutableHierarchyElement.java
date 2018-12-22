/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.project.mutables;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.project.HierarchyElement;

public class MutableHierarchyElement implements HierarchyElement {
	private static final long serialVersionUID = -5901117492235888923L;

	private final int id;
	protected final List<MutableHierarchyElement> children;

	public MutableHierarchyElement(int id, List<MutableHierarchyElement> children) {
		this.id = id;
		this.children = children;
	}
	
	public List<HierarchyElement> getChildren() {
		return Collections.unmodifiableList(this.children);
	}
	
	public List<MutableHierarchyElement> getMutableChildren() {
		return this.children;
	}

	public int getID() {
		return this.id;
	}
	
	public boolean equals(Object other) {
		if(other instanceof MutableHierarchyElement) {
			return this.id == ((MutableHierarchyElement) other).id;
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
