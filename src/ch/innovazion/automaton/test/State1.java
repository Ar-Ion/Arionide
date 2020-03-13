package ch.innovazion.automaton.test;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.State;
import ch.innovazion.automaton.StateManager;

public class State1 extends State {
	
	@Export
	public int field1;
	
	@Export
	public int field2;
	
	public int field3;
	public int field4;
	public int field5;

	public State1(StateManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<String> getActions() {
		// TODO Auto-generated method stub
		return Arrays.asList("S2", "S3");
	}

	@Override
	protected void onAction(String action) {
		field1++;
		go(action);
	}

}
