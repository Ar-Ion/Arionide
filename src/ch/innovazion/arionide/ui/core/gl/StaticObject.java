package ch.innovazion.arionide.ui.core.gl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jogamp.opengl.GL4;

public abstract class StaticObject<C extends Context, S extends Settings> extends RenderableObject<C, S> {
	
	private final Map<BufferGenerator, Consumer<C>> initializers = new HashMap<>();
	private final Map<Integer, List<Integer>> VBOs = new HashMap<>();
	
	protected StaticObject(S settings) {
		super(settings);
	}
	
	protected void setBufferInitializer(BufferGenerator generator, Consumer<C> initializer) {
		initializers.put(generator, initializer);
	}

	protected void setupData(C context, StaticAllocator allocator) {
		for(BufferGenerator generator : getGenerators()) {
			int vbo = allocator.popVBO(this);
			setupBuffer(generator.getBufferType(), vbo, generator.generate(), GL4.GL_STATIC_DRAW, generator.getDataTypeSize());
			
			List<Integer> vbosByType = VBOs.get(generator.getBufferType());

			if(vbosByType == null) {
				vbosByType = new ArrayList<>();
				VBOs.put(generator.getBufferType(), vbosByType);
			}
			
			vbosByType.add(vbo);
			
			Consumer<C> initializer = initializers.get(generator);
			
			if(initializer != null) {
				initializer.accept(context);
			}
		}
	}
	
	protected int fetchVBO(int type, int index) {
		return VBOs.getOrDefault(type, Collections.emptyList()).stream().filter(Integer.valueOf(index)::equals).findAny().orElse(-1);
	}
		
	protected abstract List<BufferGenerator> getGenerators();
}