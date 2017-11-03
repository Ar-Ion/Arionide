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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Storage;

public class CoreDataManager {

	private final Storage storage;
	
	public CoreDataManager(Storage storage) {
		this.storage = storage;
	}
	
	public List<String> getVariables(int type) {
		int id = this.storage.getCurrentDataID();
		
		List<String> variables = new ArrayList<>();
		
		for(HierarchyElement element : this.storage.getHierarchy()) {
			this.browse(element, id, variables);
		}
		
		return variables;
	}
	
	private boolean browse(HierarchyElement element, int id, List<String> variables) {		
		if(element.getID() == id) {
			this.loadVars(id, variables);
			return true;
		} else {
			for(HierarchyElement child : element.getChildren()) {
				if(this.browse(child, id, variables)) {
					this.loadVars(id, variables);
					return true;
				}
			}
			
			return false;
		}
	}
	
	private void loadVars(int id, List<String> variables) {
		
	}
		
	public Map<Integer, String> getReferencables() {
		return this.storage.getInheritance().keySet().stream().collect(Collectors.toMap(Function.identity(), e -> this.storage.getStructureMeta().get(e).getName()));
	}
}