package ch.innovazion.arionide.ui.render.gl;

import com.jogamp.opengl.GL4;

public class GLCircleContext extends GLShapeContext {

	protected GLCircleContext(GL4 gl, GLEdgeContext parent) {
		super(gl);

		gl.glUniform1i(parent.getTextureID(), 0);
	}

	public String getVertexShader() {
		return "textured.vert";
	}

	public String getFragmentShader() {
		return "edge.frag";
	}
}