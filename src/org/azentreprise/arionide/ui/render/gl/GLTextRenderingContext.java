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
import java.nio.IntBuffer;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.ui.render.PrimitiveRenderer;
import org.azentreprise.arionide.ui.render.font.GLFontRenderer;
import org.azentreprise.arionide.ui.shaders.Shaders;

import com.jogamp.opengl.GL4;

public class GLTextRenderingContext extends GLRenderingContext {

	public static final int ALPHA_CHANNEL_IDENTIFIER = 0xFF000000;
	public static final int RGB_CHANNEL_IDENTIFIER = 0x00FFFFFF;
	
	private static final int[] scheme = new int[] {ALPHA_CHANNEL_IDENTIFIER, RGB_CHANNEL_IDENTIFIER};
	
	private final GLFontRenderer fontRenderer;
	
	private int shader;
	private int sampler;
	private int rgb;
	private int alpha;
	
	public GLTextRenderingContext(GLFontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
	}
	
	public void load(PrimitiveRenderer renderer) {
		GL4 gl = this.getGL(renderer);
		
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
			
			int translation = gl.glGetUniformLocation(this.shader, "translation");
			int scale = gl.glGetUniformLocation(this.shader, "scale");
			
			IntBuffer texture = IntBuffer.allocate(1);
			gl.glGenTextures(1, texture);
			
			this.fontRenderer.initRenderer(gl, this.shader, texture.get(0), translation, scale);
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void enter(PrimitiveRenderer renderer) {
		GL4 gl = this.getGL(renderer);
		
		gl.glUseProgram(this.shader);
		gl.glUniform1i(this.sampler, 1);
	}

	public void exit(PrimitiveRenderer renderer) {
		return;
	}

	public int[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getRGBUniform() {
		return this.rgb;
	}
	
	public int getAlphaUniform() {
		return this.alpha;
	}
}