package ch.innovazion.arionide.lang.symbols;

import java.util.Arrays;
import java.util.List;

/*
 * Compile-time alias
 */
public class Alias implements ParameterValue {

	private static final long serialVersionUID = -1355328834457298750L;
	
	private final ParameterValue value;
	private String alias;
	
	public Alias(String alias, ParameterValue value) {
		this.alias = alias;
		this.value = value;
	}
	
	public void alias(String newAlias) {
		this.alias = newAlias;
	}
	
	public ParameterValue resolve() {
		ParameterValue resolved = value;
		
		while(resolved instanceof Alias) {
			resolved = ((Alias) resolved).value;
		}
		
		return resolved;
	}
		
	public List<String> getDisplayValue() {
		return Arrays.asList(alias);
	}
}
