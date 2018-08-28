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
import org.azentreprise.arionide.ui.render.gl.vao.Attribute;
import org.azentreprise.arionide.ui.render.gl.vao.UID;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArray;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArrayCache;
import org.azentreprise.arionide.ui.render.gl.vao.VertexBuffer;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;

import com.jogamp.opengl.GL4;

public class GLEdge extends GLRectangle {
	
	private final VertexBuffer positionBuffer = new VertexBuffer(Float.BYTES, new Attribute(this.getContext().getPositionAttribute(), 2, GL4.GL_FLOAT, 1));
	private final VertexBuffer edgeFactorBuffer = new VertexBuffer(Float.BYTES, new Attribute(this.getContext().getEdgeFactorAttribute(), 2, GL4.GL_FLOAT));
	private final VertexArray vao = new VertexArray(this.positionBuffer, this.edgeFactorBuffer);
		
	private float edgeRadius;
	
	private Point radius = new Point();
	
	public GLEdge(int rgb, int alpha, float edgeRadius) {
		super(rgb, alpha);
		
		this.edgeRadius = edgeRadius;
		
		float f = 1.0f + 0.625f / edgeRadius;
		
		this.edgeFactorBuffer.updateDataSupplier(() -> FloatBuffer.allocate(8).put(0).put(-f)
																			  .put(0).put(0)
																			  .put(1).put(-1)
																			  .put(f).put(0).flip());
	}
	
	protected void prepareGL() {
		if(this.bounds != null) {
			VertexArrayCache.load(new UID(this.bounds, PrimitiveType.EDGE), this.getContext().getGL(), this.vao);
		}
	}
	
	public void updateBounds(Bounds newBounds) {
		if(newBounds != null) {
			newBounds = newBounds.copy();

			this.bounds = new GLBounds(newBounds);
			this.positionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(8).putBoundingPoints().getDataBuffer().flip());
			this.vao.unload(); // Invalidate VAO
			this.prepare();
		}
	}
	
	public BigInteger getStateFingerprint() {
		return Identification.generateFingerprint(super.getStateFingerprint(),
				this.radius.hashCode());
	}
	
	public PrimitiveType getType() {
		return PrimitiveType.EDGE;
	}
	
	public void updateProperty(int identifier) {		
		switch(identifier) {
			case GLEdgeContext.EDGE_RADIUS_IDENTIFIER:
				this.getContext().getGL().glUniform2f(this.getContext().getEdgeRadiusUniform(), this.radius.getX(), this.radius.getY());
				break;
			default:
				super.updateProperty(identifier);
		}
	}

	private void updateRadius(GL4 gl) {
		Viewport.glGetPixelSize(gl).apply(this.radius);;
	}
	
	public void render() {
		if(this.bounds != null) {
			GL4 gl = this.getContext().getGL();
			
			this.radius = new Point(this.edgeRadius);
			
			this.updateRadius(gl);
			
			this.vao.bind(gl);
			gl.glDrawArraysInstanced(GL4.GL_TRIANGLE_STRIP, 0, 4, 4);
		}
	}
	
	protected GLEdgeContext getContext() {
		return GLRenderingContext.edge;
	}
}