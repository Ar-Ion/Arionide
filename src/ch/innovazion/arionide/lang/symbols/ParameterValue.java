package ch.innovazion.arionide.lang.symbols;

import java.io.Serializable;
import java.util.List;

public interface ParameterValue extends Serializable {
	public List<String> getDisplayValue();
	public ParameterValue clone();
}
