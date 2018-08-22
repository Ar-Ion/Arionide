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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.IntBuffer;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.render.Identification;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureIO;

public class GLEdgeRenderingContext extends GLRectangleRenderingContext {

	public static final int EDGE_RADIUS_IDENTIFIER = GLRectangleRenderingContext.SCHEME_SIZE + 0;

	private static final BigInteger[] scheme = Identification.makeScheme(GLRectangleRenderingContext.SCHEME_SIZE + 1);
	
	private final File edgeFile;
	
	private int edgeFactor;
	
	private int sampler;
	private int edgeRadius;
	
	public GLEdgeRenderingContext(GL4 gl, Resources resources) {
		super(gl);
		
		this.edgeFile = resources.getResource("edge");
	}
	
	public void load() {
		super.load("edge.vert", "edge.frag");
		
		GL4 gl = this.getGL();
		
		this.edgeFactor = gl.glGetAttribLocation(this.getShaderID(), "edgeFactor");
		
		this.sampler = gl.glGetUniformLocation(this.getShaderID(), "edgeTexture");
		this.edgeRadius = gl.glGetUniformLocation(this.getShaderID(), "radius");
				
		try {
			InputStream stream = new FileInputStream(this.edgeFile);
			Buffer buffer = TextureIO.newTextureData(GLProfile.get(GLProfile.GL4), stream, false, TextureIO.PNG).getBuffer();

			IntBuffer texture = IntBuffer.allocate(1);
			gl.glGenTextures(1, texture);
			
			gl.glActiveTexture(GL4.GL_TEXTURE0);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, texture.get(0));
	
			int size = (int) Math.sqrt(buffer.limit() / 4); // assume it is a square texture
					
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, size, size, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, buffer);
			
			gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);
			
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST_MIPMAP_LINEAR);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		} catch (GLException | IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void enter() {
		super.enter();
		this.getGL().glUniform1i(this.sampler, 0);
	}
	
	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getEdgeFactorAttribute() {
		return this.edgeFactor;
	}
	
	public int getEdgeRadiusUniform() {
		return this.edgeRadius;
	}
}
