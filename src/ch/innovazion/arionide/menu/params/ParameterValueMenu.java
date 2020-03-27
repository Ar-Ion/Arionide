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

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.specification.SpecificationManager;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public abstract class ParameterValueMenu extends Menu {
		
	@Inherit
	protected Structure target;
	
	@Inherit
	private Parameter parameter;
	
	@Export
	protected ParameterValue value;
	
	private SpecificationManager specManager;
	
	public ParameterValueMenu(MenuManager manager, String... staticElements) {
		super(manager, staticElements);
	}
	
	protected SpecificationManager getManager() {
		return specManager;
	}
	
	protected void onEnter() {
		super.onEnter();

		this.specManager = project.getStructureManager().getSpecificationManager();
		
		this.value = parameter.getValue();
		
		List<String> elements = new ArrayList<>();
		elements.add("Parameter name: " + parameter.getName());
		elements.add("");
		elements.addAll(parameter.getDisplayValue());
		
		this.description = new MenuDescription(elements.toArray(new String[0]));
	}
}
