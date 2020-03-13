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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.events.ProjectCloseEvent;
import ch.innovazion.arionide.events.ProjectOpenEvent;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.core.geom.WorldElement;

public abstract class Menu implements EventHandler {
	
	private final Menu parent;
	private final AppManager manager;
	private final List<String> elements = new ArrayList<>();
	
	private Project currentProject = null;
	private int menuCursor;
	
	// Only for root menu
	protected Menu(AppManager manager, String... elements) {
		this(manager, null, elements);
	}
	
	protected Menu(Menu parent, String... elements) {
		this(parent.manager, parent, elements);
	}
	
	private Menu(AppManager manager, Menu parent, String... elements) {
		this.parent = parent;
		this.manager = manager;
		this.elements.addAll(Arrays.asList(elements));
		
		manager.getEventDispatcher().registerHandler(this, 0.6f);
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
		
		if(currentProject != null && realIndex >= 0 && realIndex < elements.size()) {
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
	
	public void up() {
		if(currentProject != null) {
			onUp();
		}
	}
	
	public void down() {
		if(currentProject != null) {
			onDown();
		}
	}
	
	public void back() {
		if(parent != null) {
			parent.show();
		} // else discard;
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
	
	protected void onUp() {
		return;
	}
	
	protected void onDown() {
		return;
	}
	
	protected Project getProject() {
		return currentProject;
	}
	
	protected WorldElement getTarget() {
		if(parent != null) {
			return parent.getTarget();
		} else {
			throw new IllegalStateException("Target undefined for root menus");
		}
	}
	
	public MenuDescription getDescription() {
		if(parent != null) {
			return parent.getDescription();
		} else {
			throw new IllegalStateException("Menu description undefined for root menus");
		}
	}
	
	public boolean isCyclic() {
		return false;
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ProjectOpenEvent) {
			currentProject = ((ProjectOpenEvent) event).getProject();
		} else if(event instanceof ProjectCloseEvent) {
			currentProject = null;
		}
	}
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ProjectOpenEvent.class, ProjectCloseEvent.class);
	}
}