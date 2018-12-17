package ch.innovazion.arionide.ui.core.gl.fx;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.Context;

public class FBOFrameContext implements Context {
	
	private final int positionAttribute;
	
	public FBOFrameContext(GL4 gl, int shader) {
		this.positionAttribute = gl.glGetAttribLocation(shader, "position");
	}
	
	protected int getPositionAttribute() {
		return positionAttribute;
	}
}
