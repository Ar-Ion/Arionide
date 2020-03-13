package ch.innovazion.automaton.test;

import ch.innovazion.automaton.StateHierarchy;
import ch.innovazion.automaton.StateManager;

public class TestHierarchy extends StateHierarchy {
	protected void registerStates(StateManager mgr) {
		register("/", new State1(mgr));
		register("/S2", new State2(mgr));
		register("/S3", new State3(mgr));
		register("/S2/S4", new State4(mgr));
		register("/S2/S4/S5", new State5(mgr));
	}
}