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
package ch.innovazion.arionide.menu;

import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.ApplicationTints;

public abstract class Browser extends Menu {

	private List<Integer> objects;
	private MenuDescription description;
	
	protected Browser(AppManager manager, String... elements) {
		super(manager, elements);
	}
	
	protected Browser(Menu parent, String... elements) {
		super(parent, elements);
	}
	
	protected void onSelect(int index) {
		if(!objects.isEmpty()) {
			Integer id = objects.get(index);
								
			if(id != null) {				
				getAppManager().getCoreOrchestrator().getController().select(id);
							
				StructureMeta meta = getProject().getStorage().getStructureMeta().get(id);
				List<SpecificationElement> elements = meta.getSpecification().getElements();
				
				description = new MenuDescription(ApplicationTints.MENU_INFO_INACTIVE_COLOR, 0.5f);
				
				if(!meta.getComment().equals("?")) {
					description.add(meta.getComment(), ApplicationTints.COMMENT_COLOR);
				}
				
				for(SpecificationElement element : elements) {
					description.add(element.toString());
				}
	
				description.setHighlight(0);
			}
		}
	}

	public void show() {
		List<String> elements = getElements();
		elements.clear();
		
		Project project = getProject();
		
		if(project != null) {
			objects = loadCurrentElements();
			Map<Integer, StructureMeta> meta = project.getStorage().getStructureMeta();
						
			for(int id : objects) {
				elements.add(meta.get(id).getName());
			}
			
			if(elements.isEmpty()) {
				elements.add("<Empty>");
			}
		} else {
			throw new IllegalStateException();
		}
		
		super.show(); // Send event after having computed the elements
	}
	
	public void setSelectedID(int id) {
		if(objects != null) {
			select(objects.indexOf(id));
		}
	}
	
	protected int getSelectedID() { 
		try {
			return objects.get(getMenuCursor());
		} catch(IndexOutOfBoundsException exception) {
			return -1;
		}
	}
	
	public MenuDescription getDescription() {		
		if(description != null) {
			return description;
		} else {
			return MenuDescription.EMPTY;
		}
	}

	protected abstract List<Integer> loadCurrentElements();
}