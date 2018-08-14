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

import java.io.IOException;
import java.math.BigInteger;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.PrimitiveRenderer;
import org.azentreprise.arionide.ui.shaders.Shaders;

import com.jogamp.opengl.GL4;

public class GLRectangleRenderingContext extends GLRenderingContext {
	
	public static final int RGB_IDENTIFIER = 0;
	public static final int SCALE_IDENTIFIER = 1;
	public static final int TRANSLATION_IDENTIFIER = 2;
	public static final int ALPHA_IDENTIFIER = 3;
	public static final int LIGHT_STRENGTH_IDENTIFIER = 4;
	public static final int LIGHT_RADIUS_IDENTIFIER = 5;
	public static final int LIGHT_CENTER_IDENTIFIER = 6;

	private static final BigInteger[] scheme = Identification.makeScheme(7);
		
	private int shader;
	private int rgb;
	private int alpha;
	private int lightCenter;
	private int lightRadius;
	private int lightStrength;
	private int scale;
	private int translation;
	
	public void load(PrimitiveRenderer renderer) {
		GL4 gl = this.getGL(renderer);
		
		try {
			int vert = Shaders.loadShader(gl, "shape.vert", GL4.GL_VERTEX_SHADER);
			int frag = Shaders.loadShader(gl, "shape.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.shader = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
			
			gl.glBindFragDataLocation(this.shader, 0, "color");
			
			gl.glLinkProgram(this.shader);
			
			this.rgb = gl.glGetUniformLocation(this.shader, "rgb");
			this.alpha = gl.glGetUniformLocation(this.shader, "alpha");
			this.lightCenter = gl.glGetUniformLocation(this.shader, "lightCenter");
			this.lightRadius = gl.glGetUniformLocation(this.shader, "lightRadius");
			this.lightStrength = gl.glGetUniformLocation(this.shader, "lightStrength");
			this.scale = gl.glGetUniformLocation(this.shader, "scale");
			this.translation = gl.glGetUniformLocation(this.shader, "translation");
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void enter(PrimitiveRenderer renderer) {
		this.getGL(renderer).glUseProgram(this.shader);
	}

	public void exit(PrimitiveRenderer renderer) {
		return;
	}

	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getRGBUniform() {
		return this.rgb;
	}
	
	public int getAlphaUniform() {
		return this.alpha;
	}
	
	public int getLightCenterUniform() {
		return this.lightCenter;
	}
	
	public int getLightRadiusUniform() {
		return this.lightRadius;
	}
	
	public int getLightStrengthUniform() {
		return this.lightStrength;
	}
	
	public int getScaleUniform() {
		return this.scale;
	}
	
	public int getTranslationUniform() {
		return this.translation;
	}
}