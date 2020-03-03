package ch.innovazion.arionide.lang.symbols;

import java.util.stream.Stream;

/*
 * Runtime variable
 */
public class Variable extends AtomicValue {

	private static final long serialVersionUID = 8748976008265242711L;
	
	private Information initialValue = new Number(); // Static variable allocation
	
	public void setInitialValue(Information initialValue) {
		this.initialValue = initialValue;
	}
	
	public Information getInitialValue() {
		return initialValue;
	}

	public void parse(String value) {
		label(value);
	}
	
	public Stream<Bit> getRawStream() {
		return initialValue.getRawStream();
	}
}
