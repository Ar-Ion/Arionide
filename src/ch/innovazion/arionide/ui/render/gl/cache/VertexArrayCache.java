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
package ch.innovazion.arionide.ui.render.gl.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jogamp.opengl.GL4;

public class VertexArrayCache {
	
	private static final int CACHE_CAPACITY = 2048;
	
	private static final Map<Object, VertexArray> cache = new LinkedHashMap<Object, VertexArray>(CACHE_CAPACITY, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Map.Entry<Object, VertexArray> eldest) {
			return this.size() > CACHE_CAPACITY;
		}
	};

	public static void load(Object identifier, GL4 gl, VertexArray vao) {
		if(!vao.isLoaded()) {
			VertexArray cacheEntry = cache.get(identifier);
			
			if(cacheEntry != null && !vao.equals(cacheEntry)) {
				if(!cacheEntry.isLoaded()) {
					cacheEntry.load(gl);
				}
				
				vao.sync(cacheEntry);
			} else {
				vao.load(gl);
				cache.put(identifier, vao);
			}
		}
	}
}