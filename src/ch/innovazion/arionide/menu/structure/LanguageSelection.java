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
package ch.innovazion.arionide.menu.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.project.CodeChain;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.StructureMeta;

public class LanguageSelection extends Browser {
	
	private MenuDescription description;
	
	protected LanguageSelection(Menu parent) {
		super(parent);
	}
	
	public void onClick(int id) {	
		super.onClick(id);
				
		Event message = getProject().getDataManager().setLanguage(getTarget().getID(), getSelectedID());
		getAppManager().getEventDispatcher().fire(message);
		
		back();
	}
	
	protected void onSelect(int id) {
		return;
	}
	
	public void show() {		
		Map<Integer, StructureMeta> meta = getProject().getStorage().getStructureMeta();
		int currentLanguage = meta.get(getTarget().getID()).getLanguage();
		
		String language = meta.get(currentLanguage).getName();
		
		description = new MenuDescription("Current language: " + language);
		
		super.show();
	}
	
	protected List<Integer> loadCurrentElements() {
		List<Integer> output = new ArrayList<>();
		List<HierarchyElement> elements = getProject().getStorage().getHierarchy();
		Map<Integer, CodeChain> code = getProject().getStorage().getCode();
		
		for(HierarchyElement element : elements) {
			int id = element.getID();
			
			if(id != getTarget().getID() && code.get(id).isAbstract()) {
				output.add(id);
			}
		}
				
		return output;
	}
		
	public boolean isCyclic() {
		return true;
	}
	
	public MenuDescription getDescription() {
		return description;
	}
}