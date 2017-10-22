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
package org.azentreprise.arionide.ui.menu.edition;

import java.util.List;
import java.util.stream.Collectors;

import org.azentreprise.arionide.comparators.AlphabeticalComparator;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.InheritanceElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.StructureSelection;

public class InheritanceMenu extends SpecificMenu {
		
	private final InheritanceEditor editor;
	
	private List<Integer> parents;
	
	protected InheritanceMenu(AppManager manager) {
		super(manager);
		this.editor = new InheritanceEditor(manager, this);
	}

	public void setCurrent(WorldElement element) {
		super.setCurrent(element);
		
		Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
		
		InheritanceElement object = storage.getInheritance().get(element.getID());
		
		if(object != null) {
			this.parents = object.getParents();
			
			List<String> elements = this.getElements();
			elements.clear();
			elements.addAll(this.parents.stream().map(e -> storage.getStructureMeta().get(e).getName()).collect(Collectors.toList()));
			elements.add("Add");
			elements.add("Cancel");
		}
	}
	
	public void onClick(int id) {
		if(this.parents != null && id < this.parents.size()) {
			int element = this.parents.get(id);
			this.editor.setTarget(element);
			this.editor.show();
		} else if(id == this.parents.size()){
			Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
			StructureSelection selection = new StructureSelection(this.getAppManager(), this::inherit, new AlphabeticalComparator(storage));
			selection.show();
		} else {
			MainMenus.getStructureEditor().show();
		}
	}
	
	public void inherit(int parent) {
		MessageEvent message = this.getAppManager().getWorkspace().getCurrentProject().getDataManager().inherit(this.getCurrent().getID(), parent);
		this.getAppManager().getEventDispatcher().fire(message);
		this.reload();
		this.show(); // This is being called by the structure selection menu...
	}
	
	public String getDescription() {
		return "Inheritance menu for " + super.getDescription();
	}
}