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
package ch.innovazion.arionide.project.mutables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.ApplicationTints;

public abstract class MutableStructure implements Structure, Callable {
	
	private static final long serialVersionUID = 1259382903286126347L;
	
	private final int identifier;
	
	private String name = new String("?");
	private List<String> comment = new ArrayList<>();
	private int colorID = ApplicationTints.getColorIDByName("White");
	private int spotColorID = ApplicationTints.getColorIDByName("White");
	private boolean accessAllowed = true;
	private boolean lambda = false;
	private Specification specification;

	public MutableStructure(int structureID, int specID) {
		this.identifier = structureID;
		this.specification = new Specification(specID);
	}
	
	public int getIdentifier() {
		return identifier;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getComment() {
		return comment;
	}
	
	public void setComment(List<String> comment) {
		this.comment = Collections.unmodifiableList(comment);
	}
	
	public int getColorID() {
		return colorID;
	}
	
	public void setColorID(int colorID) {
		this.colorID = colorID;
	}
	
	public int getSpotColorID() {
		return spotColorID;
	}
	
	public void setSpotColorID(int spotColorID) {
		this.spotColorID = spotColorID;
	}
	
	public boolean isAccessAllowed() {
		return accessAllowed;
	}
	
	public void setAccessAllowed(boolean allowed) {
		this.accessAllowed = allowed;
	}
	
	public boolean isLambda() {
		return lambda;
	}
	
	public void setLambda(boolean isLambda) {
		this.lambda = isLambda;
	}

	public Specification getSpecification() {
		return specification;
	}
	
	public void setSpecification(Specification specification) {
		this.specification = specification;
	}
	
	public String toString() {
		return "[Name: " + name + "; Comment: " + comment + "; Specification: [" + specification + "]]";
	}
}