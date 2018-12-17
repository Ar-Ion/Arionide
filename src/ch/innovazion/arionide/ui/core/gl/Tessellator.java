package ch.innovazion.arionide.ui.core.gl;

import java.nio.Buffer;

import com.jogamp.opengl.GL4;

public abstract class Tessellator implements BufferGenerator {
	public Buffer generate() {
		return tessellate();
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
	
	public abstract Buffer tessellate();
}
