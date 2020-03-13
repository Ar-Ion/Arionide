package ch.innovazion.automaton.test;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;
import ch.innovazion.automaton.State;
import ch.innovazion.automaton.StateManager;

public class State4 extends State {

	public int field1;
	
	@Export
	@Inherit
	public int field2;
	
	public int field3;
	public int field4;
	public int field5;
	
	public State4(StateManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<String> getActions() {
		// TODO Auto-generated method stub
		return Arrays.asList("/", "..", "S5");
	}

	@Override
	protected void onAction(String action) {
		field2 *= 2;
		go(action);
	}

	protected void onRefresh(String identifier, Object prevValue) {
		if(identifier == "field2") {
			System.out.println("Field2: " + field2);
		}
	}
}
