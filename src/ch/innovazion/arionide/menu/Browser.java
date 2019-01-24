/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
				getAppManager().getCoreRenderer().select(id);
							
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
		return objects.get(getMenuCursor());
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