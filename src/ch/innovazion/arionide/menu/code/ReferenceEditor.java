/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
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

import java.util.List;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.MainMenus;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.ui.ApplicationTints;

public class ReferenceEditor extends Menu {
	
	private final ReferenceSelector selector;
	private final ReferenceBinding binding;
	
	private Reference element;
	private MenuDescription description;

	private int refIndex;
		
	public ReferenceEditor(Menu parent) {
		super(parent);
		this.selector = new ReferenceSelector(this);
		this.binding = new ReferenceBinding(this);
	}
	
	public void setTarget(Reference element) {
		this.element = element;
		
		selector.setTarget(element);
		
		List<String> elements = this.getElements();
		
		description = new MenuDescription();
		elements.clear();
		
		for(SpecificationElement data : element.getEagerParameters()) {
			elements.add(data.getName());
			description.add(data.toString(), ApplicationTints.SPECIFICATION_EAGER);
		}
		
		this.refIndex = elements.size();
		elements.add("Set referee");

		for(SpecificationElement data : element.getLazyParameters()) {
			elements.add(data.getName());
			description.add(data.toString(), ApplicationTints.SPECIFICATION_LAZY);
		}
		
		this.setMenuCursor(this.refIndex);
	}
	
	public void onClick(int id) {
		if(id == this.refIndex) {
			selector.show();
		} else if(element.getValue() != null) {
			if(id < this.refIndex) {
				binding.setPossibleBindings(element.getEagerParameters().get(id), element.getLazyParameters());
				binding.show();
			} else {
				id -= this.refIndex + 1;
				
				TypeEditor menu = MainMenus.getTypeEditor();
				menu.setTarget((Data) element.getLazyParameters().get(id));
				menu.show();
			}
		} else {
			getAppManager().getEventDispatcher().fire(new MessageEvent("You first have to set a referee", MessageType.ERROR));
		}
	}
	
	public MenuDescription getDescription() {
		return description;
	}
}
