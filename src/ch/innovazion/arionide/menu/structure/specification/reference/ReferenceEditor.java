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
package ch.innovazion.arionide.menu.structure.specification.reference;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.structure.specification.SpecificationElementEditor;
import ch.innovazion.arionide.ui.ApplicationTints;

public class ReferenceEditor extends SpecificationElementEditor {
	
	private static final String callability = "Callability";
	private static final String setParameters = "Parameters";
	
	public ReferenceEditor(Menu parent) {
		super(parent);
		
		getElements().add(callability);
		getElements().add(setParameters);
	}
	
	public void onClick(String element) {		
		Reference reference = (Reference) getElement();

		switch(element) {
			case callability:				
				getProject().getDataManager().getSpecificationManager().toggleCallability(reference);
				break;
			case setParameters:
				Menu menu = new ReferenceParameters(getAppManager(), this, getSpecification(), getElementID(), reference.getEagerParameters());
				menu.show();
				break;
			default: 
				super.onClick(element);
		}
	}
	
	public void delete() {
		Event message = getProject().getDataManager().getSpecificationManager().remove(getSpecification(), getElement());
		getAppManager().getEventDispatcher().fire(message);
		back();
	}
	
	public MenuDescription getDescription() {
		Reference ref = (Reference) this.getElement();
		MenuDescription description = new MenuDescription();
		
		description.add(ref.getName());
		
		for(SpecificationElement element : ref.getEagerParameters()) {
			description.add(element.toString(), ApplicationTints.SPECIFICATION_EAGER);
		}
		
		for(SpecificationElement element : ref.getLazyParameters()) {
			description.add(element.toString(), ApplicationTints.SPECIFICATION_LAZY);
		}
		
		return description;
	}
}