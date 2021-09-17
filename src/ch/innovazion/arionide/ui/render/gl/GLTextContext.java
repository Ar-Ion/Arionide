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
package ch.innovazion.arionide.ui.render.gl;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.render.font.GLFontRenderer;
import ch.innovazion.arionide.ui.render.font.latex.GLLatexRenderer;

public class GLTextContext extends GLShapeContext {
	
	public static final int NUM_LATEX_TEXTURES = 9;
		
	private final GLFontRenderer fontRenderer;
	private final GLLatexRenderer latexRenderer;
	
	private int sampler;

	public GLTextContext(GL4 gl, GLFontRenderer fontRenderer, GLLatexRenderer latexRenderer) {
		super(gl);
		
		this.fontRenderer = fontRenderer;
		this.latexRenderer = latexRenderer;

		this.sampler = gl.glGetUniformLocation(this.getShaderID(), "bitmap");
		
		IntBuffer texture = IntBuffer.allocate(1 + NUM_LATEX_TEXTURES);
		gl.glGenTextures(1 + NUM_LATEX_TEXTURES, texture);
		
		this.fontRenderer.initRenderer(gl, this.getShaderID(), texture.get(0), this.getTranslationUniform(), this.getScaleUniform());
	}
	
	public void enter() {
		super.enter();
		this.getGL().glUniform1i(this.sampler, 1);
	}
	
	public void onAspectRatioUpdate(float newRatio) {
		this.fontRenderer.windowRatioChanged(newRatio);
	}

	public String getVertexShader() {
		return "textured.vert";
	}

	public String getFragmentShader() {
		return "text.frag";
	}
	
	public GLFontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
}