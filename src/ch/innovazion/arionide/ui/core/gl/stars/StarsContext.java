package ch.innovazion.arionide.ui.core.gl.stars;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.Context;

public class StarsContext implements Context {

	private final int positionAttribute;
	private final int colorAttribute;
	
	public StarsContext(GL4 gl, int shader) {
		this.positionAttribute = gl.glGetAttribLocation(shader, "position");
		this.colorAttribute = gl.glGetAttribLocation(shader, "color");
	}
	
	protected int getPositionAttribute() {
		return positionAttribute;
	}
	
	protected int getColorAttribute() {
		return colorAttribute;
	}
}
