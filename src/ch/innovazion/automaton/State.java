package ch.innovazion.automaton;

import java.util.List;

public abstract class State {
	
	private final StateManager manager;
	
	public State(StateManager manager) {
		this.manager = manager;
	}
	
	protected void go(String identifier) {
		manager.performStateTransition(this, identifier);
	}
	

	protected void onRefresh(String identifier, Object prevValue) {
		;
	}
	
	protected void onEnter() {
		;
	}
	
	protected void onExit() {
		;
	}

	protected abstract List<String> getActions();
	protected abstract void onAction(String action);
}