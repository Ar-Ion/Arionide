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

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.gc.Trash;
import ch.innovazion.arionide.ui.gc.Trashable;
import ch.innovazion.arionide.ui.gc.UnusedVAO;
import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Scalar;
import ch.innovazion.arionide.ui.topology.Translation;

public abstract class GLTextRenderer implements TextRenderer {
		
	private final Map<String, GLTextCacheEntry> cache;
	
	private final GLTextTessellator tessellator;
	
	private boolean initialized = false;
	private int shader;
	private int translationUniform;
	private int scaleUniform;

	private float ratio = 1.0f;
		
	public GLTextRenderer(GLTextTessellator tessellator, int cacheSize) {
		this.tessellator = tessellator;
		this.cache = new LinkedHashMap<String, GLTextCacheEntry>(cacheSize, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(Map.Entry<String, GLTextCacheEntry> eldest) {
				if(this.size() > cacheSize) {
					eldest.getValue().invalidate();
					Trash.instance().throwAway(GLTextRenderer.this.getTrashable(eldest.getValue()));
					return true;
				} else {
					return false;
				}
			}
		};
	}

	public void initRenderer(GL4 gl, int shader, int translationUniform, int scaleUniform) {		
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
	
	public GLTextCacheEntry alloc(GL4 gl, String str) {
		this.checkInitialized();
		
		TessellationOutput output = this.tessellator.tessellateString(str);
		
		if(output == null) {
			return null;
		}
		
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

		GLTextCacheEntry entry = createCacheEntry(gl, str, output, vaoID, verticesBufferID, uvBufferID);
		
		this.cache.put(str, entry);
		
		return entry;
	}
	
	public abstract GLTextCacheEntry createCacheEntry(GL4 gl, String str, TessellationOutput tess, int vao, int verticesBuffer, int uvBuffer);
	
	public GLTextCacheEntry fetch(GL4 gl, String str) {
		GLTextCacheEntry entry = this.getCacheEntry(str);
		
		if(entry == null || entry.isInvalidated()) {
			entry = this.alloc(gl, str);
		}
		
		return entry;
	}
	
	public GLTextCacheEntry getCacheEntry(String str) {
		if(str != null && str.length() > FontRenderer.MAX_CHARS) {
			str = str.substring(0, FontRenderer.MAX_CHARS - 1);
		}
		
		return this.cache.get(str);
	}
		
	public Trashable getTrashable(String str) {		
		GLTextCacheEntry entry = this.cache.get(str);
		
		if(entry != null) {
			return this.getTrashable(entry);
		} else {
			return null;
		}
	}
	
	public Trashable getTrashable(GLTextCacheEntry entry) {		
		return new UnusedVAO(entry.getVAO(), entry.getFreeableResources());
	}
	
	public void windowRatioChanged(float newRatio) {
		this.ratio = newRatio;
	}
	
	public Affine renderString(GL4 gl, String str, Bounds bounds) {
		return this.renderString(gl, this.fetch(gl, str), bounds);
	}
	
	// Returns the affine transformation used by the bbox fitting system.
	public Affine renderString(GL4 gl, GLTextCacheEntry entry, Bounds bounds) {
		this.checkInitialized();
		
		if(entry == null) {
			return new Affine(new Scalar(0.0f, 0.0f), new Translation());
		}
		
		Point center = bounds.getCenter();
						
		float translateX = center.getX() - 1.0f;
		float translateY = 1.0f - center.getY();
		float scaleX = FontRenderer.BBOX_FIT_X * (float) bounds.getWidth() / entry.getWidth() * this.ratio;
		float scaleY = FontRenderer.BBOX_FIT_Y * (float) bounds.getHeight() / entry.getHeight();

		Scalar scalar = new Scalar();
		Translation translation = new Translation(translateX / entry.getWidth(), translateY / entry.getHeight());
		
		if(scaleY > scaleX) {
			gl.glUniform2f(this.scaleUniform, scaleX / this.ratio, scaleX);
			scalar.setScalar(scaleX / this.ratio, scaleX);
		} else {
			gl.glUniform2f(this.scaleUniform, scaleY / this.ratio, scaleY);
			scalar.setScalar(scaleY / this.ratio, scaleY);
		}
				
		gl.glUniform2f(this.translationUniform, translateX, translateY);
		gl.glBindVertexArray(entry.getVAO());

		renderPrimitives(gl, entry);
		
		return new Affine(scalar, translation);
	}
	
	public abstract void renderPrimitives(GL4 gl, GLTextCacheEntry entry);
		
	public GLTextTessellator getTessellator() {
		return this.tessellator;
	}
}