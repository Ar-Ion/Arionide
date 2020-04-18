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
package ch.innovazion.arionide.project.managers;

import ch.innovazion.arionide.coders.Coder;
import ch.innovazion.arionide.project.Project;

public class ResourceAllocator {
	
	private final Project project;
	
	protected ResourceAllocator(Project project) {
		this.project = project;
	}
	
	public int allocSpecification() {
		return this.alloc("specificationGen");
	}
	
	public int allocStructure() {
		return this.alloc("structureGen");
	}
	
	private int alloc(String object) {
		int id = this.project.getProperty(object, Coder.integerDecoder).intValue();
		this.project.setProperty(object, (long) id + 1, Coder.integerEncoder); // Increment generator

		return id;
	}
	
	public int nextSpecification() {
		return this.next("specificationGen");
	}
	
	public int nextStructure() {
		return this.next("structureGen");
	}
	
	private int next(String object) {
		return this.project.getProperty(object, Coder.integerDecoder).intValue();
	}
}
