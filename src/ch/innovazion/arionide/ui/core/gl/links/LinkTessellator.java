package ch.innovazion.arionide.ui.core.gl.links;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import ch.innovazion.arionide.ui.core.gl.Tessellator;

public class LinkTessellator extends Tessellator {
	
	private static final double stringConstant = 0.5d / Math.log1p(Math.sqrt(2));
	private static final double modelHeight = Math.cosh(1.0d / stringConstant) * stringConstant;
	
	private final int layers;
	
	protected LinkTessellator(int layers) {
		this.layers = layers;
	}
	
	public Buffer tessellate() {
		FloatBuffer sphere = FloatBuffer.allocate(2*layers * layers * 3 );
		
		for(double phi = 0.0d; phi < 2.0d * Math.PI - 10E-4; phi += Math.PI / layers) {
			for(double z = -1.0d; z < 1.0d + 10E-4; z += 2.0d / (layers - 1)) {
				double height = Math.cosh(z / stringConstant) * stringConstant;
				
				sphere.put((float) z);
				sphere.put((float) (Math.cos(phi) * height));
				sphere.put((float) (Math.sin(phi) * height));
			}
		}
		
		return sphere;
	}
	
	public static double getModelScaleFactor(double targetHeight) {
		return targetHeight / modelHeight;
	}
}