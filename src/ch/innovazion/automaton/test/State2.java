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
	public void onAction(String action) {
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
