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

import java.math.BigInteger;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.render.Identification;
import ch.innovazion.arionide.ui.render.font.GLFontRenderer;
import ch.innovazion.arionide.ui.render.font.latex.GLLatexRenderer;

public class GLTextContext extends GLShapeContext {
	
	public static final int USE_LATEX_IDENTIFIER = SCHEME_SIZE + 0;	
	public static final int NUM_LATEX_TEXTURES = GLLatexRenderer.GL_CACHE_CAPACITY;
	
	public static final int FETCH_CACHE_ACTION_IDENTIFIER = 0x2;
	public static final int UPDATE_TEXTURE_ACTION_IDENTIFIER = 0x4;


	private static final BigInteger[] scheme = Identification.makeScheme(SCHEME_SIZE + 1);
		
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
		
		int fontTexture = texture.get();
		int[] latexTextures = new int[NUM_LATEX_TEXTURES];
		texture.get(latexTextures, 0, NUM_LATEX_TEXTURES);
		
		this.fontRenderer.initRenderer(gl, this.getShaderID(), fontTexture, this.getTranslationUniform(), this.getScaleUniform());
		this.latexRenderer.initRenderer(gl, this.getShaderID(), latexTextures, this.getTranslationUniform(), this.getScaleUniform());
	}

	public void onAspectRatioUpdate(float newRatio) {
		this.fontRenderer.windowRatioChanged(newRatio);
	}
	
	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getSamplerUniform() {
		return this.sampler;
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
	
	public GLLatexRenderer getLatexRenderer() {
		return this.latexRenderer;
	}
}