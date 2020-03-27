package ch.innovazion.arionide.lang.symbols;

import java.util.Collections;
import java.util.List;

public class Signature {
	private final String name;
	private final List<Parameter> parameters;
	
	public Signature(String name, List<Parameter> parameters) {
		this.name = name;
		this.parameters = parameters;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Parameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public String toString() {
		return name + ": " + parameters;
	}
}
