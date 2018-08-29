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
package org.azentreprise.arionide.ui.render.gl.vao;

import java.nio.IntBuffer;

import org.azentreprise.arionide.ui.gc.Trash;
import org.azentreprise.arionide.ui.gc.Trashable;
import org.azentreprise.arionide.ui.gc.UnusedVAO;

import com.jogamp.opengl.GL4;

public class VertexArray {
	
	private VertexBuffer[] buffers;
	private int[] bufferIDs;

	private boolean loaded = false;
	private int id;
	private Trashable trashable;
	
	public VertexArray(VertexBuffer... buffers) {
		this.setBuffers(buffers);
	}
	
	public void setBuffers(VertexBuffer... buffers) {
		this.unload();

		this.buffers = buffers;
		this.bufferIDs = new int[buffers.length];
	}
	
	public void addBuffers(VertexBuffer... more) {		
		this.unload();

		int newLength = this.buffers.length + more.length;
		
		VertexBuffer[] buffersRealloc = new VertexBuffer[newLength];
		
		System.arraycopy(this.buffers, 0, buffersRealloc, 0, this.buffers.length);
		System.arraycopy(more, 0, buffersRealloc, this.buffers.length, more.length);

		this.buffers = buffersRealloc;
		this.bufferIDs = new int[newLength];
	}
	
	public void load(GL4 gl) {		
		IntBuffer idBuffer = IntBuffer.allocate(1);
		gl.glGenVertexArrays(1, idBuffer);
		
		gl.glBindVertexArray(this.id = idBuffer.get(0));
		
		for(int i = 0; i < this.bufferIDs.length; i++) {
			VertexBuffer buffer = this.buffers[i];
			
			if(!buffer.isLoaded()) {
				buffer.load(gl);
			} else {
				gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffer.getID());
				buffer.loadAttributes(gl);
			}
			
			this.bufferIDs[i] = buffer.getID();
		}
		
		this.trashable = new UnusedVAO(this.id, this.bufferIDs);
				
		this.loaded = true;
	}
	
	public void unload() {
		if(this.isLoaded()) {
			this.loaded = false;
			
			for(VertexBuffer buffer : this.buffers) {
				buffer.unload();
			}
			
			Trash.instance().throwAway(this.trashable);
		}
	}
	
	public boolean isLoaded() {
		return this.loaded;
	}
	
	private void checkLoaded() {
		if(!this.loaded) {
			throw new IllegalStateException("VAO not loaded");
		}
	}
	
	public void sync(VertexArray other) {
		assert this.bufferIDs.length == other.bufferIDs.length;
		
		this.id = other.getID();
		System.arraycopy(other.bufferIDs, 0, this.bufferIDs, 0, this.bufferIDs.length);
		this.trashable = other.trashable;
		
		this.loaded = true;
	}
	
	public int getID() {
		this.checkLoaded();
		return this.id;
	}
	
	public int[] getBuffers() {
		this.checkLoaded();
		return this.bufferIDs;
	}
	
	public void bind(GL4 gl) {
		this.checkLoaded();
		gl.glBindVertexArray(this.id);
	}
	
	protected void finalize() {
		Trash.instance().throwAway(this.trashable);
	}
	
	public int hashCode() {
		return this.id;
	}
	
	public boolean equals(Object other) {
		return other instanceof VertexArray && ((VertexArray) other).id == this.id;
	}
}