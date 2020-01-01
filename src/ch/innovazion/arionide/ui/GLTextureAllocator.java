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
package ch.innovazion.arionide.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

public class GLTextureAllocator {
	
	private static final GLTextureAllocator singleton = new GLTextureAllocator();
	
	private final Map<String, Integer> labelledTextures = new HashMap<>();
	private final Stack<Integer> freed = new Stack<>();
	private int id = 0;
	
	private GLTextureAllocator() {
		throw new IllegalStateException();
	}
	
	public int allocate() {
		return pop().orElse(GL.GL_TEXTURE0 + internalAlloc());
	}
	
	/*
	 * Also acts as a reallocate.
	 */
	public int allocate(String label) {
		int texture = allocate();
		free(labelledTextures.put(label, texture));
		return texture;
	}

	public void free(Integer unit) {
		if(unit != null) {
			freed.push(unit);
			labelledTextures.values().removeIf(unit::equals);
		}
	}
	
	public void free(String label) {
		free(labelledTextures.get(label));
	}
	
	public int retrieve(String label) {
		Integer texture = labelledTextures.get(label);
		
		if(texture != null) {
			return texture;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	private Optional<Integer> pop() {
		if(freed.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(freed.pop());
		}
	}
	
	private int internalAlloc() {
		if(++id >= GL4.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS) {
			throw new OutOfMemoryError("GPU texture unit limit exceeded");
		}
		
		return id;
	}
	
	public static GLTextureAllocator instance() {
		return singleton;
	}
}