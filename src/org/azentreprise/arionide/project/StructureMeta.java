/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.project;

import java.io.Serializable;

import org.azentreprise.arionide.ui.menu.edition.Coloring;

public class StructureMeta implements Serializable {
	private static final long serialVersionUID = 5582101421258702064L;
	
	private String name = new String("?");
	private String comment = new String("?");
	private int colorID = Coloring.WHITE;
	private int spotColorID = Coloring.WHITE;
	private boolean accessAllowed = true;
	private Specification specification = new Specification();

	public String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	protected void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getColorID() {
		return this.colorID;
	}
	
	protected void setColorID(int colorID) {
		this.colorID = colorID;
	}
	
	public int getSpotColorID() {
		return this.spotColorID;
	}
	
	protected void setSpotColorID(int spotColorID) {
		this.spotColorID = spotColorID;
	}
	
	public boolean isAccessAllowed() {
		return this.accessAllowed;
	}
	
	protected void setAccessAllowed(boolean allowed) {
		this.accessAllowed = allowed;
	}

	public Specification getSpecification() {
		return this.specification;
	}
	
	protected void setSpecification(Specification specification) {
		this.specification = specification;
	}
	
	public String toString() {
		return "Name: " + this.name + "; Comment: " + this.comment;
	}
}