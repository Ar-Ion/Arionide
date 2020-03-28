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
package ch.innovazion.arionide.menu.params.edit;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.managers.specification.EnumerationManager;
import ch.innovazion.automaton.Inherit;

public class EnumerationRemover extends Menu {
	
	@Inherit
	protected ParameterValue value;
	
	@Inherit
	protected Parameter parameter; // If the parameter is mutable
	
	private EnumerationManager enumManager;
	
	public EnumerationRemover(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		super.onEnter();
		this.enumManager = project.getStructureManager().getSpecificationManager().loadEnumerationManager(value);
		setDynamicElements(enumManager.getNames().toArray(new String[0]));
		
		this.description = new MenuDescription("Select the enumeration possibilty you want to remove");
	}

	public void onAction(String action) {
		dispatch(enumManager.removePossibleEnum(action));
		go("..");
	}
}