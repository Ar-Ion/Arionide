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
package org.azentreprise.arionide.lang.natives;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.function.BiConsumer;

import org.azentreprise.arionide.lang.SpecificationElement;

public class NativeDataCommunicator {
	
	private final NativeRuntime runtime;
	private final BiConsumer<String, Integer> channel;
	private final Stack<Integer> stack = new Stack<>();
	private final Map<Integer, Map<String, Entry<Boolean, SpecificationElement>>> variables = new HashMap<>();

	protected NativeDataCommunicator(NativeRuntime runtime, BiConsumer<String, Integer> channel) {
		this.runtime = runtime;
		this.channel = channel;
	}
	
	public void info(String message, int color) {
		this.channel.accept("[prog] " + message, color);
	}
	
	public void exec(int structureID) {
		this.runtime.exec(structureID);
	}
	
	public Stack<Integer> getStack() {
		return this.stack;
	}
	
	public void initVariablePool() {
		this.variables.put(this.stack.peek(), new HashMap<>());
	}
	
	public void clearVariablePool() {
		this.variables.remove(this.stack.peek());
	}
	
	public void setVariable(String name, boolean local, SpecificationElement value) {
		SpecificationElement element = this.getVariable(name);
		
		if(!local && element != null) {
			element.setValue(value.getValue());
		} else {
			this.variables.get(this.stack.peek()).put(name, new SimpleEntry<Boolean, SpecificationElement>(local, value));			
		}
	}
	
	public SpecificationElement getVariable(String name) {
		@SuppressWarnings("unchecked")
		Stack<Integer> stack = (Stack<Integer>) this.stack.clone();
	
		boolean first = true;
				
		while(!stack.isEmpty()) {
			Entry<Boolean, SpecificationElement> variable = this.variables.get(stack.pop()).get(name);
			
			if(variable != null) {
				if(first || !variable.getKey()) { // If it is the current scope or the variable is not local
					return variable.getValue();
				}
			}
				
			first = false;
		}
		
		return null;
	}
	
	public boolean isDefined(String name) {
		return this.variables.get(this.stack.peek()).containsKey(name);
	}
	
	public boolean isLocal(String name) {
		Entry<Boolean, SpecificationElement> var = this.variables.get(this.stack.peek()).get(name);
		return var != null ? var.getKey() : true;
	}
	
	public void exception(String message) {
		this.channel.accept(message, 0xFF0000);
		
		while(!this.stack.empty()) {
			int element = this.stack.pop();
			this.channel.accept("In @{" + element + "} (" + element + ":?)", 0xFF7700);
		}
	}
}