package ch.innovazion.arionide.ui.core.gl.fx;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.RenderableObject;
import ch.innovazion.arionide.ui.core.gl.StaticAllocator;

public class FBOFrame extends RenderableObject<FBOFrameContext, FBOFrameSettings> {

	private final FloatBuffer buffer = FloatBuffer.wrap(new float[] { -1.0f,  1.0f, 
																 	   1.0f,  1.0f, 
																      -1.0f, -1.0f, 
																	   1.0f, -1.0f });
	
	protected FBOFrame() {
		super(new FBOFrameSettings());
	}

	protected void setupData(FBOFrameContext context, StaticAllocator allocator) {
		setupFloatBuffer(GL4.GL_ARRAY_BUFFER, allocator.popVBO(this), buffer, GL4.GL_STATIC_DRAW);
		setupFloatAttribute(context.getPositionAttribute(), 2, 0, 0);
	}

	protected void update(GL4 gl, FBOFrameContext context, FBOFrameSettings settings) {
		
	}

	protected void renderObject(GL4 gl) {
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}

	protected int getBufferCount() {
		return 1;
	}
}