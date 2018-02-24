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
package org.azentreprise.arionide.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;

public class CoreDataManager {

	private final Storage storage;
	
	public CoreDataManager(Storage storage) {
		this.storage = storage;
	}
	
	public List<String> getVariables(int type, String name) {
		int id = this.storage.getCurrentDataID();
		
		List<String> variables = new ArrayList<>();
		
		for(SpecificationElement element : this.storage.getStructureMeta().get(id).getSpecification().getElements()) {
			variables.add(element.getName() + "$$$var@" + element.getName());
		}
		
		for(HierarchyElement element : this.storage.getHierarchy()) {
			this.browseHierarchy(element, id, variables);
		}
		
		this.browseInheritance(id, variables);
		
		Collections.reverse(variables);
		
		Iterator<String> iterator = variables.iterator();
		List<String> priority = new ArrayList<>();
		
		while(iterator.hasNext()) {
			String var = iterator.next();
			
			if(var.startsWith(name)) {
				priority.add(var);
				iterator.remove();
			}
		}
		
		variables.addAll(priority);
				
		this.storage.loadData(id); // Restore initial state
		
		return variables;
	}
	
	private boolean browseHierarchy(HierarchyElement element, int id, List<String> variables) {
		if(element.getID() == id) {
			this.loadVars(id, variables);
			return true;
		} else {
			for(HierarchyElement child : element.getChildren()) {
				if(this.browseHierarchy(child, id, variables)) {
					this.loadVars(element.getID(), variables);
					return true;
				}
			}
			
			return false;
		}
	}
	
	private void browseInheritance(int element, List<String> variables) {
		this.loadVars(element, variables);
		
		for(int parent : this.storage.getInheritance().get(element).getParents()) {
			this.browseInheritance(parent, variables);
		}
	}
	
	private void loadVars(int id, List<String> variables) {
		this.storage.loadData(id);
				
		for(HierarchyElement element : this.storage.getCurrentData()) {
			StructureMeta meta = this.storage.getStructureMeta().get(element.getID());
			
			for(SpecificationElement specElement : meta.getSpecification().getElements()) {
				String value = specElement.getRawValue();
				
				if(value != null && value.startsWith("var@")) {
					value = value.substring(4) + "$$$" + value;
						
					if(!variables.contains(value)) {
						variables.add(value);
					}
				}
			}
		}
	}
		
	public Map<Integer, String> getReferencables() {
		return this.storage.getInheritance().keySet().stream().collect(Collectors.toMap(Function.identity(), e -> {
			return this.storage.getStructureMeta().containsKey(e) ? this.storage.getStructureMeta().get(e).getName() : "?";
		}));
	}
}