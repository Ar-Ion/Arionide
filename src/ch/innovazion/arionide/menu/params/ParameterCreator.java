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

import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Reference;
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
	protected ParameterValue value;
	
	private SpecificationManager specManager;
	
	public ParameterCreator(MenuManager manager) {
		super(manager, "Information", "Variable", "Reference");
	}
	
	protected void onEnter() {
		super.onEnter();
		this.specManager = project.getStructureManager().loadSpecificationManager(target);
		this.description = new MenuDescription("Please select the type of '" + parameter.getName() + "'");
	}

	public void onAction(String action) {
		switch(action) {
		case "Information":
			dispatch(specManager.refactorParameterDefault(parameter, new Information("Information")));
			this.value = new Information("info");
			break;
		case "Variable":
			dispatch(specManager.refactorParameterDefault(parameter, new Variable()));
			this.value = new Variable().getInitialValue();
			break;
		case "Reference":
			dispatch(specManager.refactorParameterDefault(parameter, new Reference()));
			this.value = new Reference();
			break;
		default:
			throw new IllegalArgumentException();
		}
				
		go(EditorMultiplexer.findDestination("/structure/edit", value));
	}
}
