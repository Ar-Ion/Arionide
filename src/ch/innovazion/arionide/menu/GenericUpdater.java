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
package ch.innovazion.arionide.menu;

import java.util.function.Consumer;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.Export;

public class GenericUpdater extends Menu {
	
	protected GenericUpdater(MenuManager manager) {
		super(manager);
	}

	@Export
	protected Structure target;
	
	@Export
	protected Parameter parameter;
	
	@Export
	protected ParameterValue value;
	
	@Export
	protected Consumer<Void> onUpdate;
	
	@Export
	protected boolean frozen;
	
	public void setGenericTarget(Structure target) {
		this.target = target;
	}
	
	public void setGenericParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public void setGenericParameterValue(ParameterValue value) {
		this.value = value;
	}
	
	public void setGenericUpdateResponder(Consumer<Void> onUpdate) {
		this.onUpdate = onUpdate;
	}
	
	public void setGenericParameterFrozen(boolean frozen) {
		this.frozen = frozen;
	}
	
	public void onAction(String action) {
		go(action);
	}
}
