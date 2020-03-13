package ch.innovazion.automaton.test;

import java.util.Scanner;

import ch.innovazion.automaton.StateHierarchy;
import ch.innovazion.automaton.StateManager;

public class Main {
	public static void main(String args[]) {
		StateHierarchy hierarchy = new TestHierarchy();
		StateManager manager = new StateManager(hierarchy, true);
		
		Scanner scanner = new Scanner(System.in);
		
		TestUI ui = new TestUI(scanner, manager);

		ui.load();
		
		manager.go("/");
	}
}
