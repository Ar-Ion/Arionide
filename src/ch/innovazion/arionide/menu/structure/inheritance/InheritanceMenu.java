/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.menu.structure.inheritance;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.comparators.AlphabeticalComparator;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.structure.StructureSelection;
import ch.innovazion.arionide.project.InheritanceElement;
import ch.innovazion.arionide.project.Storage;

public class InheritanceMenu extends Menu {
		
	private final InheritanceEditor editor;
	private final StructureSelection selection;

	private List<Integer> parents;
	
	public InheritanceMenu(Menu parent) {
		super(parent);
		this.editor = new InheritanceEditor(parent);
		this.selection = new StructureSelection(this.getAppManager(), this::inherit);
	}

	public void show() {
		super.show();
		
		Comparator<Integer> comparator = new AlphabeticalComparator(getProject().getStorage());
	
		selection.resetComparators();
		selection.setupComparator(comparator);
		
		selection.resetFilters();
		selection.setupFilter(e -> e != getTarget().getID());
		
		Storage storage = getProject().getStorage();
		
		InheritanceElement object = storage.getInheritance().get(getTarget().getID());
		
		if(object != null) {
			parents = object.getParents();
			
			List<String> elements = getElements();
			elements.clear();
			elements.addAll(parents.stream().map(e -> storage.getStructureMeta().get(e).getName()).collect(Collectors.toList()));
			elements.add("Add");
		}
	}
	
	public void onClick(int id) {
		if(parents != null && id < parents.size()) {
			int element = parents.get(id);
			String name = getElements().get(id);
			
			editor.setInheritedStructure(element, name);
			editor.show();
		} else {
			selection.show();
		}
	}
	
	public void inherit(int structure) {
		Event message = getProject().getDataManager().getInheritanceManager().inherit(getTarget().getID(), structure);
		getAppManager().getEventDispatcher().fire(message);
		show();
	}
}