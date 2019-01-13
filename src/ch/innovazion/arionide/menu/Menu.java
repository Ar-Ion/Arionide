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
package ch.innovazion.arionide.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.events.ProjectCloseEvent;
import ch.innovazion.arionide.events.ProjectOpenEvent;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.ui.AppManager;

public abstract class Menu implements EventHandler {
	
	private final Menu parent;
	private final AppManager manager;
	private final List<String> elements = new ArrayList<>();
	
	private Project currentProject = null;
	private int menuCursor;
	
	// Only for root menu
	protected Menu(AppManager manager, String... elements) {
		this.parent = null;
		this.manager = parent.getAppManager();
		this.elements.addAll(Arrays.asList(elements));
	}
	
	protected Menu(Menu parent, String... elements) {
		this.parent = parent;
		this.manager = parent.getAppManager();
		this.elements.addAll(Arrays.asList(elements));
	}
	
	public AppManager getAppManager() {
		return manager;
	}
	
	protected List<String> getElements() {
		return elements;
	}
	
	public List<String> getMenuElements() {
		return Collections.unmodifiableList(elements);
	}
	
	protected void fire(Event event) {
		manager.getEventDispatcher().fire(event);
	}
	
	public void show() {
		manager.getEventDispatcher().fire(new MenuEvent(this));
	}
	
	public int getMenuCursor() {
		return menuCursor;
	}
	
	public int setMenuCursor(int index) {
		if(index < 0) {
			return menuCursor = elements.size() + index;
		} else {
			return menuCursor = index;
		}
	}
	
	public void select(int index) {
		int realIndex = setMenuCursor(index);
		
		if(currentProject != null) {
			onSelect(realIndex);
			onSelect(elements.get(realIndex));
		}
	}
	
	public void click() {
		if(currentProject != null && menuCursor >= 0 && menuCursor < elements.size()) {
			onClick(menuCursor);
			onClick(elements.get(menuCursor));
		}
	}
	
	public void back() {
		if(parent != null) {
			parent.show();
		}
	}

	protected void onClick(String element) {
		return;
	}
	
	protected void onClick(int index) {
		return;
	}
	
	protected void onSelect(String element) {
		return;
	}
	
	protected void onSelect(int index) {
		return;
	}
	
	protected Project getProject() {
		return currentProject;
	}
	
	public void handleEvent(Event event) {
		if(event instanceof ProjectOpenEvent) {
			currentProject = ((ProjectOpenEvent) event).getProject();
		} else if(event instanceof ProjectCloseEvent) {
			currentProject = null;
		}
	}
	
	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ProjectOpenEvent.class, ProjectCloseEvent.class);
	}
	
	public abstract MenuDescription getDescription();
}