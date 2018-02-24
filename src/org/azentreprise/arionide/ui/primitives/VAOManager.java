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
package org.azentreprise.arionide.ui.primitives;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.jogamp.opengl.GL4;

public class VAOManager {
	
	private final Map<Long, Integer> VAOs = new HashMap<>();
	private final GL4 gl;
	private final int shaderProgram;
	
	public VAOManager(GL4 gl, int shaderProgram) {
		this.gl = gl;
		this.shaderProgram = shaderProgram;
	}
	
	public void loadVAO(long uuid, Supplier<DoubleBuffer> bufferAllocator, BiConsumer<String, Integer> attributeLoader, String... attributes) {
		if(this.VAOs.containsKey(uuid)) {
			this.gl.glBindVertexArray(this.VAOs.get(uuid));
		} else {
			this.createVAO(uuid, bufferAllocator, attributeLoader, attributes);
		}
	}
		
	private void createVAO(long uuid, Supplier<DoubleBuffer> bufferAllocator, BiConsumer<String, Integer> attributeLoader, String... attributes) {		
		IntBuffer vao = IntBuffer.allocate(1);
		IntBuffer vbo = IntBuffer.allocate(1);
		
		
		this.gl.glGenVertexArrays(1, vao);
		
		int vaoID = vao.get(0);
		this.VAOs.put(uuid, vaoID);
		
		this.gl.glBindVertexArray(vaoID);
		
		DoubleBuffer data = bufferAllocator.get();
				
		this.gl.glGenBuffers(1, vbo);
		this.gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.get(0));
		this.gl.glBufferData(GL4.GL_ARRAY_BUFFER, data.capacity() * Double.BYTES, data, GL4.GL_STATIC_DRAW);
		
		for(String attribute : attributes) {
			int id = this.gl.glGetAttribLocation(this.shaderProgram, attribute);
			this.gl.glEnableVertexAttribArray(id);
			attributeLoader.accept(attribute, id);
		}
	}
}
