package ch.innovazion.arionide.ui.core.gl;

import java.nio.Buffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

public abstract class RenderableObject<C extends Context, S extends Settings> implements Renderable {
	
	private final S settings;
	
	private GL4 gl;
	private C context;
	private int id = -1;
	
	protected RenderableObject(S settings) {
		this.settings = settings;
	}
	
	public S getSettings() {
		return this.settings;
	}
	
	public void bind() {
		ensureInitialized();
		gl.glBindVertexArray(id);
	}
	
	public void render() {
		ensureInitialized();
		update(gl, context, settings);
		renderObject(gl);
	}
	
	public void init(GL4 gl, C context, StaticAllocator allocator) {
		ensureNotInitialized();
		
		this.gl = gl;
		this.context = context;
		this.id = allocator.popVAO(this);
		
		gl.glBindVertexArray(id);
		
		setupData(context, allocator);
		
		gl.glBindVertexArray(0);
	}
	
	protected void setupFloatAttribute(int attribute, int count, int stride, int disp) {
		setupAttribute(attribute, count, stride, disp, GL.GL_FLOAT, Float.BYTES);
	}
	
	protected void setupAttribute(int attribute, int count, int stride, int disp, int dataType, int dataTypeSize) {
		ensureInitialized();
		gl.glEnableVertexAttribArray(attribute);
		gl.glVertexAttribPointer(attribute, count, dataType, false, stride * dataTypeSize, disp * dataTypeSize);
	}
	
	protected void setupFloatBuffer(int type, int id, Buffer buffer, int drawMode) {
		setupBuffer(type, id, buffer, drawMode, Float.BYTES);
	}
	
	protected void setupBuffer(int type, int id, Buffer buffer, int drawMode, int dataTypeSize) {
		ensureInitialized();
		gl.glBindBuffer(type, id);
		gl.glBufferData(type, buffer.capacity() * dataTypeSize, buffer.flip(), drawMode);
	}

	private void ensureInitialized() {
		if(id < 0) {
			throw new IllegalStateException();
		}
	}
	
	private void ensureNotInitialized() {
		if(id >= 0) {
			throw new IllegalStateException();
		}
	}

	protected abstract void setupData(C context, StaticAllocator allocator);
	
	protected abstract void update(GL4 gl, C context, S settings);
	protected abstract void renderObject(GL4 gl);
	
	protected abstract int getBufferCount();
}
