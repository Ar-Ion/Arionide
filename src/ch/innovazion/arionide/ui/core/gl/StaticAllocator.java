/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.jogamp.opengl.GL4;

public class StaticAllocator {
	
	private final IntBuffer VAOs;
	private final IntBuffer VBOs;
	
	private final int[] VAOAccessTable;
	private final int[] VBOAccessTable;
	
	private final Map<RenderableObject<?, ?>, Integer> IDs = new HashMap<>();
	
	public StaticAllocator(RenderableObject<?, ?>... objects) {
		this.VAOs = IntBuffer.allocate(objects.length);
		this.VBOs = IntBuffer.allocate(Stream.of(objects).mapToInt(RenderableObject::getBufferCount).sum());
		
		this.VAOAccessTable = new int[objects.length];
		this.VBOAccessTable = new int[objects.length];

		for(int i = 0; i < objects.length; i++) {
			IDs.put(objects[i], i);
		}
	}
	
	public void generate(GL4 gl) {
		gl.glGenVertexArrays(VAOs.limit(), VAOs);
		gl.glGenBuffers(VBOs.limit(), VBOs);
	}
	
	public int popVAO(RenderableObject<?, ?> object) {
		securityCheck(VAOAccessTable, object);
		return VAOs.get();
	}
	
	public int popVBO(RenderableObject<?, ?> object) {
		securityCheck(VBOAccessTable, object);
		return VBOs.get();
	}
	
	private void securityCheck(int[] table, RenderableObject<?, ?> object) {				
		if(++table[IDs.get(object)] > object.getBufferCount()) {
			throw new SecurityException("Attempt to access a protected memory location");
		}
	}
}