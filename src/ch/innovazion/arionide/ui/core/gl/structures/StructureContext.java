package ch.innovazion.arionide.ui.core.gl.structures;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.Context;

public class StructureContext implements Context {
	
	private final int positionAttribute;
	
	public StructureContext(GL4 gl, int shader) {
		this.positionAttribute = gl.glGetAttribLocation(shader, "position");
	}
	
	protected int getPositionAttribute() {
		return positionAttribute;
	}
}
