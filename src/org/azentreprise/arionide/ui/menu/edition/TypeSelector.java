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

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.lang.Types;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class TypeSelector extends Menu {
		
	private final Menu parent;
	private final Specification specification;
	private final int id;
	private final String currentType;
	
	public TypeSelector(AppManager manager, Menu parent, Specification specification, int id) {
		super(manager);
		
		this.parent = parent;
		this.specification = specification;
		this.id = id;
		
		Types types = manager.getWorkspace().getCurrentProject().getLanguage().getTypes();
				
		TypeManager typeManager = types.getTypeManager(this.specification.getElements().get(id).getType());
		
		if(typeManager != null) {
			this.currentType = typeManager.toString();
		} else {
			this.currentType = "Undefined";
		}
		
		this.getElements().addAll(types.getAvailableTypes());
		this.getElements().add("Cancel");
	}
	
	public void onClick(int id) {
		if(id < this.getElements().size() - 1) {
			MessageEvent event = this.getAppManager().getWorkspace().getCurrentProject().getDataManager().refactorSpecificationType(this.specification, this.id, id);
			this.getAppManager().getEventDispatcher().fire(event);
			this.parent.show();
		} else {
			this.parent.show();
		}
	}
	
	public String getDescription() {
		return "Current type: " + this.currentType;
	}
}