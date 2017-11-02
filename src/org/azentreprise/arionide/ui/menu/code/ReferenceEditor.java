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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceEditor extends Menu {
	
	private static final String back = "Back";

	private final Menu parent;
	private final SpecificationElement element;
		
	public ReferenceEditor(AppManager manager, Menu parent, Reference element) {
		super(manager);
		
		this.parent = parent;
		this.element = element;
		
		CoreDataManager cdm = manager.getWorkspace().getCurrentProject().getLanguage().getCoreDataManager();
		
		List<String> suggestions = new ArrayList<>();
		
		for(Entry<Integer, String> entry : cdm.getReferencables().entrySet()) {
			suggestions.add(entry.getValue() + "$$$" + entry.getKey());
		}
		
		this.getElements().add(back);
		this.getElements().addAll(suggestions);
		
		if(element.getValue() != null) {
			int index = this.getElements().indexOf(element.getValue());
		
			if(index > -1) {
				this.select(index + 1);
			}
		}
	}
	
	public void onClick(String element) {
		this.element.setValue(element);
		this.parent.show();
		this.getAppManager().getEventDispatcher().fire(new MessageEvent("Value successfully updated", MessageType.SUCCESS));
		this.getAppManager().getWorkspace().getCurrentProject().getStorage().saveStructureMeta();
	}
	
	public String getDescription() {
		return "Reference editor for '" + this.element + "'";
	}
}
