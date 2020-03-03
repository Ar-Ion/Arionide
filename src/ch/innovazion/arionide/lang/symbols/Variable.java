package ch.innovazion.arionide.lang.symbols;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/*
 * Runtime variable
 */
public class Variable extends AtomicValue {

	private static final long serialVersionUID = 8748976008265242711L;
	
	private String name;
	private Information initialValue = new Number(); // Static variable allocation
	
	public Variable() {
		label(null);
	}
	
	public void label(String name) {
		this.name = (name != null && !name.isEmpty()) ? name : "Lambda variable";
	}
	
	public void setInitialValue(Information initialValue) {
		this.initialValue = initialValue;
	}
	
	public Information getInitialValue() {
		return initialValue;
	}
	
	public Stream<Bit> getRawStream() {
		return initialValue.getRawStream();
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList(name, "(" + initialValue.getSize() + " bit(s))");
	}
	
	public Variable clone() {
		Variable clone = new Variable();
		
		clone.initialValue = initialValue.clone();
		
		return clone;
	}
}
