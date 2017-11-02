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
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceEditor extends Menu {

	private static final String back = "Back";
	private static final String reference = "Reference";
	
	private final Menu parent;
	private final Reference element;
	private final ReferenceSelector selector;
		
	public ReferenceEditor(AppManager manager, Menu parent, Reference element) {
		super(manager);
		
		this.parent = parent;
		this.element = element;
		this.selector = new ReferenceSelector(manager, parent, element);
		
		List<String> elements = this.getElements();
		
		for(Data data : element.getNeededParameters()) {
			elements.add(data.getName());
		}
		
		elements.add(back);
		elements.add(reference);
		
		this.setMenuCursor(elements.size() - 1);

		for(Data data : element.getSpecificationParameters()) {
			elements.add(data.getName());
		}	
	}
	
	public void onClick(String element) {
		if(element.equals(back)) {
			this.parent.show();
		} else if(element.equals(reference)) {
			this.selector.show();
		} else {
			
		}
	}
	
	public String getDescription() {
		return "Reference editor for '" + this.element + "'";
	}
}
