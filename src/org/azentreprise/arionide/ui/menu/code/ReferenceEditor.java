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
package org.azentreprise.arionide.ui.menu.code;

import java.util.List;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceEditor extends Menu {
	
	private final Menu parent;
	private final Reference element;
	private final ReferenceSelector selector;
	
	private final int refIndex;
		
	public ReferenceEditor(AppManager manager, Menu parent, Reference element) {
		super(manager);
		
		this.parent = parent;
		this.element = element;
		this.selector = new ReferenceSelector(manager, parent, element);
		
		List<String> elements = this.getElements();
		
		for(SpecificationElement data : element.getNeededParameters()) {
			elements.add(data.getName());
		}
		
		elements.add("Back");
		this.refIndex = elements.size();
		elements.add("Reference");

		for(SpecificationElement data : element.getSpecificationParameters()) {
			elements.add(data.getName());
		}
		
		this.setMenuCursor(this.refIndex);
	}
	
	public void onClick(int id) {
		if(id == this.refIndex) {
			this.selector.show();
		} else if(id == this.refIndex - 1) {
			this.parent.show();
		} else if(id < this.refIndex) {
			Menu binding = new ReferenceBinding(this.getAppManager(), this, (Data) this.element.getSpecificationParameters().get(id), this.element.getSpecificationParameters());
			binding.show();
		} else {
			id -= this.refIndex + 1;
			
			Menu menu = new TypeEditor(this.getAppManager(), this, (Data) this.element.getSpecificationParameters().get(id));
			menu.show();
		}
	}
	
	public String getDescription() {
		return "Reference editor for '" + this.element + "'";
	}
}
