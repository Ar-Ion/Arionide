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
}