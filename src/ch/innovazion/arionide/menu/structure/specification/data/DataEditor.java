/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.menu.structure.specification.data;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.lang.TypeManager;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.code.TypeEditor;
import ch.innovazion.arionide.menu.structure.specification.SpecificationElementEditor;
import ch.innovazion.arionide.ui.ApplicationTints;

public class DataEditor extends SpecificationElementEditor {

	private static final String setType = "Set type";
	private static final String setDefault = "Set default";

	private final TypeSelector typeSelector;
	private final TypeEditor typeEditor;
	
	private MenuDescription description;
	
	public DataEditor(Menu parent) {
		super(parent);
		
		this.typeSelector = new TypeSelector(this);
		this.typeEditor = new TypeEditor(this);
		
		getElements().add(setType);
		getElements().add(setDefault);
	}

	public void setTarget(Specification specification, int id) {
		super.setTarget(specification, id);
		
		Data element = (Data) getElement();
		
		TypeManager type = getProject().getLanguage().getTypes().getTypeManager(element.getType());	
		String typeName = type != null ? type.toString() : "Undefined";
				
		description = new MenuDescription();
		
		description.add(element.getName());
		description.add("Type: " + typeName, ApplicationTints.DATA_TYPE);
		
		if(element.getValue() != null) {
			description.add("Default value: " + element.getValue());
		}
	}
	
	public void onClick(String element) {		
		switch(element) {
			case setType:
				typeSelector.setTarget(getSpecification(), getElementID());
				typeSelector.show();
				break;
			case setDefault:
				typeEditor.setTarget((Data) getElement());
				typeEditor.show();
				break;
			default: 
				super.onClick(element);
		}
	}
	
	public void delete() {
		Event message = getProject().getDataManager().getSpecificationManager().deleteElement(getSpecification(), getElementID());
		getAppManager().getEventDispatcher().fire(message);
		back();
	}
	
	public MenuDescription getDescription() {
		return description;
	}
}
