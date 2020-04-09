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
import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.specification.SpecificationManager;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class ParameterSelector extends Menu {
	
	private final boolean mutable;
	
	@Inherit
	@Export
	protected Structure target;
	
	@Export
	protected Parameter parameter;
	
	@Export
	protected ParameterValue value; // If the selector has the immutable flag
	
	@Export
	protected Consumer<Void> onUpdate;
	
	@Export
	protected boolean frozen;
	
	private SpecificationManager specManager;
	
	public ParameterSelector(MenuManager manager, boolean mutable) {
		super(manager, mutable ? new String[] { "Add", null } : new String[0]);
		
		this.mutable = mutable;
	}
	
	protected void onEnter() {		
		this.specManager = project.getStructureManager().loadSpecificationManager(target);
		setDynamicElements(target.getSpecification().getParameters().stream().map(Parameter::getName).toArray(String[]::new));
		super.onEnter();
		updateCursor(0);
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
		
		int realID = id;
		
		if(target != null) {
			if(mutable) {
				if(id == 0) {
					this.description = new MenuDescription("Add a new parameter to '" + target.getName() + "'");
					return;
				}
				
				realID -= 2;
			}
			
			if(realID >= 0 && realID < target.getSpecification().getParameters().size()) {
				this.parameter = target.getSpecification().getParameters().get(realID);
				this.value = parameter.getValue();
				this.description = new MenuDescription(parameter.getDisplayValue().toArray(new String[0]));

				dispatch(new TargetUpdateEvent(((realID + 1) << 24) | target.getIdentifier()));	
			} else {
				this.description = new MenuDescription("No parameter");
			}
		}
	}
	
	public void onAction(String action) {
		if(mutable) {
			if(id == 0) {
				Views.input.setText("Please enter the name of the parameter")
						   .setPlaceholder("Parameter name")
						   .setResponder(this::createParameter)
						   .stackOnto(Views.code);
			} else {
				go("edit");
			}
		} else {
			this.onUpdate = null;
			this.frozen = parameter.isFrozen();
			
			if(value instanceof Variable) {
				go("../variable");
			} else if(value instanceof Information) {
				go("../constant");
			}
		}
	}
	
	private void createParameter(String name) {
		this.parameter = new Parameter(name, new Information("Constant"));
		
		dispatch(specManager.addParameter(parameter));
		dispatch(new GeometryInvalidateEvent(1));
		
		go("create");
	}
}
