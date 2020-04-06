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
package ch.innovazion.arionide.menu.params;

import java.util.function.Consumer;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.specification.SpecificationManager;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class ParameterEditor extends Menu {
	
	@Inherit
	@Export
	protected Structure target;
	
	@Inherit
	@Export
	protected Parameter parameter;
	
	// When we want to edit a variable as a specification parameter, we may only change its initial value
	@Export
	protected ParameterValue value;
	
	@Export
	protected Consumer<Void> onUpdate;
	
	@Export
	protected boolean frozen;

	private SpecificationManager specManager;
	
	public ParameterEditor(MenuManager manager) {
		super(manager, "Edit", "Rename", "Reset", "Delete");
	}
	
	protected void onEnter() {
		super.onEnter();
		this.specManager = project.getStructureManager().loadSpecificationManager(target);
		
		this.frozen = parameter.isFrozen();
		
		if(frozen) {
			setDynamicElements("Unfreeze");
		} else {
			setDynamicElements("Freeze");
		}
		
		this.value = parameter.getValue();
		
		updateCursor(0);
	}

	public void onAction(String action) {
		switch(action) {
		case "Edit":
			this.onUpdate = this::onUpdate;
			
			if(value instanceof Variable) {
				go("../variable");
			} else if(value instanceof Information) {
				go("../constant");
			}

			break;
		case "Rename":
			Views.input.setText("Please enter the name of the parameter")
			   .setPlaceholder("Parameter name")
			   .setResponder(this::renameParameter)
			   .stackOnto(Views.code);
			
			break;
		case "Reset":
			go("../create");
			break;
		case "Delete":
			deleteParameter();
			break;
		case "Unfreeze":
			dispatch(specManager.setParameterFrozen(parameter, false));
			dispatch(new GeometryInvalidateEvent(1));
			go(".");
			break;
		case "Freeze":
			dispatch(specManager.setParameterFrozen(parameter, true));
			dispatch(new GeometryInvalidateEvent(1));
			go(".");
			break;
		}
	}
	
	private void renameParameter(String newName) {
		dispatch(specManager.refactorParameterName(parameter, newName));
		dispatch(new GeometryInvalidateEvent(1));
		go("..");
	}
	
	private void deleteParameter() {
		dispatch(specManager.removeParameter(parameter));
		dispatch(new GeometryInvalidateEvent(1));
		go("..");
	}
	
	private void onUpdate(Void nil) {
		dispatch(specManager.refactorParameterDefault(parameter, this.value));
		dispatch(new GeometryInvalidateEvent(1));
	}
}