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
