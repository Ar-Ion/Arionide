/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
import ch.innovazion.arionide.project.StructureMeta;

public class MutableCodeMeta extends MutableStructureMeta {
	private static final long serialVersionUID = 1259382903286126347L;
	
	private final StructureMeta codeBase;
	private Specification specification;

	public MutableCodeMeta(StructureMeta codeBase) {
		super(-1);
		
		this.codeBase = codeBase;
		this.specification = new Specification(codeBase.getSpecification());
	}
	
	public String getName() {
		return codeBase.getName();
	}
	
	public int getColorID() {
		return codeBase.getColorID();
	}

	public int getSpotColorID() {
		return codeBase.getSpotColorID();
	}
	
	public boolean isAccessAllowed() {
		return codeBase.isAccessAllowed();
	}

	public Specification getSpecification() {
		return specification;
	}
	
	public int getLanguage() {
		return codeBase.getLanguage();
	}

	public String toString() {
		return "[Name: " + getName() + "; Comment: " + getComment() + "; Specification: [" + this.specification + "]]";
	}
}
