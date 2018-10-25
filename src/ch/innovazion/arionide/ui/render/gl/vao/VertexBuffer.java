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
package ch.innovazion.arionide.ui.render.gl.vao;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;

import com.jogamp.opengl.GL4;

public class VertexBuffer {
	
	private final int bufferType;
	private final int primitiveSize;
	private final int drawMode;
	private final Attribute[] attributes;
	private final int components;
		
	private Supplier<Buffer> bufferSupplier;

	private boolean loaded = false;
	private int id;
	
	public VertexBuffer(int primitiveSize, Attribute... attributes) {
		this(GL4.GL_ARRAY_BUFFER, primitiveSize, GL4.GL_STATIC_DRAW, attributes);
	}
	
	public VertexBuffer(int bufferType, int primitiveSize, Attribute... attributes) {
		this(bufferType, primitiveSize, GL4.GL_STATIC_DRAW, attributes);
	}
	
	public VertexBuffer(int bufferType, int primitiveSize, int drawMode, Attribute... attributes) {
		this.bufferType = bufferType;
		this.primitiveSize = primitiveSize;
		this.attributes = attributes;
		this.drawMode = drawMode;
		
		int sum = 0;
		
		for(Attribute attribute : attributes) {
			sum += attribute.getSize();
		}
		
		this.components = sum;
	}
	
	public void load(GL4 gl) {
		if(this.bufferSupplier != null) {
			IntBuffer idBuffer = IntBuffer.allocate(1);
			gl.glGenBuffers(1, idBuffer);
			
			Buffer buffer = this.bufferSupplier.get();
			
			gl.glBindBuffer(this.bufferType, this.id = idBuffer.get(0));
			gl.glBufferData(this.bufferType, buffer.limit() * this.primitiveSize, buffer, this.drawMode);
			
			this.loadAttributes(gl);
			
			this.loaded = true;
		} else {
			throw new IllegalStateException("Unable to load a vertex buffer without any data supplier");
		}
	}
	
	protected void loadAttributes(GL4 gl) {
		int index = 0;
		
		for(Attribute attribute : this.attributes) {
			attribute.load(gl, this.components - attribute.getSize(), index);
			index += attribute.getSize();
		}
	}
	
	public void unload() {
		this.loaded = false;
	}
	
	public boolean isLoaded() {
		return this.loaded;
	}
	
	private void checkLoaded() {
		if(!this.loaded) {
			throw new IllegalStateException("VBO not loaded");
		}
	}
	
	public VertexBuffer updateDataSupplier(Supplier<Buffer> supplier) {
		this.bufferSupplier = supplier;
		return this;
	}
	
	public int getID() {
		this.checkLoaded();
		return this.id;
	}
	
	public int hashCode() {
		return this.id;
	}
	
	public boolean equals(Object other) {
		return other instanceof VertexBuffer && ((VertexBuffer) other).id == this.id;
	}
}