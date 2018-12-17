package ch.innovazion.arionide.ui.core.gl.stars;

import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.BufferGenerator;
import ch.innovazion.arionide.ui.core.gl.StaticObject;

public class Stars extends StaticObject<StarsContext, StarsSettings> {

	private final int count;
	private final BufferGenerator generator;
	
	public Stars(int count, float size) {
		super(new StarsSettings(size));
		
		this.count = count;
		this.generator = new StarsGenerator(count);
		
		this.setBufferInitializer(this.generator, this::setupAttributes);
	}
	
	private void setupAttributes(StarsContext context) {
		setupFloatAttribute(context.getPositionAttribute(), 3, 6, 0);
		setupFloatAttribute(context.getColorAttribute(), 3, 6, 3);
	}

	protected void update(GL4 gl, StarsContext context, StarsSettings settings) {
		gl.glPointSize(settings.getSize());
	}

	protected void renderObject(GL4 gl) {
		gl.glDrawArrays(GL4.GL_POINTS, 0, count);
	}

	protected List<BufferGenerator> getGenerators() {
		return Arrays.asList(generator);
	}
	
	protected int getBufferCount() {
		return 1;
	}
}