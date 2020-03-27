package ch.innovazion.arionide.lang.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Enumeration extends AtomicValue {
	
	private static final long serialVersionUID = 1209877426348042694L;

	private final List<String> nameMapping = new ArrayList<>();
	private final Map<String, Information> possibleValues = new HashMap<>();

	private Information value;
	
	public void addPossibleValue(String name) {
		nameMapping.add(name);
		possibleValues.put(name, new Information());
	}
	
	/*
	 * Method should be accessed only by EnumerationManager
	 */
	public Information getValue(String name) {
		return possibleValues.get(name);
	}
	
	public void setValue(String name) {
		this.value = possibleValues.get(name);
	}
	
	public Information getValue() {
		if(value != null) {
			return value;
		} else if(!nameMapping.isEmpty()) {
			return possibleValues.get(nameMapping.get(0));
		} else {
			throw new IllegalStateException("Empty enumerations cannot be resolved");
		}
	}
	
	protected Stream<Bit> getRawStream() {
		return getValue().getRawStream();
	}

	public AtomicValue clone() {
		Enumeration enumeration = new Enumeration();
		enumeration.nameMapping.addAll(nameMapping);
		enumeration.possibleValues.putAll(possibleValues);
		return enumeration;
	}
}