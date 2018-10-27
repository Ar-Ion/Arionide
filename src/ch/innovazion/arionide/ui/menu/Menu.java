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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.project.managers.HostStructureStack;
import ch.innovazion.arionide.ui.AppManager;

public abstract class Menu {
	
	private final AppManager manager;
	private List<String> elements = new ArrayList<>();
	private int menuCursor;
	private boolean active;
	
	protected Menu(AppManager manager, String... elements) {
		this.manager = manager;
		this.elements.addAll(Arrays.asList(elements));
	}
	
	public AppManager getAppManager() {
		return this.manager;
	}
	
	protected List<String> getElements() {
		return this.elements;
	}
	
	public List<String> getMenuElements() {
		return Collections.unmodifiableList(this.elements);
	}
	
	protected void fire(Event event) {
		this.manager.getEventDispatcher().fire(event);
	}
	
	public void show() {
		this.manager.getEventDispatcher().fire(new MenuEvent(this));
	}
	
	public int getMenuCursor() {
		return this.menuCursor;
	}
	
	public void setMenuCursor(int id) {
		this.menuCursor = id;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void select(int id) {
		if(id >= 0 && id < this.elements.size()) {
			this.setMenuCursor(id);
			this.onSelect(id);
			this.onSelect(this.elements.get(id));
		}
	}
	
	public void click() {
		if(this.menuCursor >= 0 && this.menuCursor < this.elements.size()) {
			this.onClick(this.menuCursor);
			this.onClick(this.elements.get(this.menuCursor));
		}
	}
		
	protected void onClick(String element) {
		;
	}
	
	protected void onClick(int id) {
		;
	}
	
	protected void onSelect(String element) {
		;
	}
	
	protected void onSelect(int id) {
		;
	}
	
	public String getDescription() {
		Project project = this.manager.getWorkspace().getCurrentProject();
		HostStructureStack stack = project.getDataManager().getHostStack();
		
		if(!stack.isEmpty()) {
			try {
				StructureMeta meta = project.getStorage().getStructureMeta().get(stack.getCurrent());
				return "The " + meta.getName() + (meta.getComment().equals("?") ? "" : " (" + meta.getComment() + ")");
			} catch(Exception e) {
				return "Unknown";
			}
		} else {
			return "The space";
		}
	}
}
