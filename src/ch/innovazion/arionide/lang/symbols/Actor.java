package ch.innovazion.arionide.lang.symbols;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Actor implements ParameterValue {

	private static final long serialVersionUID = -8776466553804724034L;

	private final Set<Actor> parents = new HashSet<>();
	private final Set<Reference> actions = new HashSet<>();
	private final Variable state = new Variable();
	private Information props = new Information();
	
	public Information getProperties() {
		return props;
	}
	
	public Variable getState() {
		return state;
	}
	
	public Set<Actor> getParents() {
		return parents;
	}
	
	public Set<Reference> getAbilities() {
		return actions;
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList("");
	}

	public ParameterValue clone() {
		Actor clone = new Actor();
		
		clone.parents.addAll(parents);
		clone.actions.addAll(actions);
		clone.state.setInitialValue(state.getInitialValue().clone());
		clone.props = props.clone();
		
		return null;
	}

}
