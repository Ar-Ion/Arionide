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

import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.menu.structure.Coloring;
import ch.innovazion.arionide.project.StructureMeta;

public class MutableStructureMeta implements StructureMeta {
	private static final long serialVersionUID = 1259382903286126347L;
	
	private String name = new String("?");
	private String comment = new String("?");
	private int colorID = Coloring.WHITE;
	private int spotColorID = Coloring.WHITE;
	private boolean accessAllowed = true;
	private Specification specification;
	private int language = 0;

	public MutableStructureMeta(int specID) {
		this.specification = new Specification(specID);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getColorID() {
		return this.colorID;
	}
	
	public void setColorID(int colorID) {
		this.colorID = colorID;
	}
	
	public int getSpotColorID() {
		return this.spotColorID;
	}
	
	public void setSpotColorID(int spotColorID) {
		this.spotColorID = spotColorID;
	}
	
	public boolean isAccessAllowed() {
		return this.accessAllowed;
	}
	
	public void setAccessAllowed(boolean allowed) {
		this.accessAllowed = allowed;
	}

	public Specification getSpecification() {
		return this.specification;
	}
	
	public void setSpecification(Specification specification) {
		this.specification = specification;
	}
	
	public int getLanguage() {
		return this.language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}
	
	public String toString() {
		return "[Name: " + this.name + "; Comment: " + this.comment + "; Specification: [" + this.specification + "]]";
	}
}