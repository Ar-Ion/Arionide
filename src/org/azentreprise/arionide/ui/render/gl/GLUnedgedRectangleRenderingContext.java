/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.render.gl;

import java.math.BigInteger;

import org.azentreprise.arionide.ui.render.Identification;

import com.jogamp.opengl.GL4;

public class GLUnedgedRectangleRenderingContext extends GLRectangleRenderingContext {

	public static final int UNEDGING_RADIUS_IDENTIFIER = GLRectangleRenderingContext.SCHEME_SIZE + 0;

	private static final BigInteger[] scheme = Identification.makeScheme(GLRectangleRenderingContext.SCHEME_SIZE + 1);
		
	private int unedgingFactor;
	private int unedgingRadius;
	
	public GLUnedgedRectangleRenderingContext(GL4 gl) {
		super(gl);
	}
	
	public void load() {
		super.load("unedged_shape.vert", "shape.frag");
		
		GL4 gl = this.getGL();
		
		this.unedgingFactor = gl.glGetAttribLocation(this.getShaderID(), "unedgingFactor");
		this.unedgingRadius = gl.glGetUniformLocation(this.getShaderID(), "radius");
	}

	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getUnedgingFactorAttribute() {
		return this.unedgingFactor;
	}
	
	public int getUnedgingRadiusUniform() {
		return this.unedgingRadius;
	}
}