package ch.innovazion.automaton.test;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.automaton.State;
import ch.innovazion.automaton.StateManager;

public class State3 extends State {

	public int field1;
	public int field2;
	public int field3;
	public int field4;
	public int field5;
	
	public State3(StateManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<String> getActions() {
		return Arrays.asList("..");
	}

	@Override
	public void onAction(String action) {
		go(action);
	}

}
