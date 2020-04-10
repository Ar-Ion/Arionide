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

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.StateManager;

public class MenuManager extends StateManager {
	
	private static final MenuHierarchy hierarchy = new MenuHierarchy();
	private final IEventDispatcher dispatcher;
		
	public MenuManager(IEventDispatcher dispatcher) {
		super(hierarchy, true);
		
		this.dispatcher = dispatcher;
	}
	
	public void setProject(Project project) {
		hierarchy.root.project = project;
	}
	
	public void selectStructure(Structure structure) {
		go("/");
		triggerAction(RootMenu.structureBrowser);
		
		hierarchy.structureBrowser.select(structure);
	}
	
	public void selectCode(Structure code) {		
		go("/");
		triggerAction(RootMenu.codeBrowser);
		
		hierarchy.codeBrowser.select(code);
	}
	
	public void selectNode(Structure actor, Node node) {		
		go("/");
		triggerAction(RootMenu.genericUpdater);
		
		hierarchy.genericUpdater.setGenericTarget(actor);
		hierarchy.genericUpdater.setGenericParameterValue(node);
		hierarchy.genericUpdater.setGenericParameterFrozen(false);
		
		triggerAction("information");
	}
	
	public void select(int cursor) {
		Menu menu = hierarchy.resolveCurrentState();
		menu.updateCursor(cursor);
	}
	
	public void click() {
		Menu menu = hierarchy.resolveCurrentState();
		menu.onAction(menu.selection);
	}
	
	public void back() {
		Menu menu = hierarchy.resolveCurrentState();
		
		if(menu != hierarchy.codeBrowser && menu != hierarchy.structureBrowser) {
			go("..");
		}
	}
	
	public void refresh(Menu current) {
		dispatcher.fire(new MenuEvent(current));
	}
	
	public void dispatch(Event event) {
		dispatcher.fire(event);
	}
}
