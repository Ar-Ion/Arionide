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
package org.azentreprise.arionide.ui.primitives.font;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.ui.FontResources;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.TextureData;

public class FontRenderer {
	
	public static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!№;%:?*()_+-=.,/|\\\"'@#$^&{}[]°§<>≤≥";
	
	private static final float bboxFitting = 0.5f;
	private static final int maxChars = 256;
	
	private final Map<String, TextCacheEntry> cache = new HashMap<>();
	private final TextTessellator tessellator;
	private final TextureData fontData;
	
	private boolean initialized = false;
	private int indices;
	private int shader;
	private int samplerUniform;
	private int translationUniform;
	private int scaleUniform;
	private int rgbUniform;
	private int alphaUniform;
	
	private float r;
	private float g;
	private float b;
	private float alpha;
	
	private float ratio = 1.0f;
		
	public FontRenderer(FontResources resources) {
		this.tessellator = new TextTessellator(resources.getMetrics());
		this.fontData = resources.getFontData();
	}

	public void initRenderer(GL4 gl, int shader, int textureID) {
		gl.glActiveTexture(GL4.GL_TEXTURE1);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textureID);
		
		int size = (int) Math.sqrt(this.fontData.getBuffer().limit() / 4); // assume it is a square texture
		
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, size, size, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, this.fontData.getBuffer());
		
		gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenBuffers(1, buffer);
		this.indices = buffer.get(0);
		
		IntBuffer data = IntBuffer.allocate(6 * maxChars);
		this.generateIndices(maxChars, data);
		
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.indices);
		gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, data.capacity() * Integer.BYTES, data.flip(), GL4.GL_STATIC_DRAW);
		
		this.shader = shader;
		this.samplerUniform = gl.glGetUniformLocation(shader, "bitmap");
		this.translationUniform = gl.glGetUniformLocation(shader, "translation");
		this.scaleUniform = gl.glGetUniformLocation(shader, "scale");
		this.rgbUniform = gl.glGetUniformLocation(shader, "rgb");
		this.alphaUniform = gl.glGetUniformLocation(shader, "alpha");
		
		this.initialized = true;
	}
	
	private void checkInitialized() {
		if(!this.initialized) {
			throw new IllegalStateException();
		}
	}
	
	private void generateIndices(int count, IntBuffer indices) {
		for(int i = 0; i < count; i++) {
			indices.put(4 * i);
			indices.put(4 * i + 1);
			indices.put(4 * i + 2);
			indices.put(4 * i + 1);
			indices.put(4 * i + 2);
			indices.put(4 * i + 3);
		}
	}
	
	public TextCacheEntry prepareString(GL4 gl, String str) {
		this.checkInitialized();
		
		TessellationOutput output = this.tessellator.tessellateString(str);
		
		Buffer verticesBuffer = output.getVerticesBuffer();
		Buffer uvBuffer = output.getUVBuffer();

		IntBuffer vao = IntBuffer.allocate(1);
		IntBuffer buffers = IntBuffer.allocate(3);
		
		gl.glGenVertexArrays(1, vao);
		
		int vaoID = vao.get(0);
		
		gl.glBindVertexArray(vaoID);
		
		gl.glGenBuffers(3, buffers);
		
		// Vertices
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, verticesBuffer.capacity() * Float.BYTES, verticesBuffer, GL4.GL_STATIC_DRAW);
		
		int position = gl.glGetAttribLocation(this.shader, "position");
		gl.glVertexAttribPointer(position, 2, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(position);
		
		// UVs
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, uvBuffer.capacity() * Float.BYTES, uvBuffer, GL4.GL_STATIC_DRAW);
		
		int uv = gl.glGetAttribLocation(this.shader, "uv");
		gl.glVertexAttribPointer(uv, 2, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(uv);

		// Indices
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.indices);
		
		TextCacheEntry entry = new TextCacheEntry(output.getWidth(), output.getHeight(), vaoID, output.getCount());
		
		this.cache.put(str, entry);
		
		return entry;
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public void windowRatioChanged(float newRatio) {
		this.ratio = newRatio;
	}
	
	public Point2D renderString(GL4 gl, String str, Rectangle2D bounds) {
		if(str.length() > maxChars) {
			str = str.substring(0, maxChars - 1);
		}
		
		TextCacheEntry entry = this.cache.get(str);
		
		if(entry == null) {
			// Dynamic string rendering: performance loss and GPU memory leak (TODO cache clearing system) --> use with care!
			entry = this.prepareString(gl, str);
		}
		
		return this.renderString(gl, entry, bounds);
	}
	
	// returns the origin of the rendered string
	public Point2D renderString(GL4 gl, TextCacheEntry entry, Rectangle2D bounds) {
		this.checkInitialized();
				
		float halfScaleX = (float) bounds.getWidth() / entry.getWidth() * this.ratio;
		float halfScaleY = (float) bounds.getHeight() / entry.getHeight() / this.ratio;
		
		float halfMainScale = bboxFitting * Math.min(halfScaleX, halfScaleY);
		
		float translateX = (float) bounds.getCenterX() - 1.0f;
		float translateY = (float) -bounds.getCenterY() + 1.0f;
		
		boolean horizontalLead = halfScaleX > halfScaleY;

		gl.glUseProgram(this.shader);
		
		gl.glUniform1i(this.samplerUniform, 1);
		gl.glUniform2f(this.translationUniform, translateX, translateY);
		gl.glUniform2f(this.scaleUniform, halfMainScale / (horizontalLead ? 1.0f : this.ratio), halfMainScale * (horizontalLead ? this.ratio : 1.0f));
		gl.glUniform3f(this.rgbUniform, this.r, this.g, this.b);
		gl.glUniform1f(this.alphaUniform, this.alpha);
				
		gl.glBindVertexArray(entry.getVAO());

		gl.glDrawElements(GL4.GL_TRIANGLES, entry.getCount() * 6, GL4.GL_UNSIGNED_INT, 0);

		return new Point2D.Float(translateX, translateY);
	}
	
	public TextTessellator getTessellator() {
		return this.tessellator;
	}
}
