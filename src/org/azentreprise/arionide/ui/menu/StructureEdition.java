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
package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class StructureEdition extends Menu {
		
	private static final String go = "Go";
	private static final String name = "Name";
	private static final String color = "Color";

	private final Coloring coloring;
	
	private WorldElement current;
	
	public StructureEdition(IEventDispatcher dispatcher) {
		super(dispatcher, go, name, color);
		this.coloring = new Coloring(dispatcher);
	}
	
	public void setCurrent(WorldElement current) {
		this.current = current;
		this.coloring.setCurrent(current);
	}
	
	protected void onSelect(String element) {
		assert this.current != null;
		
		switch(element) {
			case go:
				break;
			case name:
				break;
			case color:
				break;
		}
	}
	
	protected void onClick(String element) {
		assert this.current != null;
		
		switch(element) {
			case go:
				break;
			case name:
				break;
			case color:
				this.show(this.coloring);
				break;
		}
	}
	
}