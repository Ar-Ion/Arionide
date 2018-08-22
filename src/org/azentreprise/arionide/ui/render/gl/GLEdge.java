/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.render.gl;

import java.math.BigInteger;
import java.nio.FloatBuffer;

import org.azentreprise.arionide.ui.Viewport;
import org.azentreprise.arionide.ui.render.GLBounds;
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.RenderingContext;
import org.azentreprise.arionide.ui.render.gl.vao.Attribute;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArray;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArrayCache;
import org.azentreprise.arionide.ui.render.gl.vao.VertexBuffer;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Size;

import com.jogamp.opengl.GL4;

public class GLEdge extends GLRectangle {
	
	private static GLEdgeRenderingContext context;
	
	private final VertexBuffer positionBuffer;
	private final VertexBuffer edgeFactorBuffer;
	private final VertexArray vao;
	
	private int edgeRadius;
	
	private float radiusX;
	private float radiusY;
	
	public GLEdge(Bounds bounds, int rgb, int alpha, int edgeRadius) {
		super(bounds, rgb, alpha);
		
		assert context != null;
		
		this.edgeRadius = edgeRadius;
		
		this.positionBuffer = new VertexBuffer(Float.BYTES, new Attribute(context.getPositionAttribute(), 2, GL4.GL_FLOAT, 1));
		this.edgeFactorBuffer = new VertexBuffer(Float.BYTES, new Attribute(context.getEdgeFactorAttribute(), 2, GL4.GL_FLOAT));
		this.vao = new VertexArray(this.positionBuffer, this.edgeFactorBuffer);
		
		this.edgeFactorBuffer.updateDataSupplier(() -> FloatBuffer.allocate(8).put(0).put(-1)
																			  .put(0).put(0)
																			  .put(1).put(-1)
																			  .put(1).put(0).flip());
	}
	
	public void load() {
		if(this.bounds != null) {
			VertexArrayCache.load(this.bounds, context.getGL(), this.vao);
		}
	}
	
	public void updateBounds(Bounds newBounds) {
		if(newBounds != null) {
			this.bounds = new GLBounds(newBounds);
			this.positionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(8).putBoundingPoints().getDataBuffer().flip());
			this.vao.unload(); // Invalidate VAO
		}
	}
	
	public BigInteger getFingerprint() {
		return Identification.generateFingerprint(super.getFingerprint(),
				Float.floatToIntBits(this.radiusX) ^ Float.floatToIntBits(this.radiusY));
	}
	
	public PrimitiveType getType() {
		return PrimitiveType.EDGE;
	}
	
	public void updateProperty(int identifier) {
		super.updateProperty(identifier);
						
		switch(identifier) {
			case GLEdgeRenderingContext.EDGE_RADIUS_IDENTIFIER:
				context.getGL().glUniform2f(context.getEdgeRadiusUniform(), this.radiusX, this.radiusY);
				break;
		}
	}

	private void updateRadius(GL4 gl) {
		Size pixelSize = Viewport.glGetPixelSize(context.getGL());
		
		this.radiusX = this.edgeRadius * pixelSize.getWidth();
		this.radiusY = this.edgeRadius * pixelSize.getHeight();
	}
	
	public void render() {
		GL4 gl = context.getGL();
		
		this.updateRadius(gl);
		
		this.vao.bind(gl);
		gl.glDrawArraysInstanced(GL4.GL_TRIANGLE_STRIP, 0, 4, 4);
	}
	
	public static RenderingContext setupContext(GLEdgeRenderingContext context) {
		return GLEdge.context = context;
	}
}
