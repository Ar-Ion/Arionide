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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureIO;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.ui.render.Identification;

public class GLEdgeContext extends GLPolygonContext {

	public static final int EDGE_RADIUS_IDENTIFIER = SCHEME_SIZE + 0;

	private static final BigInteger[] scheme = Identification.makeScheme(SCHEME_SIZE + 1);
	
	private final File edgeFile;
	
	private int edgeFactor;
	private int edgeRadius;
	
	public GLEdgeContext(GL4 gl, Resources resources) {
		super(gl);
		
		this.edgeFile = resources.getResource("edge");
		
		this.edgeFactor = gl.glGetAttribLocation(this.getShaderID(), "edgeFactor");
		
		int sampler = gl.glGetUniformLocation(this.getShaderID(), "edgeTexture");
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
						
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
			
			gl.glUniform1i(sampler, getTextureID());
		} catch (GLException | IOException exception) {
			Debug.exception(exception);
		}
	}
	
	protected int getTextureID() {
		return 0;
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
	
	public String getVertexShader() {
		return "edge.vert";
	}
	
	public String getFragmentShader() {
		return "edge.frag";
	}
}
