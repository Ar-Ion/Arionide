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
package ch.innovazion.arionide.lang;

import java.util.function.BiConsumer;

import ch.innovazion.arionide.project.Project;

public abstract class Runtime {
	
	private final Project project;
	private BiConsumer<String, Integer> output;

	public Runtime(Project project) {
		this.project = project;
	}
	
	protected Project getProject() {
		return this.project;
	}
	
	public void setupOutput(BiConsumer<String, Integer> output) {
		this.output = output;
	}
	
	protected void info(String info, int color) {
		this.output.accept(info, color);
	}
	
	public abstract void run(int id);
}