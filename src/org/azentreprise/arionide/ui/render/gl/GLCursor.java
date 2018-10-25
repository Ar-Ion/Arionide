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

import org.azentreprise.arionide.ui.render.Cursor;
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.Primitive;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.gl.vao.Attribute;
import org.azentreprise.arionide.ui.render.gl.vao.UID;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArray;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArrayCache;
import org.azentreprise.arionide.ui.render.gl.vao.VertexBuffer;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;

import com.jogamp.opengl.GL4;

public class GLCursor extends Primitive implements Cursor {

	private final VertexBuffer positionBuffer = new VertexBuffer(Float.BYTES, new Attribute(GLRenderingContext.cursor.getPositionAttribute(), 2, GL4.GL_FLOAT, 0));
	private final VertexArray vao = new VertexArray(this.positionBuffer);
	
	private float size;
	private Point center = new Point();
	
	public GLCursor(float size) {
		this.size = size;
	}
	
	public void updateBounds(Bounds newBounds) {
		if(newBounds != null) {
			this.center = newBounds.getCenter();
			this.positionBuffer.updateDataSupplier(() -> FloatBuffer.allocate(2).put(this.center.getX()).put(this.center.getY()).flip());
			this.vao.unload(); // Invalidate VAO
			this.prepare();
		}
	}

	public void updateSize(float newSize) {
		this.size = newSize;
	}
		
	public void prepare() {
		this.requestAction(GLCursorContext.PREPARE_ACTION_IDENTIFIER);
	}

	protected BigInteger getStateFingerprint() {
		return Identification.generateFingerprint(Float.floatToIntBits(this.size));
	}

	protected PrimitiveType getType() {
		return PrimitiveType.CURSOR;
	}
	
	protected void updateProperty(int identifier) {
		switch(identifier) {
		case GLCursorContext.SIZE_IDENTIFIER:
			this.getContext().getGL().glPointSize(this.size);
			break;
		}
	}

	protected void processAction(int identifier) {		
		switch(identifier) {
			case GLCursorContext.PREPARE_ACTION_IDENTIFIER:
				if(this.center != null) {
					VertexArrayCache.load(new UID(this.center, this.getType()), this.getContext().getGL(), this.vao);
				}
								
				break;
			default:
				return;
		}
		
		this.clearAction(identifier);
	}

	protected void render() {
		if(this.center != null) {
			GL4 gl = this.getContext().getGL();
			
			this.vao.bind(gl);
			
			gl.glDrawArrays(GL4.GL_POINTS, 0, 1);
			gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		}		
	}
	
	protected GLCursorContext getContext() {
		return GLRenderingContext.cursor;
	}
}