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

import ch.innovazion.arionide.project.StructureModel;

public class MutableAtomicStructure extends MutableStructure {
	
	private static final long serialVersionUID = -7922459397738938571L;

	public MutableAtomicStructure(int structureID, int specID) {
		super(structureID, specID);
	}
	
	public MutableAtomicStructure(int structureID, int specID, StructureModel model) {
		super(structureID, specID, model);
	}
	
	public boolean isAccessAllowed() {
		return false;
	}
	
	public void setAccessAllowed(boolean allowed) {
		throw new RuntimeException("Unsupported operation");
	}
	
	public boolean isLambda() {
		return false;
	}
	
	public void setLambda(boolean isLambda) {
		throw new RuntimeException("Unsupported operation");
	}
	
	public void setLanguage(String language) {
		throw new RuntimeException("Unsupported operation");
	}
	
	public String getLanguage() {
		return null;
	}
}
