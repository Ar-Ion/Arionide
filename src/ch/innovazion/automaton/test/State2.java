package ch.innovazion.automaton.test;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;
import ch.innovazion.automaton.State;
import ch.innovazion.automaton.StateManager;

public class State2 extends State {

	@Inherit
	public int field1;
	
	@Export
	@Inherit
	public int field2;
	
	public int field3;
	public int field4;
	public int field5;
	
	public State2(StateManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<String> getActions() {
		return Arrays.asList("..", "S4");
	}

	@Override
	protected void onAction(String action) {
		field2 += 1;
		go(action);
	}
	
	protected void onRefresh(String identifier, Object prevValue) {
		if(identifier == "field1") {
			System.out.println("Field1: " + field1);
		} else if(identifier == "field2") {
			System.out.println("Field2: " + field2);
		}
	}
}
