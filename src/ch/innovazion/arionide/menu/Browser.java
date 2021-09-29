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
import java.util.stream.Collectors;

import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.Export;

public abstract class Browser extends Menu {

	private List<Structure> browsables;
	
	@Export
	protected Structure target;
	
	public Browser(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		List<HierarchyElement> elements = fetchBrowsableIDs();
		browsables = elements.stream().map(HierarchyElement::getID).map(project.getStorage().getStructures()::get).collect(Collectors.toList());
		
		setDynamicElements(browsables.stream().map(Structure::getName).toArray(String[]::new));
						
		super.onEnter();
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
				
		if(browsables != null && id < browsables.size()) {
			this.target = browsables.get(id);
			this.selection = target.getName();

			generateDescription();
			dispatch(new TargetUpdateEvent(target.getIdentifier()));
		}
	}
	
	protected void select(Structure target) {
		if(target != null) {
			int index = browsables.indexOf(target);
			
			if(index != -1) {
				this.target = target;
				this.cursor = index;
				this.id = index;
				this.selection = target.getName();
			}
		} else if(browsables.size() > 0) {
			this.cursor = 0;
			this.id = 0;
			this.target = browsables.get(0);
			this.selection = this.target.getName();
			
			dispatch(new TargetUpdateEvent(this.target.getIdentifier()));
		}
		
		generateDescription();
	}
	
	private void generateDescription() {
		this.description = new MenuDescription();
		
		if(target != null) {
			for(Parameter param : target.getSpecification().getParameters()) {
				description.add(param.toString());
			}
			
			
			if(!target.getSpecification().isTextOnly()) {
				description.spacer();
				
				for(String comment : target.getComment()) {
					description.add(comment);
				}
			}
		}
	}

	public void onAction(String action) {
		if(target != null) {
			browse();
		}
	}

	protected abstract List<HierarchyElement> fetchBrowsableIDs();
	protected abstract void browse();
}
