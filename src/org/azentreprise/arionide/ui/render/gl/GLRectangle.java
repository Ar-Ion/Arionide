package org.azentreprise.arionide.ui.render.gl;

import org.azentreprise.arionide.ui.render.gl.vao.VertexBuffer;

import com.jogamp.opengl.GL4;

public class GLRectangle extends GLPolygon {

	public GLRectangle(int rgb, int alpha) {
		super(rgb, alpha);
	}

	protected void updateBuffers(VertexBuffer mainPositionBuffer) {
		mainPositionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(8).putBoundingPoints().getDataBuffer().flip());
	}

	public void renderPolygon() {
		this.getContext().getGL().glDrawArrays(GL4.GL_LINE_LOOP, 0, 4);
	}
}
