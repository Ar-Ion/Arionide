/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
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
