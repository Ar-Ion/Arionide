package ch.innovazion.arionide.ui.core.gl.structures;

import java.nio.Buffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.BufferGenerator;

public class StructureIndicesGenerator implements BufferGenerator {
	
	private final int layers;
	
	protected StructureIndicesGenerator(int layers) {
		this.layers = layers;
	}
	
	public Buffer generate() {
		IntBuffer indices = IntBuffer.allocate(2*layers * layers * 2);
		
		for(int latitudeID = 0; latitudeID < layers; latitudeID++) {
			for(int longitudeID = 0; longitudeID < 2*layers; longitudeID++) {
				indices.put(latitudeID * 2*layers + longitudeID);
				indices.put((latitudeID + 1) * 2*layers + longitudeID);
			}
		}
				
		return indices;
	}
	
	public int getBufferType() {
		return GL4.GL_ELEMENT_ARRAY_BUFFER;
	}
	
	public int getDataType() {
		return GL4.GL_UNSIGNED_INT;
	}
	
	public int getDataTypeSize() {
		return Integer.BYTES;
	}
}