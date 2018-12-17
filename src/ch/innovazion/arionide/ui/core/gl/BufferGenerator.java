package ch.innovazion.arionide.ui.core.gl;

import java.nio.Buffer;

public interface BufferGenerator {
	public Buffer generate();
	public int getBufferType();
	public int getDataType();
	public int getDataTypeSize();
}
