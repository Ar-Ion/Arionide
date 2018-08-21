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

import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.RenderingContext;
import org.azentreprise.arionide.ui.topology.Bounds;

public class GLUnedgedRectangle extends GLRectangle {

	private static GLUnedgedRectangleRenderingContext context;
	
	private int unedgingRadius;
	
	private float radiusX;
	private float radiusY;
	
	public GLUnedgedRectangle(Bounds bounds, int rgb, int alpha, int unedgingRadius) {
		super(bounds, rgb, alpha);
		this.unedgingRadius = unedgingRadius;
	}

	public BigInteger getFingerprint() {
		return Identification.generateFingerprint(super.getFingerprint(),
				Float.floatToIntBits(this.radiusX) ^ Float.floatToIntBits(this.radiusY));
	}
	
	public PrimitiveType getType() {
		return PrimitiveType.UNEDGED_RECT;
	}
	
	public void updateProperty(int identifier) {
		super.updateProperty(identifier);
						
		switch(identifier) {
			case GLUnedgedRectangleRenderingContext.UNEDGING_RADIUS_IDENTIFIER:
				context.getGL().glUniform2f(context.getUnedgingRadiusUniform(), this.radiusX, this.radiusY);
				break;
		}
	}

	public void render() {				
		if(this.bounds != null) {
			this.radiusX = this.unedgingRadius * context.getAspectRatio();
			this.radiusY = this.unedgingRadius / context.getAspectRatio();
		}
	}
	
	public static RenderingContext setupContext(GLUnedgedRectangleRenderingContext context) {
		return GLUnedgedRectangle.context = context;
	}
}