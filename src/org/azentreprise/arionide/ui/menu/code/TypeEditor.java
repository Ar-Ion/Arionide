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
package org.azentreprise.arionide.ui.menu.code;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Language;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.TypeManager;
import org.azentreprise.arionide.lang.Validator;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class TypeEditor extends Menu {

	private final Menu parent;
	private final SpecificationElement element;
	private final TypeManager typeManager;
	private final Validator validator;
	private final int separator;
	
	private boolean eventAborted = false;
	
	public TypeEditor(AppManager manager, Menu parent, Data element) {
		super(manager);
		
		this.parent = parent;
		this.element = element;
		
		Language lang = manager.getWorkspace().getCurrentProject().getLanguage();
		
		CoreDataManager cdm = lang.getCoreDataManager();
		
		this.typeManager = lang.getTypes().getTypeManager(element.getType());
		this.validator = lang.getTypes().getValidator(element.getType());

		if(this.typeManager != null) {
			this.getElements().addAll(cdm.getVariables(element.getType()));
			this.getElements().addAll(this.typeManager.getSuggestions(cdm));
			this.separator = this.getElements().size();
			this.getElements().add("Back");
			this.getElements().add("New variable");
			this.getElements().addAll(this.typeManager.getActionLabels());
						
			if(this.separator > 0) {
				this.setMenuCursor(this.separator - 1);
			} else {
				this.setMenuCursor(1);
			}
		} else {
			this.getElements().add("Back");
			this.separator = 0;
		}
	}
	
	public void onClick(int id) {
		if(id < this.separator) {
			this.eventAborted = false;
		} else {
			this.eventAborted = true;
			this.processAction(id - this.separator);
		}
	}
	
	private void processAction(int id) {
		if(id == 0) {
			this.parent.show();
		} else if(id == 1) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Please enter the name of the variable", "New variable", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					this.validateAction("var@" + name);
				}
			}).start();
		} else {
			this.typeManager.getActions().get(id - 2).accept(this.element.getValue(), this::validateAction);
		}
	}
	
	private void validateAction(String newValue) {
		if(this.validator.validate(newValue)) {
			this.element.setValue(newValue);
			this.parent.show();
			this.getAppManager().getEventDispatcher().fire(new MessageEvent("Value successfully updated", MessageType.SUCCESS));
			this.getAppManager().getWorkspace().getCurrentProject().getStorage().saveStructureMeta();
		} else {
			this.getAppManager().getEventDispatcher().fire(new MessageEvent("Unable to validate the input '" + newValue + "'", MessageType.ERROR));
		}
	}
	
	public void onClick(String element) {
		if(!this.eventAborted) {
			this.element.setValue(element);
			this.parent.show();
			this.getAppManager().getEventDispatcher().fire(new MessageEvent("Value successfully updated", MessageType.SUCCESS));
			this.getAppManager().getWorkspace().getCurrentProject().getStorage().saveStructureMeta();
		}
	}
	
	public String getDescription() {
		return this.element.toString();
	}
}