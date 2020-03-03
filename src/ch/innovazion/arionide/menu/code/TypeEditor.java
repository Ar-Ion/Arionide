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

import java.util.List;

import javax.swing.JOptionPane;

import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.TypeManager;
import ch.innovazion.arionide.lang.UserHelper;
import ch.innovazion.arionide.lang.Validator;
import ch.innovazion.arionide.lang.symbols.SpecificationElement;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.managers.HostStructureStack;

public class TypeEditor extends Menu {
	
	private SpecificationElement element;
	private TypeManager typeManager;
	private Validator validator;
	private int separator;
	private boolean eventAborted = false;
	
	public TypeEditor(Menu parent) {
		super(parent);
	}
	
	public void setTarget(Data element) {
		this.element = element;
		
		List<String> elements = this.getElements();
		
		elements.clear();
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		HostStructureStack stack = project.getDataManager().getHostStack();
		Language lang = project.getLanguage();
		UserHelper helper = lang.getUserHelper();
		
		typeManager = lang.getTypes().getTypeManager(element.getType());
		validator = lang.getTypes().getValidator(element.getType());

		if(typeManager != null) {
			if(!stack.isEmpty()) {
				elements.addAll(helper.getVariables(stack.getCurrent(), element.getType(), element.getName()));
				elements.addAll(typeManager.getSuggestions(helper));
			}
			
			separator = elements.size();
			
			elements.add("New variable");
			elements.addAll(typeManager.getActionLabels());
						
			if(separator > 0) {
				setMenuCursor(separator - 1);
			} else {
				setMenuCursor(1);
			}
		} else {
			separator = 0;
		}
	}
	
	public void onClick(int id) {
		if(id < separator) {
			eventAborted = false;
		} else {
			eventAborted = true;
			processAction(id - separator);
		}
	}
	
	private void processAction(int id) {
		if(id == 0) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Please enter the name of the variable", "New variable", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					validateAction(SpecificationElement.VAR + name);
				}
			}).start();
		} else {
			typeManager.getActions().get(id - 2).accept(element.getDisplayValue(), this::validateAction);
		}
	}
	
	private void validateAction(String newValue) {
		if(this.validator.validate(newValue)) {
			setValue(element, newValue);
		} else {
			this.getAppManager().getEventDispatcher().fire(new MessageEvent("Unable to validate the input '" + newValue + "'", MessageType.ERROR));
		}
	}
	
	public void onClick(String value) {
		super.onClick(value);
		
		if(!eventAborted) {
			setValue(element, value);
		}
	}
	
	private void setValue(SpecificationElement element, String value) {
		Event event = getProject().getDataManager().getSpecificationManager().setValue(element, value);
		getAppManager().getEventDispatcher().fire(event);
		back();
	}
	
	public MenuDescription getDescription() {
		return new MenuDescription(element.toString());
	}
}