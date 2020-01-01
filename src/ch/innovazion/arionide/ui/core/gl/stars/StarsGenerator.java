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
package ch.innovazion.arionide.ui.core.gl.stars;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Random;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.BufferGenerator;

public class StarsGenerator implements BufferGenerator {

	private final int count;
	
	public StarsGenerator(int count) {
		this.count = count;
	}
	
	public Buffer generate() {
		FloatBuffer data = FloatBuffer.allocate(6 * count);
		Random random = new Random();
		
		for(int i = 0; i < count; i++) {
			float x = random.nextFloat() - 0.5f;
			float y = random.nextFloat() - 0.5f;
			float z = random.nextFloat() - 0.5f;
			
			float factor = 1.0f / (float) Math.sqrt(x * x + y * y + z * z);
			
			data.put(x * factor);
			data.put(y * factor);
			data.put(z * factor);
			
			float brightness = random.nextFloat();
			float red = (1 - brightness) * brightness * random.nextFloat();
			float blue = (1 - brightness) * brightness * random.nextFloat();
			
			data.put(brightness + red);
			data.put(brightness + 0);
			data.put(brightness + blue);
		}
				
		return data;
	}
	
	public int getBufferType() {
		return GL4.GL_ARRAY_BUFFER;
	}
	
	public int getDataType() {
		return GL4.GL_FLOAT;
	}
	
	public int getDataTypeSize() {
		return Float.BYTES;
	}
	
	public int getDrawMode() {
		return GL4.GL_STATIC_DRAW;
	}
}