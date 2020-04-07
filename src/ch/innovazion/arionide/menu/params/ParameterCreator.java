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
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.specification.SpecificationManager;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class ParameterCreator extends Menu {
	
	@Export
	@Inherit
	protected Structure target;
	
	@Inherit
	@Export
	protected Parameter parameter;
	
	@Export
	protected boolean frozen;
	
	@Export
	protected Consumer<Void> onUpdate;
	
	@Export
	protected ParameterValue value;
	
	private SpecificationManager specManager;
	
	public ParameterCreator(MenuManager manager) {
		super(manager, "Variable", "Constant");
	}
	
	protected void onEnter() {
		super.onEnter();
		this.specManager = project.getStructureManager().loadSpecificationManager(target);
		this.description = new MenuDescription("Please select the type of '" + parameter.getName() + "'");
	}

	public void onAction(String action) {
		switch(action) {
		case "Variable":
			this.value = new Variable();
			this.onUpdate = this::onUpdate;
			this.frozen = false;
			onUpdate(null);
			go("../variable");
			break;
		case "Constant":
			this.value = new Information();
			this.onUpdate = this::onUpdate;
			this.frozen = false;
			onUpdate(null);
			go("../constant");
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void onUpdate(Void nil) {
		dispatch(specManager.refactorParameterDefault(parameter, this.value));
		dispatch(new GeometryInvalidateEvent(1));
	}
}
