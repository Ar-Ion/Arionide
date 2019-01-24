/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.project;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class CodeChain implements Iterable<HierarchyElement>, Serializable {
	private static final long serialVersionUID = -5243535024202731258L;
	
	public Iterator<HierarchyElement> iterator() {
		return (Iterator<HierarchyElement>) list().iterator();
	}
	
	public String toString() {
		return list().toString();
	}
	
	public boolean isAbstract() {
		return list().isEmpty();
	}
	
	public abstract List<HierarchyElement> list();
	public abstract int indexOf(int id);
	public abstract int getID(int index);
}