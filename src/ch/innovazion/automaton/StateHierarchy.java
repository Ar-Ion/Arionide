package ch.innovazion.automaton;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		
	protected void register(String identifier, State state) {
		if(state == null) {
			throw new IllegalArgumentException("Cannot register a null-state");
		}
		
		states.put(identifier, state);
	}
	
	protected State resolveCurrentState() {
		String combined = composePath(path);
		return Optional.ofNullable(states.get(combined)).orElseThrow(() -> new StateResolutionError(combined));
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
