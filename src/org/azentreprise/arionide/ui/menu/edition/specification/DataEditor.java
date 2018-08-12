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
package org.azentreprise.arionide.ui.menu.edition.specification;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.code.TypeEditor;

public class DataEditor extends SpecificationElementEditor {

	private static final String setType = "Set type";
	private static final String setDefault = "Set default";

	private TypeManager type;
	private String description;
	
	protected DataEditor(AppManager manager, SpecificMenu parent) {
		super(manager, parent);
		
		this.getElements().add(setType);
		this.getElements().add(setDefault);
	}

	protected void setTarget(Specification specification, int id) {
		super.setTarget(specification, id);
		
		Data element = (Data) this.getElement();
		
		this.type = this.getAppManager().getWorkspace().getCurrentProject().getLanguage().getTypes().getTypeManager(element.getType());			
		this.description = element.getName() + " [" + this.type + "]";
		
		if(element.getValue() != null) {
			this.description += " (default: " + element.getValue() + ")";
		}
	}
	
	public void onClick(String element) {		
		switch(element) {
			case setType:
				TypeSelector selector = new TypeSelector(this.getAppManager(), this, this.getSpecification(), this.getElementID());
				selector.show();
				break;
			case setDefault:
				TypeEditor editor = MainMenus.getTypeEditor();
				editor.setTarget((Data) this.getElement());
				editor.show();
				break;
			default: 
				super.onClick(element);
		}
	}
	
	public void delete() {
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		
		if(project != null) {
			MessageEvent message = project.getDataManager().deleteSpecificationElement(this.getSpecification(), this.getElementID());
			this.getAppManager().getEventDispatcher().fire(message);
			
			this.getParent().reload();
			this.getParent().show();
		}
	}
	
	public String getDescription() {
		return this.description;
	}
}
