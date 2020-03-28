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
package ch.innovazion.arionide.menu.params.assign;

import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.specification.VariableManager;
import ch.innovazion.automaton.Inherit;

public abstract class VariableSelector extends Menu {
	
	@Inherit
	protected Structure target;
	
	@Inherit
	protected ParameterValue value;
	
	protected VariableManager varManager;
	
	public VariableSelector(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		super.onEnter();
		
		this.varManager = project.getStructureManager().getSpecificationManager().loadVariableManager(value);
		
		setDynamicElements(varManager.getVariables(target).stream().map(Information::getLabel).toArray(String[]::new));
	}
}