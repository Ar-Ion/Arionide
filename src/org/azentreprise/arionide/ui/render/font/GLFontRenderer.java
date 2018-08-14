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
package org.azentreprise.arionide.ui.render.font;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.TextureData;

public class GLFontRenderer implements FontRenderer {
	
	private static final int CACHE_CAPACITY = 1024;
	
	private final List<TextCacheEntry> trash = new ArrayList<>();
	
	private final Map<String, TextCacheEntry> cache = new LinkedHashMap<String, TextCacheEntry>() {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Map.Entry<String, TextCacheEntry> eldest) {
			if(this.size() > CACHE_CAPACITY) {
				return trash.add(eldest.getValue());
			} else {
				return false;
			}
		}
	};
	
	private final GLTextTessellator tessellator;
	private final TextureData fontData;
	
	private boolean initialized = false;
	private int indices;
	private int shader;
	private int translationUniform;
	private int scaleUniform;

	private float ratio = 1.0f;
		
	public GLFontRenderer(FontResources resources) {
		this.tessellator = new GLTextTessellator(resources.getMetrics());
		this.fontData = resources.getFontData();
	}

	public void initRenderer(GL4 gl, int shader, int textureID, int translationUniform, int scaleUniform) {		
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
		
		IntBuffer data = IntBuffer.allocate(6 * FontRenderer.MAX_CHARS);
		this.generateIndices(FontRenderer.MAX_CHARS, data);
		
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.indices);
		gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, data.capacity() * Integer.BYTES, data.flip(), GL4.GL_STATIC_DRAW);
		
		this.shader = shader;
		this.translationUniform = translationUniform;
		this.scaleUniform = scaleUniform;
		
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
	
	public TextCacheEntry alloc(GL4 gl, String str) {
		this.checkInitialized();
		
		TessellationOutput output = this.tessellator.tessellateString(str);
		
		Buffer verticesBuffer = output.getVerticesBuffer();
		Buffer uvBuffer = output.getUVBuffer();

		IntBuffer vao = IntBuffer.allocate(1);
		IntBuffer buffers = IntBuffer.allocate(2);
		
		gl.glGenVertexArrays(1, vao);
		
		int vaoID = vao.get(0);
		
		gl.glBindVertexArray(vaoID);
		
		gl.glGenBuffers(2, buffers);
		
		int verticesBufferID = buffers.get(0);
		int uvBufferID = buffers.get(1);
		
		// Vertices
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, verticesBufferID);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, verticesBuffer.capacity() * Float.BYTES, verticesBuffer, GL4.GL_STATIC_DRAW);
		
		int position = gl.glGetAttribLocation(this.shader, "position");
		gl.glVertexAttribPointer(position, 2, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(position);
		
		// UVs
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, uvBufferID);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, uvBuffer.capacity() * Float.BYTES, uvBuffer, GL4.GL_STATIC_DRAW);
		
		int uv = gl.glGetAttribLocation(this.shader, "uv");
		gl.glVertexAttribPointer(uv, 2, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(uv);

		// Indices
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.indices);
		
		TextCacheEntry entry = new TextCacheEntry(output.getWidth(), output.getHeight(), vaoID, new int[] {verticesBufferID, uvBufferID}, output.getCount());
		
		this.cache.put(str, entry);

		for(TextCacheEntry garbage : this.trash) {
			this.free(gl, garbage);
		}
		
		this.trash.clear();
		
		return entry;
	}
	
	public void free(GL4 gl, String str) {		
		TextCacheEntry entry = this.cache.get(str);
		
		if(entry != null) {
			this.free(gl, entry);
		}
	}
	
	public void free(GL4 gl, TextCacheEntry entry) {
		this.checkInitialized();
		
		IntBuffer freeables = entry.getFreeableResources();
		gl.glDeleteBuffers(freeables.capacity(), freeables);
		gl.glDeleteVertexArrays(1, IntBuffer.wrap(new int[] {entry.getVAO()}));
	}
	
	public void windowRatioChanged(float newRatio) {
		this.ratio = newRatio;
	}
	
	public Point2D renderString(GL4 gl, String str, Rectangle2D bounds) {
		if(str.length() > FontRenderer.MAX_CHARS) {
			str = str.substring(0, FontRenderer.MAX_CHARS - 1);
		}
		
		TextCacheEntry entry = this.cache.get(str);
		
		if(entry == null) {
			entry = this.alloc(gl, str);
		}
		
		return this.renderString(gl, entry, bounds);
	}
	
	// returns the origin of the rendered string
	public Point2D renderString(GL4 gl, TextCacheEntry entry, Rectangle2D bounds) {
		this.checkInitialized();
						
		float translateX = (float) bounds.getCenterX() - 1.0f;
		float translateY = 1.0f - (float) bounds.getCenterY();
		float scaleX = FontRenderer.BBOX_FIT_X * (float) bounds.getWidth() / entry.getWidth();
		float scaleY = FontRenderer.BBOX_FIT_Y * (float) bounds.getHeight() / entry.getHeight();

		if(scaleY > scaleX * this.ratio) {
			gl.glUniform2f(this.scaleUniform, scaleX, scaleX * this.ratio);
		} else {
			gl.glUniform2f(this.scaleUniform, scaleY / this.ratio, scaleY);
		}
		
		gl.glUniform2f(this.translationUniform, translateX, translateY);

		gl.glBindVertexArray(entry.getVAO());

		gl.glDrawElements(GL4.GL_TRIANGLES, entry.getCount() * 6, GL4.GL_UNSIGNED_INT, 0);

		return new Point2D.Float(translateX, translateY);
	}
	
	public GLTextTessellator getTessellator() {
		return this.tessellator;
	}
}
