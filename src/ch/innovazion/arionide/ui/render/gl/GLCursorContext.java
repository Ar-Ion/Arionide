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
package ch.innovazion.arionide.ui.render.gl;

import java.io.IOException;
import java.math.BigInteger;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.ui.render.Identification;
import ch.innovazion.arionide.ui.shaders.Shaders;
import ch.innovazion.arionide.ui.shaders.preprocessor.DummySettings;

public class GLCursorContext extends GLRenderingContext {
	
	public static final int SIZE_IDENTIFIER = 0;
	public static final int PREPARE_ACTION_IDENTIFIER = 0x1;

	private static final BigInteger[] scheme = Identification.makeScheme(1);
	
	private int shader;	
	private int position;

	protected GLCursorContext(GL4 gl) {
		super(gl);
	}
	
	public void load() {
		GL4 gl = this.getGL();
		
		try {
			int vert = Shaders.loadShader(gl, "cursor.vert", DummySettings.VERTEX);
			int frag = Shaders.loadShader(gl, "cursor.frag", DummySettings.FRAGMENT);
			
			this.shader = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
			
			gl.glBindFragDataLocation(this.shader, 0, "color");
			
			gl.glLinkProgram(this.shader);
			
			this.position = gl.glGetAttribLocation(this.shader, "position");
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void enter() {
		GL4 gl = this.getGL();
		
		gl.glUseProgram(this.shader);
		gl.glBlendFunc(GL4.GL_ONE_MINUS_DST_COLOR, GL4.GL_ZERO);
	}

	public void exit() {
		this.getGL().glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void onAspectRatioUpdate(float newRatio) {
		return;
	}

	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getShaderID() {
		return this.shader;
	}
	
	public int getPositionAttribute() {
		return this.position;
	}
}
