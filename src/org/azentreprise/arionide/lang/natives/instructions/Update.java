/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Object;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class Update implements NativeInstruction {

	private final Data object;
	private final Data selector;
	private final Call updater;
	
	public Update(Data object, Data selector, Reference updater) {
		this.object = object;
		this.selector = selector;
		this.updater = new Call(updater);
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.object.getValue().startsWith("var@")) {
			SpecificationElement element = communicator.getVariable(this.object.getValue().substring(4));
			
			if(element != null) {
				String selector = this.selector.getValue();
				
				if(selector.startsWith("var@")) {
					selector = communicator.getVariable(selector.substring(4)).getValue();
				}
				
				if(selector != null) {
					Object object = communicator.getObject(element.getValue());
					
					if(object != null) {
						for(Object obj : object.getObjects()) {
							this.scan(obj, selector, communicator, references);
						}
						
						return true;
					} else {
						communicator.exception("Invalid object: " + element.getValue());
					}
				} else {
					communicator.exception("Dead variable: " + selector);
				}
			} else {
				communicator.exception("Dead variable: " + this.object.getValue());
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
		}
		
		return false;
	}
	
	private void scan(Object obj, String selector, NativeDataCommunicator communicator, List<Integer> references) {
		if(obj.getValue() != null && obj.getValue().startsWith(selector)) {
			String value = obj.getValue().substring(selector.length());
			
			communicator.setVariable("value", true, new Data("value", value, NativeTypes.TEXT));
			this.updater.execute(communicator, references);
			obj.setValue(communicator.getVariable("value").getValue());
		}
		
		for(Object sub : obj.getObjects()) {
			this.scan(sub, selector, communicator, references);
		}
	}
}