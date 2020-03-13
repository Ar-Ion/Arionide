package ch.innovazion.automaton.test;

import java.util.Scanner;

import ch.innovazion.automaton.StateManager;

public class TestUI {
	
	private final Scanner input;
	private final StateManager manager;
	
	protected TestUI(Scanner input, StateManager manager) {
		this.input = input;
		this.manager = manager;
		
		manager.registerNotifiable(this::loadUI);
	}
	
	protected void load() {
		System.out.println("User interface initialized");
	}
	
	private void loadUI(String identifier) {
		System.out.println();
		System.out.println("Current state: " + identifier);
		System.out.println();
		
		System.out.println("Available actions: " + manager.getAvailableActions().stream().reduce((a, b) -> a + ", " + b).orElse("<None>"));
		System.out.print("Trigger action: ");
		String action = input.next();
		
		System.out.println();
		
		manager.triggerAction(action);
	}
}
