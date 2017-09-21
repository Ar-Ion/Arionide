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
package org.azentreprise.arionide.ui.core.opengl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.InheritanceElement;

public class InheritanceGenerator {
	
	private final Map<Integer, InheritanceElement> inheritance;
	private final Consumer<List<HierarchyElement>> completion;
	
	protected InheritanceGenerator(Map<Integer, InheritanceElement> inheritance, Consumer<List<HierarchyElement>> completion) {
		this.inheritance = inheritance;
		this.completion = completion;
	}
	
	protected void generate(int id) {
		List<HierarchyElement> elements = new ArrayList<HierarchyElement>();
		
		this.processChildren(this.inheritance.get(id), elements);		
		elements.add(new HierarchyElement(-1, null));
		this.processParents(this.inheritance.get(id), elements);
				
		System.out.println(elements);
		
		this.completion.accept(Arrays.asList(new HierarchyElement(id, elements)));
	}
	
	private void processChildren(InheritanceElement src, List<HierarchyElement> dest) {		
		for(int childID : src.getChildren()) {
			List<HierarchyElement> next = new ArrayList<>();
			dest.add(new HierarchyElement(childID, next));
			this.processChildren(this.inheritance.get(childID), next);
		}
	}
	
	private void processParents(InheritanceElement src, List<HierarchyElement> dest) {
		for(int parentID : src.getParents()) {
			List<HierarchyElement> next = new ArrayList<>();
			dest.add(new HierarchyElement(parentID, next));
			this.processParents(this.inheritance.get(parentID), next);
		}
	}
}