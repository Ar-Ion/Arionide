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
package ch.innovazion.arionide.ui.render.gl;

import java.math.BigInteger;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.Viewport;
import ch.innovazion.arionide.ui.render.Identification;
import ch.innovazion.arionide.ui.render.PrimitiveType;
import ch.innovazion.arionide.ui.render.gl.vao.Attribute;
import ch.innovazion.arionide.ui.render.gl.vao.VertexBuffer;
import ch.innovazion.arionide.ui.topology.Point;

public class GLUnedgedRectangle extends GLPolygon {
	
	private final VertexBuffer unedgingFactorBuffer = new VertexBuffer(Float.BYTES, new Attribute(this.getContext().getUnedgingFactorAttribute(), 2, GL4.GL_FLOAT));;
	
	private float unedgingRadius;
	private Point radius = new Point();
	
	public GLUnedgedRectangle(int rgb, int alpha, float unedgingRadius) {
		super(rgb, alpha);
		
		this.unedgingRadius = unedgingRadius;
		this.vao.addBuffers(this.unedgingFactorBuffer);
		
		float m = 1.0f / unedgingRadius;

		this.unedgingFactorBuffer.updateDataSupplier(() -> FloatBuffer.allocate(16).put(1.0f + 1.375f*m).put(-m)
																				 .put(-1.0f).put(-m)
																				 .put(-m).put(-1.0f)
																				 .put(-m).put(1.0f - m)
																				 .put(1.0f + 1.5f*m).put(m)
																				 .put(-1.0f).put(m)
																				 .put(m).put(-1.0f - m)
																				 .put(m).put(1.0f - m).flip());
	}
	
	public BigInteger getStateFingerprint() {
		return Identification.generateFingerprint(super.getStateFingerprint(),
				this.radius.hashCode());
	}
	
	public PrimitiveType getType() {
		return PrimitiveType.UNEDGED_RECT;
	}
	
	public void updateProperty(int identifier) {
		switch(identifier) {
			case GLUnedgedRectangleContext.UNEDGING_RADIUS_IDENTIFIER:
				this.getContext().getGL().glUniform2f(this.getContext().getUnedgingRadiusUniform(), this.radius.getX(), this.radius.getY());
				break;
			default:
				super.updateProperty(identifier);
		}
	}

	protected void updateBuffers(VertexBuffer mainPositionBuffer) {
		mainPositionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(16).putNorth().putEast().putSouth().putWest().getDataBuffer().flip());
	}
	
	public void render() {
		this.radius = new Point(this.unedgingRadius);
		Viewport.glGetPixelSize(this.getContext().getGL()).apply(this.radius);
		super.render();
	}
	
	public void renderPolygon() {
		this.getContext().getGL().glDrawArrays(GL4.GL_LINES, 0, 8);
	}
	
	protected GLUnedgedRectangleContext getContext() {
		return GLRenderingContext.unedgedRectangle;
	}
}