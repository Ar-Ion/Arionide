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
package ch.innovazion.arionide.ui.menu.code;

import java.util.List;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.lang.TypeManager;
import ch.innovazion.arionide.lang.UserHelper;
import ch.innovazion.arionide.lang.Validator;
import ch.innovazion.arionide.project.HostStructureStack;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.menu.Menu;

public class TypeEditor extends Menu {

	private final Menu parent;
	
	private SpecificationElement element;
	private TypeManager typeManager;
	private Validator validator;
	private int separator;
	private boolean eventAborted = false;
	
	public TypeEditor(AppManager manager, Menu parent) {
		super(manager);
		this.parent = parent;
	}
	
	public void setTarget(Data element) {
		this.element = element;
		
		List<String> elements = this.getElements();
		
		elements.clear();
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		HostStructureStack stack = project.getDataManager().getHostStack();
		Language lang = project.getLanguage();
		UserHelper helper = lang.getUserHelper();
		
		this.typeManager = lang.getTypes().getTypeManager(element.getType());
		this.validator = lang.getTypes().getValidator(element.getType());

		if(this.typeManager != null) {
			if(!stack.isEmpty()) {
				elements.addAll(helper.getVariables(stack.getCurrent(), element.getType(), element.getName()));
				elements.addAll(this.typeManager.getSuggestions(helper));
			}
			
			this.separator = this.getElements().size();
			
			elements.add("Back");
			elements.add("New variable");
			elements.addAll(this.typeManager.getActionLabels());
						
			if(this.separator > 0) {
				this.setMenuCursor(this.separator - 1);
			} else {
				this.setMenuCursor(1);
			}
		} else {
			elements.add("Back");
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
					this.validateAction(SpecificationElement.VAR + name);
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