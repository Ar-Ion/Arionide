package ch.innovazion.arionide.ui.render.gl;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.render.gl.cache.VertexBuffer;

public class GLSolid extends GLPolygon {
	public GLSolid(int rgb, int alpha) {
		super(rgb, alpha);
	}
	
	protected void updateBuffers(VertexBuffer mainPositionBuffer) {
		mainPositionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(8).putNorth().putSouth().getDataBuffer().flip());
	}

	public void renderPolygon() {
		getContext().getGL().glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}
}
