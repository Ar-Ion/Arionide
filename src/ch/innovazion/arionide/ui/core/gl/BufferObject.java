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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public abstract class BufferObject<C extends Context, S extends Settings> extends RenderableObject<C, S> {
	
	private final Map<BufferGenerator, Integer> vbos = new HashMap<>();
	private final Map<BufferGenerator, Consumer<C>> initializers = new HashMap<>();
	private final Map<String, BufferGenerator> labels = new HashMap<>();
	
	protected BufferObject(S settings) {
		super(settings);
	}
	
	protected void setBufferInitializer(BufferGenerator generator, Consumer<C> initializer) {
		initializers.put(generator, initializer);
	}
	
	protected void setBufferLabel(BufferGenerator generator, String label) {
		labels.put(label, generator);
	}

	protected void setupData(C context, StaticAllocator allocator) {		
		for(BufferGenerator generator : getGenerators()) {
			int vbo = allocator.popVBO(this);
					
			setupBuffer(generator.getBufferType(), vbo, generator.generate(), generator.getDrawMode(), generator.getDataTypeSize());
			
			vbos.put(generator, vbo);
			
			Consumer<C> initializer = initializers.get(generator);

			if(initializer != null) {
				initializer.accept(context);
			}
		}
	}
	
	public void updateData(String label, Buffer data) {
		this.updateDataSegment(label, 0, data);
	}
	
	public void updateDataSegment(String label, int offset, Buffer data) {
		BufferGenerator generator = fetchGenerator(label);
		Integer vbo = vbos.get(generator);
			
		assert vbo != null;
			
		updateBuffer(generator.getBufferType(), vbo, data, offset, generator.getDataTypeSize());
	}
	
	private BufferGenerator fetchGenerator(String label) {
		BufferGenerator generator = labels.get(label);
		
		if(generator != null) {
			return generator;
		} else {
			throw new NoSuchElementException();
		}
	}

	protected abstract List<BufferGenerator> getGenerators();
}