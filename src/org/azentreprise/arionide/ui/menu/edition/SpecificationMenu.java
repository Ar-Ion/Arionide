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

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class SpecificationMenu extends SpecificMenu {
		
	private final SpecificationEditor editor;
	
	private Specification specification;
	
	protected SpecificationMenu(AppManager manager) {
		super(manager);
		this.editor = new SpecificationEditor(manager, this);
	}

	public void setCurrent(WorldElement element) {
		super.setCurrent(element);
		
		Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
		
		this.specification = storage.getStructureMeta().get(element.getID()).getSpecification();
		
		if(this.specification != null) {
			List<String> elements = this.getElements();
			
			elements.clear();
			elements.addAll(this.specification.getElements().stream().map(SpecificationElement::getName).collect(Collectors.toList()));
			elements.add("Add");
			elements.add("Cancel");
		}
	}
	
	public void onClick(int id) {
		if(id < this.getElements().size() - 2) {
			this.editor.setTarget(this.specification, id);
			this.editor.show();
		} else if(id == this.getElements().size() - 2){
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the new specification element's name", "New specification element", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					SpecificationElement element = new SpecificationElement(name, -1, null);
					MessageEvent event = this.getAppManager().getWorkspace().getCurrentProject().getDataManager().addSpecificationElement(this.specification, element);
					this.getAppManager().getEventDispatcher().fire(event);
					
					TypeSelector selector = new TypeSelector(this.getAppManager(), this, this.specification, this.specification.getElements().size() - 1);
					selector.show();
				}
			}).start();
		} else {
			MainMenus.getStructureEditor().show();
		}
	}
	
	public String getDescription() {
		return "Specification menu for " + super.getDescription();
	}
}