package ch.innovazion.arionide.ui.core.gl.links;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import ch.innovazion.arionide.ui.core.gl.Tessellator;

public class LinkTessellator extends Tessellator {
	
	private final int layers;
	
	protected LinkTessellator(int layers) {
		this.layers = layers;
	}
	
	public Buffer tessellate() {
		FloatBuffer sphere = FloatBuffer.allocate(2*layers * layers * 3);
		
		for(double z = -0.5d; z < 0.5d + 10E-4; z += 1.0d / (layers - 1)) {
			for(double phi = 0.0d; phi < 2.0d * Math.PI - 10E-4; phi += Math.PI / layers) {
				sphere.put((float) z);
				sphere.put((float) (Math.cos(phi) * Math.cosh(4*z) * 0.125));
				sphere.put((float) (Math.sin(phi) * Math.cosh(4*z) * 0.125));
			}
		}
		
		return sphere;
	}
}