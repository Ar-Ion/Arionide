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

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class ParameterEditor extends Menu {
	
	@Inherit
	protected Structure target;
	
	@Export
	@Inherit
	protected Parameter parameter;

	
	public ParameterEditor(MenuManager manager) {
		super(manager, "Rename", "Edit", "Delete");
	}

	public void onAction(String action) {
		switch(action) {
		case "Rename":
			Views.input.setText("Please enter the name of the parameter")
			   .setPlaceholder("Parameter name")
			   .setResponder(this::renameParameter)
			   .stackOnto(Views.code);
			
			break;
		case "Edit":
			go(EditorMultiplexer.findDestination("/structure/edit", parameter.getValue()));
			break;
		case "Delete":
			deleteParameter();
			break;
		}
	}
	
	private void renameParameter(String newName) {
		dispatch(project.getStructureManager().getSpecificationManager().refactorParameterName(parameter, newName));
		dispatch(new GeometryInvalidateEvent(1));
	}
	
	private void deleteParameter() {
		dispatch(project.getStructureManager().getSpecificationManager().removeParameter(parameter));
		dispatch(new GeometryInvalidateEvent(1));
	}
}