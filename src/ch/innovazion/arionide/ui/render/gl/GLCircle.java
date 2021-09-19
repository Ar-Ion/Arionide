package ch.innovazion.arionide.ui.render.gl;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.render.PrimitiveType;
import ch.innovazion.arionide.ui.render.gl.cache.Attribute;
import ch.innovazion.arionide.ui.render.gl.cache.VertexArray;
import ch.innovazion.arionide.ui.render.gl.cache.VertexBuffer;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;

public class GLCircle extends GLShape {

	private final VertexBuffer positionBuffer = new VertexBuffer(Float.BYTES, new Attribute(GLRenderingContext.cursor.getPositionAttribute(), 2, GL4.GL_FLOAT, 0));
	private final VertexArray vao = new VertexArray(this.positionBuffer);
	
	private float radius;
	private Point center = new Point();
	
	protected GLCircle(int rgb, int alpha) {
		super(rgb, alpha);
	}

	public void updateBounds(Bounds bounds) {
		
	}

	protected void prepareGL() {
		
	}

	protected GLShapeContext getContext() {
		return GLRenderingContext.circle;
	}

	protected PrimitiveType getType() {
		return PrimitiveType.CIRCLE;
	}

	protected void render() {

	}
}