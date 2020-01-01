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
package ch.innovazion.arionide.menu.code;

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.Menu;

public class ReferenceBinding extends Menu {
	
	private SpecificationElement element;
	private List<SpecificationElement> possible;
	
	public ReferenceBinding(Menu parent) {
		super(parent);
	}
	
	protected void setPossibleBindings(SpecificationElement element, List<SpecificationElement> possible) {
		this.element = element;
		this.possible = new ArrayList<>();
		
		for(SpecificationElement poss : possible) {
			if(element.getClass().isInstance(poss)) {
				if(poss.getValue() == null) {
					this.possible.add(poss);
					getElements().add(poss.getName());
				}
			}
		}
		
		if(getElements().isEmpty()) {
			getElements().add("<No bindings available>");
		}
	}
	
	public void onClick(int id) {
		if(!possible.isEmpty()) {
			Event event = getProject().getDataManager().getSpecificationManager().bindParameter(element, possible.get(id));
			getAppManager().getEventDispatcher().fire(event);
			back();
		}
	}
}