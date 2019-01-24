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
package ch.innovazion.arionide.menu.structure.inheritance;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;

public class InheritanceEditor extends Menu {

	private static final String remove = "Desinherit";
	
	private int id = -1;
	private String name;
	
	protected InheritanceEditor(Menu parent) {
		super(parent, remove);
	}
	
	protected void setInheritedStructure(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public void show() {
		assert id >= 0;
		super.show();
	}
	
	protected void onClick(String element) {	
		switch(element) {
			case remove:				
				Event message = getProject().getDataManager().getInheritanceManager().disinherit(id, getTarget().getID());
				getAppManager().getEventDispatcher().fire(message);
				
				back();
				
				break;
		}
	}
	
	public MenuDescription getDescription() {
		return new MenuDescription("Inheritance editor for '" + name + "'");
	}
}
