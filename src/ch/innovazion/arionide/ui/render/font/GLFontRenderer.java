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
package ch.innovazion.arionide.ui.render.font;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.TextureData;

public class GLFontRenderer extends GLTextRenderer implements FontRenderer {
	
	private static final int CACHE_CAPACITY = 256;

	private final TextureData fontData;

	private int indices;

	public GLFontRenderer(FontResources resources) {
		super(new GLFontTessellator(resources.getMetrics()), CACHE_CAPACITY);
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
		
		super.initRenderer(gl, shader, translationUniform, scaleUniform);
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
	

	public GLTextCacheEntry createCacheEntry(GL4 gl, String str, TessellationOutput tess, int vao, int verticesBuffer, int uvBuffer) {
		// Also bind indices to VAO
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.indices);
		
		GLTextCacheEntry entry = new GLTextCacheEntry(tess.getWidth(), tess.getHeight(), tess.getCount(), vao, 1, new int[] {verticesBuffer, uvBuffer});

		return entry;
	}
	
	public void renderPrimitives(GL4 gl, GLTextCacheEntry entry) {
		gl.glDrawElements(GL4.GL_TRIANGLES, entry.getCount() * 6, GL4.GL_UNSIGNED_INT, 0);
	}
}