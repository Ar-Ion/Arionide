package ch.innovazion.arionide.ui.core.gl;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.jogamp.opengl.GL4;

public class StaticAllocator {
	
	private final IntBuffer VAOs;
	private final IntBuffer VBOs;
	
	private final int[] VAOAccessTable;
	private final int[] VBOAccessTable;
	
	private final Map<RenderableObject<?, ?>, Integer> IDs = new HashMap<>();
	
	public StaticAllocator(RenderableObject<?, ?>... objects) {
		this.VAOs = IntBuffer.allocate(objects.length);
		this.VBOs = IntBuffer.allocate(Stream.of(objects).mapToInt(RenderableObject::getBufferCount).sum());
		
		this.VAOAccessTable = new int[objects.length];
		this.VBOAccessTable = new int[objects.length];

		for(int i = 0; i < objects.length; i++) {
			IDs.put(objects[i], i);
		}
	}
	
	public void generate(GL4 gl) {
		gl.glGenVertexArrays(VAOs.limit(), VAOs);
		gl.glGenBuffers(VBOs.limit(), VBOs);
	}
	
	public int popVAO(RenderableObject<?, ?> object) {
		securityCheck(VAOAccessTable, object);
		return VAOs.get();
	}
	
	public int popVBO(RenderableObject<?, ?> object) {
		securityCheck(VBOAccessTable, object);
		return VBOs.get();
	}
	
	private void securityCheck(int[] table, RenderableObject<?, ?> object) {				
		if(++table[IDs.get(object)] > object.getBufferCount()) {
			throw new SecurityException("Attempt to access a protected memory location");
		}
	}
}