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
package ch.innovazion.arionide.ui.core.gl.stars;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.Context;

public class StarsContext implements Context {

	private final int positionAttribute;
	private final int colorAttribute;
	private final int modelUniform;
	private final int viewUniform;
	private final int projectionUniform;
	
	public StarsContext(GL4 gl, int shader) {
		this.positionAttribute = gl.glGetAttribLocation(shader, "position");
		this.colorAttribute = gl.glGetAttribLocation(shader, "color");
		
		this.modelUniform = gl.glGetUniformLocation(shader, "model");
		this.viewUniform = gl.glGetUniformLocation(shader, "view");
		this.projectionUniform = gl.glGetUniformLocation(shader, "projection");
	}
	
	protected int getPositionAttribute() {
		return positionAttribute;
	}
	
	protected int getColorAttribute() {
		return colorAttribute;
	}
	
	protected int getModelUniform() {
		return modelUniform;
	}
	
	protected int getViewUniform() {
		return viewUniform;
	}
	
	protected int getProjectionUniform() {
		return projectionUniform;
	}
}
