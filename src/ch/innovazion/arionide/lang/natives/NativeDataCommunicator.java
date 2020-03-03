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
package ch.innovazion.arionide.lang.natives;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.lang.symbols.Parameter;

public class NativeDataCommunicator {
	
	private final NativeRuntime runtime;
	private final BiConsumer<String, Integer> channel;
	private final Stack<Integer> stack = new Stack<>();
	private final Map<Integer, Map<String, Entry<Boolean, Parameter>>> variables = new HashMap<>();
	private final List<Information> objects = new ArrayList<>();
	private final Stack<String> boundObjects = new Stack<>();
	
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
	
	public void setVariable(String name, boolean local, Parameter value) {
		Parameter element = this.getVariable(name);
		
		if(!local && element != null) {
			element.setValue(value.getDisplayValue());
		} else {
			this.variables.get(this.stack.peek()).put(name, new SimpleEntry<Boolean, Parameter>(local, value));			
		}
	}
	
	public Parameter getVariable(String name) {
		@SuppressWarnings("unchecked")
		Stack<Integer> stack = (Stack<Integer>) this.stack.clone();		
		
		boolean first = true;
				
		while(!stack.isEmpty()) {
			Entry<Boolean, Parameter> variable = this.variables.get(stack.pop()).get(name);
			
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
		Entry<Boolean, Parameter> var = this.variables.get(this.stack.peek()).get(name);
		return var != null ? var.getKey() : true;
	}
	
	public void exception(String message) {		
		this.channel.accept(message, 0xFF0000);
		
		while(!this.stack.empty()) {
			int element = this.stack.pop();
			this.channel.accept("In @{" + element + "} (" + element + ":?)", 0xFF7700);
		}
		
		new Exception().printStackTrace();
	}
	
	public String allocObject(String structure) {
		this.objects.add(new Information(Arrays.stream(structure.split("; ")).map(Integer::parseInt).collect(Collectors.toList()), "#@" + this.objects.size()));
		return "#@" + (this.objects.size() - 1);
	}
	
	public void bindObject(String identifier) {
		this.boundObjects.push(identifier);
	}
	
	public void unbindObject() {
		this.boundObjects.pop();
	}

	public Information getBoundObject() {
		return this.getObject(this.boundObjects.peek());
	}
	
	public Information getObject(String identifier) {
		if(identifier != null && identifier.startsWith("#@")) {
			return this.objects.get(Integer.parseInt(identifier.substring(2)));
		} else {
			return null;
		}
	}
	
	public String load(int object) {
		String identifier = this.allocObject(Integer.toString(NativeTypes.STRUCTURE));
		Information theObject = this.getObject(identifier);
		
		List<Entry<String, Specification>> code = this.runtime.getCode(object);
				
		for(Entry<String, Specification> instruction : code) {
			List<String> types = new ArrayList<>();
			
			types.add(Integer.toString(NativeTypes.TEXT));
			
			for(Parameter element : instruction.getValue().getElements()) {
				if(element instanceof Reference || ((Data) element).getType() == NativeTypes.INTEGER) {
					types.add(String.valueOf(NativeTypes.INTEGER));
				} else {
					types.add(String.valueOf(NativeTypes.TEXT));
				}
			}
			
			String instrID = this.allocObject(String.join("; ", types));
			Information instrObject = this.getObject(instrID);
			
			instrObject.add(instruction.getKey(), this);
			
			for(Parameter element : instruction.getValue().getElements()) {
				if(element.getDisplayValue().startsWith(Parameter.VAR)) {
					Parameter real = this.getVariable(element.getDisplayValue().substring(4));
					
					if(real != null) {
						instrObject.add(real.getDisplayValue(), this);	
					} else {
						instrObject.add(element.getDisplayValue(), this);
					}
				} else {
					instrObject.add(element.getDisplayValue(), this);
				}
			}
			
			theObject.add(instrID, this);
		}
		
		return identifier;
	}
}