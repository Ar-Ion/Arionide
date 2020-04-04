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
package ch.innovazion.arionide.lang.symbols;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Actor implements ParameterValue {

	private static final long serialVersionUID = -8776466553804724034L;

	private final Set<Actor> parents = new HashSet<>();
	private final Set<Reference> actions = new HashSet<>();
	private Node props = new Node("props");
	private Node state = new Node("state");
	private Node constants = new Node("consts");

	public Node getProperties() {
		return props;
	}
	
	public Node getState() {
		return state;
	}
	
	public Node getConstants() {
		return constants;
	}
	
	public Set<Actor> getParents() {
		return parents;
	}
	
	public Set<Reference> getAbilities() {
		return actions;
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList("");
	}

	public ParameterValue clone() {
		Actor clone = new Actor();
		
		clone.parents.addAll(parents);
		clone.actions.addAll(actions);
		clone.props = props.clone();
		clone.state = state.clone();
		
		return null;
	}

}
