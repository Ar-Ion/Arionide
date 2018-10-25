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
package ch.innovazion.arionide.ui.menu;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.ProjectCloseEvent;
import ch.innovazion.arionide.events.ProjectEvent;
import ch.innovazion.arionide.events.ProjectOpenEvent;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.HostStructureChangeObserver;
import ch.innovazion.arionide.project.HostStructureStack;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.core.geom.Geometry;
import ch.innovazion.arionide.ui.core.geom.WorldElement;

public class StructureList extends Menu implements EventHandler, HostStructureChangeObserver {
	
	private Project project;
	private List<HierarchyElement> generation;
	
	public StructureList(AppManager manager) {
		super(manager);
				
		manager.getEventDispatcher().registerHandler(this);
	}
	
	protected void onClick(int id) {
		if(this.generation != null && !this.generation.isEmpty()) {
			WorldElement element = this.getAppManager().getCoreRenderer().getStructuresGeometry().getElementByID(this.generation.get(id).getID());
			
			if(element != null) {
				MainMenus.getStructureEditor().setCurrent(element);
				MainMenus.getStructureEditor().show();
			}
		}
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ProjectEvent) {
			Project project = ((ProjectEvent) event).getProject();
			
			assert project != null;
			
			HostStructureStack stack = project.getDataManager().getHostStack();
			
			if(event instanceof ProjectOpenEvent) {
				this.project = project;
				stack.registerObserver(this);
				
				this.onHostStructureChanged(-1);
				/*
				 *  The open event might be firstly dispatched to the CoreRenderer, 
				 *  and thus the host stack could have been updated before this handler is registered.
				 */
			} else if(event instanceof ProjectCloseEvent) {
				this.project = null;
				this.generation = null;
				
				stack.unregisterObserver(this);
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ProjectOpenEvent.class, ProjectCloseEvent.class);
	}

	public void onHostStructureChanged(int newStruct) {
		this.generation = this.project.getDataManager().getCurrentGeneration(this.project.getStorage().getHierarchy());
		
		List<String> strings = this.getElements();
		Geometry geometry = this.getAppManager().getCoreRenderer().getStructuresGeometry();
		
		strings.clear();
		
		for(HierarchyElement struct : generation) {
			WorldElement element = geometry.getElementByID(struct.getID());
			
			if(element != null) {
				strings.add(element.toString());
			}
		}
		
		if(strings.isEmpty()) {
			strings.add("<No children>");
		} else {
			strings.sort(null);
		}
	}
}