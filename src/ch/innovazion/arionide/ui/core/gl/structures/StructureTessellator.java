package ch.innovazion.arionide.ui.core.gl.structures;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import ch.innovazion.arionide.ui.core.gl.Tessellator;

public class StructureTessellator extends Tessellator {
	
	private final int layers;
	
	protected StructureTessellator(int layers) {
		this.layers = layers;
	}
	
	public Buffer tessellate() {
		FloatBuffer sphere = FloatBuffer.allocate(2*layers * layers * 3);
		
		for(double theta = 0.0d; theta < Math.PI + 10E-4; theta += Math.PI / (layers - 1)) {
			for(double phi = 0.0d; phi < 2.0d * Math.PI - 10E-4; phi += Math.PI / layers) {
				sphere.put((float) (Math.sin(theta) * Math.cos(phi)));
				sphere.put((float) Math.cos(theta));
				sphere.put((float) (Math.sin(theta) * Math.sin(phi)));
			}
		}
		
		return sphere;
	}
}