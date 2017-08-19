package org.azentreprise.arionide.ui.overlay.components;

import java.nio.IntBuffer;

import org.azentreprise.arionide.ui.OpenGLDrawingContext;

import com.jogamp.opengl.GL4;

public class OpenGLTextMetricsTransform {
	protected static double[] getScalarsAndDisplacements(OpenGLDrawingContext context) {
		IntBuffer viewport = IntBuffer.allocate(4);
		context.getRenderer().glGetIntegerv(GL4.GL_VIEWPORT, viewport);
		return new double[] {2.0d / viewport.get(2), 2.0d / viewport.get(3), 1.0d, 0.0d};
	}
}