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
package org.azentreprise.arionide.ui.menu.edition.specification.reference;

import java.util.ArrayList;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.azentreprise.arionide.ui.menu.edition.specification.SpecificationElementEditor;

public class ReferenceEditor extends SpecificationElementEditor {
	
	private static final String callability = "Callability";
	private static final String setParameters = "Parameters";
	
	protected ReferenceEditor(AppManager manager, SpecificMenu parent) {
		super(manager, parent);
		
		this.getElements().add(callability);
		this.getElements().add(setParameters);
	}
	
	public void onClick(String element) {		
		Reference reference = (Reference) this.getElement();

		switch(element) {
			case callability:				
				if(reference.getParameters() != null) {
					reference.setParameters(null);
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is not callable anymore", MessageType.SUCCESS));
				} else {
					reference.setParameters(new ArrayList<>());
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is now callable", MessageType.SUCCESS));
				}
				
				break;
			case setParameters:
				if(reference.getParameters() != null) {
					Menu menu = new ReferenceParameters(this.getAppManager(), this, reference.getParameters());
					menu.show();
				} else {
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("This reference is not callable", MessageType.ERROR));
				}
				
				break;
			default: 
				super.onClick(element);
		}
	}
	
	public String getDescription() {
		return this.getElement().toString();
	}
}