package ch.innovazion.arionide.ui.core.gl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jogamp.opengl.GL4;

public abstract class StaticObject<C extends Context, S extends Settings> extends RenderableObject<C, S> {
	
	private final Map<BufferGenerator, Consumer<C>> initializers = new HashMap<>();
	
	protected StaticObject(S settings) {
		super(settings);
	}
	
	protected void setBufferInitializer(BufferGenerator generator, Consumer<C> initializer) {
		initializers.put(generator, initializer);
	}

	protected void setupData(C context, StaticAllocator allocator) {		
		for(BufferGenerator generator : getGenerators()) {
			setupBuffer(generator.getBufferType(), allocator.popVBO(this), generator.generate(), GL4.GL_STATIC_DRAW, generator.getDataTypeSize());
			
			Consumer<C> initializer = initializers.get(generator);
						
			if(initializer != null) {
				initializer.accept(context);
			}
		}
	}

	protected abstract List<BufferGenerator> getGenerators();
}