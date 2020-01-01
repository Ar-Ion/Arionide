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
package ch.innovazion.arionide.ui.core.gl;

import java.nio.Buffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

public abstract class RenderableObject<C extends Context, S extends Settings> implements Renderable {
	
	private final S settings;
	
	private GL4 gl;
	private C context;
	private int id = -1;
	
	protected RenderableObject(S settings) {
		this.settings = settings;
	}
	
	public S getSettings() {
		return this.settings;
	}
	
	public void bind() {
		ensureInitialized();
		gl.glBindVertexArray(id);
	}
	
	public void render() {
		ensureInitialized();
		update(gl, context, settings);
		renderObject(gl);
	}
	
	public void init(GL4 gl, C context, StaticAllocator allocator) {
		ensureNotInitialized();
		
		this.gl = gl;
		this.context = context;
		this.id = allocator.popVAO(this);
		
		gl.glBindVertexArray(id);
		
		setupData(context, allocator);
		
		gl.glBindVertexArray(0);
	}
	
	protected void setupFloatAttribute(int attribute, int count, int stride, int disp) {
		setupAttribute(attribute, count, stride, disp, GL.GL_FLOAT, Float.BYTES);
	}
	
	protected void setupAttribute(int attribute, int count, int stride, int disp, int dataType, int dataTypeSize) {
		ensureInitialized();
		gl.glEnableVertexAttribArray(attribute);
		gl.glVertexAttribPointer(attribute, count, dataType, false, stride * dataTypeSize, disp * dataTypeSize);
	}
	
	protected void setupFloatBuffer(int type, int id, Buffer buffer, int drawMode) {
		setupBuffer(type, id, buffer, drawMode, Float.BYTES);
	}
	
	protected void setupBuffer(int type, int id, Buffer buffer, int drawMode, int dataTypeSize) {
		ensureInitialized();
		gl.glBindBuffer(type, id);
		gl.glBufferData(type, buffer.capacity() * dataTypeSize, buffer.flip(), drawMode);
	}
	
	protected void updateBuffer(int type, int id, Buffer buffer, int offset, int dataTypeSize) {
		ensureInitialized();
		gl.glBindBuffer(type, id);
		gl.glBufferSubData(type, offset, buffer.capacity() * dataTypeSize, buffer.flip());
	}

	private void ensureInitialized() {
		if(id < 0) {
			throw new IllegalStateException();
		}
	}
	
	private void ensureNotInitialized() {
		if(id >= 0) {
			throw new IllegalStateException();
		}
	}

	protected abstract void setupData(C context, StaticAllocator allocator);
	
	protected abstract void update(GL4 gl, C context, S settings);
	protected abstract void renderObject(GL4 gl);
	
	protected abstract int getBufferCount();
}
