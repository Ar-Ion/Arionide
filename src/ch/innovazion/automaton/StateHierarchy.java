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
package ch.innovazion.automaton;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class StateHierarchy {
		
	private final Map<String, State> states = new HashMap<>();
	private final Stack<String> path = new Stack<>();
	
	protected void enterState(Stack<String> newPath) {
		path.clear();
		path.addAll(newPath);
	}
	
	protected void back() {
		path.pop();
	}
	
	protected void reset() {
		states.clear();
		path.clear();
	}

	protected void register(String identifier, State state) {
		if(state == null) {
			throw new IllegalArgumentException("Cannot register a null-state");
		}
		
		states.put(identifier, state);
	}
	
	protected State resolveCurrentState() {
		String combined = composePath(path);
		
		State current = states.get(combined.replace(":", "/"));
		
		if(current != null) {
			return current;
		} else {
			try {
				path.pop();
			} catch(EmptyStackException exception) {
				System.err.println("Unable to restore a consistent state");
			}

			throw new StateResolutionError(combined);
		}
	}
	
	protected Stack<String> resolvePath(String identifier) {		
		@SuppressWarnings("unchecked")
		Stack<String> resolved = (Stack<String>) path.clone();
		
		identifier = normalizeIdentifier(identifier);
				
		try {			
			List<String> components = decomposePath(identifier);
						
			if(identifier.startsWith("/")) {
				resolved.clear();
				resolved.addAll(components);
			} else {				
				for(String component : components) {
					if(component.equals("..")) {
						resolved.pop();
					} else if(!component.equals(".")) {
						resolved.push(component);
					}
				}
			}
			
			return resolved;
		} catch(EmptyStackException exception) {
			throw new StateResolutionError(identifier);
		}
	}
	
	private String normalizeIdentifier(String identifier) {
		if(!identifier.startsWith("/") && identifier.startsWith("./")) {
			identifier = "./" + identifier;
		}
		
		if(!identifier.endsWith("/")) {
			identifier += "/";
		}
		
		return identifier;
	}
	
	protected List<String> decomposePath(String path) {
		List<String> components = new ArrayList<>();
		
		String buf = new String();
		
		for(char c : path.toCharArray()) {
			if(c == '/') {
				components.add(buf);
				buf = new String();
			} else {
				buf += c;
			}
		}
		
		return components;
	}
	
	protected String composePath(List<String> components) {
		String path = new String();
				
		for(String component : components) {
			path += '/';
			path += component;
		}
		
		if(components.size() > 1) {
			return path.substring(1);
		} else {
			return path;
		}
	}
	
	protected abstract void registerStates(StateManager mgr);
}
