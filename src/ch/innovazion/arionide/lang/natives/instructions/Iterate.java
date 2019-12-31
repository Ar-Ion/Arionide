/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.lang.natives.instructions;

import java.util.List;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Object;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.natives.NativeTypes;

public class Iterate implements NativeInstruction {

	private final Data object;
	private final Data selector;
	private final Call updater;
	private final Data layers;
	private int index = 0;
	
	public Iterate(Data object, Data selector, Reference updater, Data layers) {
		this.object = object;
		this.selector = selector;
		this.updater = new Call(updater);
		this.layers = layers;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		this.index = 0;
		
		if(this.object.getValue().startsWith(SpecificationElement.VAR)) {
			SpecificationElement element = communicator.getVariable(this.object.getValue().substring(4));
			
			if(element != null) {
				String selector = this.selector.getValue();
				
				if(selector == null) {
					selector = new String();
				}
				
				if(selector.startsWith(SpecificationElement.VAR)) {
					selector = communicator.getVariable(selector.substring(4)).getValue();
				}
				
				String layers = this.layers.getValue();
				
				if(layers.startsWith(SpecificationElement.VAR)) {
					layers = communicator.getVariable(layers.substring(4)).getValue();
				}
				
				if(layers != null) {
					Object object = communicator.getObject(element.getValue());
					
					if(object != null) {
						for(Object obj : object.getObjects()) {
							this.scan(obj, selector, communicator, references, Integer.valueOf(layers.substring(1), layers.charAt(0) != 'd' ? layers.charAt(0) != 'b' ? 16 : 2 : 10));
						}
						
						return true;
					} else {
						communicator.exception("Invalid object: " + element.getValue());
					}
				} else {
					communicator.exception("Dead variable: " + this.selector.getValue());
				}
			} else {
				communicator.exception("Dead variable: " + this.object.getValue());
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
		}
		
		return false;
	}
	
	private void scan(Object obj, String selector, NativeDataCommunicator communicator, List<Integer> references, int layers) {
		if(obj.getValue() != null && (selector.isEmpty() || obj.getValue().startsWith(selector))) {			
			String value = obj.getValue().substring(selector.length());
			
			communicator.setVariable("value", true, new Data("value", value, NativeTypes.TEXT));
			communicator.setVariable("index", true, new Data("index", "d" + this.index++, NativeTypes.INTEGER));
			this.updater.execute(communicator, references);
			obj.setValue(communicator.getVariable("value").getValue());
		}
		
		if(layers > 0) {
			for(Object sub : obj.getObjects()) {
				this.scan(sub, selector, communicator, references, layers - 1);
			}
		}
	}
}