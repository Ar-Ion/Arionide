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
package ch.innovazion.arionide.menu.structure.specification.data;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.TypeManager;
import ch.innovazion.arionide.lang.Types;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;

public class TypeSelector extends Menu {
		
	private Specification specification;
	private int index;
	private String currentType;
	
	public TypeSelector(Menu parent) {
		super(parent);
	}
	
	public void setTarget(Specification specification, int index) {
		this.specification = specification;
		this.index = index;
	}
	
	public void show() {
		assert specification != null;
		
		super.show();

		Types types = getProject().getLanguage().getTypes();		
		TypeManager typeManager = types.getTypeManager(((Data) specification.getParameters().get(index)).getType());
		
		if(typeManager != null) {
			currentType = typeManager.toString();
		} else {
			currentType = "Undefined";
		}
		
		getElements().addAll(types.getAvailableTypes());
	}
	
	public void onClick(int id) {
		Event event = getAppManager().getWorkspace().getCurrentProject().getDataManager().getSpecificationManager().refactorType(specification, this.index, id);
		getAppManager().getEventDispatcher().fire(event);
		
		back();
	}
	
	public MenuDescription getDescription() {
		MenuDescription description = super.getDescription().clone();
		description.add("Current type: " + currentType);
		return description;
	}
}