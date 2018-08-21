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
import java.nio.IntBuffer;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.font.GLFontRenderer;
import org.azentreprise.arionide.ui.shaders.Shaders;

import com.jogamp.opengl.GL4;

public class GLTextRenderingContext extends GLRenderingContext {

	public static final int TEXT_IDENTIFIER = 0;
	public static final int RGB_IDENTIFIER = 1;
	public static final int ALPHA_IDENTIFIER = 2;
	public static final int LIGHT_STRENGTH_IDENTIFIER = 3;
	public static final int LIGHT_RADIUS_IDENTIFIER = 4;
	public static final int LIGHT_CENTER_IDENTIFIER = 5;
	
	private static final BigInteger[] scheme = Identification.makeScheme(6);
	
	private final GLFontRenderer fontRenderer;
	
	private int shader;
	private int sampler;
	private int rgb;
	private int alpha;
	private int lightCenter;
	private int lightRadius;
	private int lightStrength;

	public GLTextRenderingContext(GL4 gl, GLFontRenderer fontRenderer) {
		super(gl);
		this.fontRenderer = fontRenderer;
	}
	
	public void load() {
		GL4 gl = this.getGL();
		
		try {
			int vert = Shaders.loadShader(gl, "text.vert", GL4.GL_VERTEX_SHADER);
			int frag = Shaders.loadShader(gl, "text.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.shader = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
			
			gl.glBindFragDataLocation(this.shader, 0, "color");
			
			gl.glLinkProgram(this.shader);
			
			this.sampler = gl.glGetUniformLocation(this.shader, "bitmap");
			this.rgb = gl.glGetUniformLocation(this.shader, "rgb");
			this.alpha = gl.glGetUniformLocation(this.shader, "alpha");
			this.lightCenter = gl.glGetUniformLocation(this.shader, "lightCenter");
			this.lightRadius = gl.glGetUniformLocation(this.shader, "lightRadius");
			this.lightStrength = gl.glGetUniformLocation(this.shader, "lightStrength");
			
			int translation = gl.glGetUniformLocation(this.shader, "translation");
			int scale = gl.glGetUniformLocation(this.shader, "scale");
			
			IntBuffer texture = IntBuffer.allocate(1);
			gl.glGenTextures(1, texture);
			
			this.fontRenderer.initRenderer(gl, this.shader, texture.get(0), translation, scale);
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void enter() {
		GL4 gl = this.getGL();
		
		gl.glUseProgram(this.shader);
		gl.glUniform1i(this.sampler, 1);
	}

	public void exit() {
		return;
	}
	
	public void onAspectRatioUpdate(float newRatio) {
		this.fontRenderer.windowRatioChanged(newRatio);
	}

	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getShaderID() {
		return this.shader;
	}
	
	public GLFontRenderer getFontRenderer() {
		return this.fontRenderer;
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
}