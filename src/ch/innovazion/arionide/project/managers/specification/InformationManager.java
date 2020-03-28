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
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.AtomicValue;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.InvalidValueException;
import ch.innovazion.arionide.lang.symbols.SymbolResolutionException;
import ch.innovazion.arionide.project.Storage;

public class InformationManager extends ContextualManager<Information> {
	protected InformationManager(Storage storage) {
		super(storage);
	}
	
	public List<Information> getChildren() {
		return getContext().getInformation();
	}
	
	public MessageEvent setLabel(String name) {
		getContext().label(name);
		return success();
	}
	
	public MessageEvent setValue(String rawValue) {
		try {
			getContext().parse(rawValue);
			return success();
		} catch (InvalidValueException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
	
	public MessageEvent destroy(Information parent) {
		try {
			parent.disconnect(getContext());
			return success();
		} catch (SymbolResolutionException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
	
	public MessageEvent assign(Information parent, Information value) {
		try {
			if(parent instanceof AtomicValue) {
				return new MessageEvent("Cannot assign a node value to an atomic information", MessageType.ERROR);
			}
			
			int index = parent.indexOf(getContext());
			parent.disconnect(getContext());
			parent.connect(value, index);
			
			return success();
		} catch (SymbolResolutionException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
}