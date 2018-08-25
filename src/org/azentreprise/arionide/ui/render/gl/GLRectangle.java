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

import org.azentreprise.arionide.ui.render.GLBounds;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.Rectangle;
import org.azentreprise.arionide.ui.render.gl.vao.Attribute;
import org.azentreprise.arionide.ui.render.gl.vao.UID;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArray;
import org.azentreprise.arionide.ui.render.gl.vao.VertexArrayCache;
import org.azentreprise.arionide.ui.render.gl.vao.VertexBuffer;
import org.azentreprise.arionide.ui.topology.Bounds;

import com.jogamp.opengl.GL4;

public class GLRectangle extends GLShape implements Rectangle {
			
	protected final VertexBuffer positionBuffer = new VertexBuffer(Float.BYTES, new Attribute(this.getContext().getPositionAttribute(), 2, GL4.GL_FLOAT));
	protected final VertexArray vao = new VertexArray(this.positionBuffer);
	
	protected GLBounds bounds;
	
	public GLRectangle(Bounds bounds, int rgb, int alpha) {
		super(rgb, alpha);		
		this.updateBounds(bounds);
	} 
	
	protected void prepareGL() {
		if(this.bounds != null) {
			VertexArrayCache.load(new UID(this.bounds, PrimitiveType.RECT), this.getContext().getGL(), this.vao);
		}
	}
	
	public void updateBounds(Bounds newBounds) {
		if(newBounds != null) {
			this.bounds = new GLBounds(newBounds);
			this.positionBuffer.updateDataSupplier(() -> this.bounds.allocDataBuffer(8).putBoundingPoints().getDataBuffer().flip());
			this.vao.unload(); // Invalidate VAO
			this.prepare();
		}
	}

	public PrimitiveType getType() {
		return PrimitiveType.RECT;
	}
	
	public void render() {		
		if(this.bounds != null) {
			GL4 gl = this.getContext().getGL();
	
			this.vao.bind(gl);
			gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, 4);
		}
	}
	
	protected GLRectangleContext getContext() {
		return GLRenderingContext.rectangle;
	}
}