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
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.gl.vao.Attribute;
import org.azentreprise.arionide.ui.render.gl.vao.VertexBuffer;
import org.azentreprise.arionide.ui.topology.Point;

import com.jogamp.opengl.GL4;

public class GLEdge extends GLPolygon {
	
	private final VertexBuffer edgeFactorBuffer = new VertexBuffer(Float.BYTES, new Attribute(this.getContext().getEdgeFactorAttribute(), 2, GL4.GL_FLOAT));
		
	private float edgeRadius;
	
	private Point radius = new Point();
	
	public GLEdge(int rgb, int alpha, float edgeRadius) {
		super(rgb, alpha);
		
		this.edgeRadius = edgeRadius;
		this.vao.addBuffers(this.edgeFactorBuffer);

		float f = 1.0f + 0.625f / edgeRadius;		
		this.edgeFactorBuffer.updateDataSupplier(() -> FloatBuffer.allocate(8).put(0).put(-f)
																			  .put(0).put(0)
																			  .put(1).put(-1)
																			  .put(f).put(0).flip());
	}
	
	public BigInteger getStateFingerprint() {
		return Identification.generateFingerprint(super.getStateFingerprint(),
				this.radius.hashCode());
	}
	
	protected int getPositionAttributeDivisor() {
		return 1;
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

	protected void updateBuffers(VertexBuffer mainPositionBuffer) {
		mainPositionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(8).putBoundingPoints().getDataBuffer().flip());
	}
	
	public void render() {
		this.radius = new Point(this.edgeRadius);
		Viewport.glGetPixelSize(this.getContext().getGL()).apply(this.radius); // TODO optimize
		super.render();
	}
	
	public void renderPolygon() {
		this.getContext().getGL().glDrawArraysInstanced(GL4.GL_TRIANGLE_STRIP, 0, 4, 4);
	}
	
	protected GLEdgeContext getContext() {
		return GLRenderingContext.edge;
	}
}