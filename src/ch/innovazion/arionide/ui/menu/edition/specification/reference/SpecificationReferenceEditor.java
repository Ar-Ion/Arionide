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
package ch.innovazion.arionide.ui.menu.edition.specification.reference;

import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.menu.Menu;
import ch.innovazion.arionide.ui.menu.SpecificMenu;
import ch.innovazion.arionide.ui.menu.edition.specification.SpecificationElementEditor;

public class SpecificationReferenceEditor extends SpecificationElementEditor {
	
	private static final String callability = "Callability";
	private static final String setParameters = "Parameters";
	
	public SpecificationReferenceEditor(AppManager manager, SpecificMenu parent) {
		super(manager, parent);
		
		this.getElements().add(callability);
		this.getElements().add(setParameters);
	}
	
	public void onClick(String element) {		
		Reference reference = (Reference) this.getElement();

		switch(element) {
			case callability:				
				this.getAppManager().getWorkspace().getCurrentProject().getDataManager().getSpecificationManager().toggleCallability(reference);
				
				break;
			case setParameters:
				Menu menu = new ReferenceParameters(this.getAppManager(), this, this.getSpecification(), this.getElementID(), reference.getNeededParameters());
				menu.show();

				break;
			default: 
				super.onClick(element);
		}
	}
	
	public void delete() {
		this.getAppManager().getWorkspace().getCurrentProject().getDataManager().getSpecificationManager().remove(this.getSpecification(), this.getElement());
		
		this.getParent().reload();
		this.getParent().show();
	}
	
	public String getDescription() {
		return this.getElement().getName() + " <" + String.join("; ", ((Reference) this.getElement()).getNeededParameters().stream().map(SpecificationElement::toString).toArray(String[]::new)) + ">";
	}
}