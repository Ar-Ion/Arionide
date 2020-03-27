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
package ch.innovazion.arionide.project.managers.specification;

import java.util.List;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.lang.symbols.Enumeration;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.project.Storage;

public class EnumerationManager extends ContextualManager<Enumeration> {
	protected EnumerationManager(Storage storage) {
		super(storage);
	}
	
	public List<String> getNames() {
		return getContext().getNames();
	}
	
	public List<String> getEnumDescription(String name) {
		return getContext().getValue(name).getDisplayValue();
	}
	
	public MessageEvent addPossibleEnum(String name) {
		getContext().addPossibleValue(name);
		return success();
	}
	
	public MessageEvent removePossibleEnum(String name) {
		getContext().removePossibleValue(name);
		return success();
	}
	
	public Information getEnumValue(String name) {
		return getContext().getValue(name);
	}
	
	public MessageEvent assignEnumValue(String name) {
		getContext().setValue(name);
		return success();
	}
}