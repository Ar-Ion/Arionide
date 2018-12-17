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

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.render.GLBounds;
import ch.innovazion.arionide.ui.render.Polygon;
import ch.innovazion.arionide.ui.render.PrimitiveType;
import ch.innovazion.arionide.ui.render.gl.cache.Attribute;
import ch.innovazion.arionide.ui.render.gl.cache.UID;
import ch.innovazion.arionide.ui.render.gl.cache.VertexArray;
import ch.innovazion.arionide.ui.render.gl.cache.VertexArrayCache;
import ch.innovazion.arionide.ui.render.gl.cache.VertexBuffer;
import ch.innovazion.arionide.ui.topology.Bounds;

public abstract class GLPolygon extends GLShape implements Polygon {
			
	private final VertexBuffer positionBuffer = new VertexBuffer(Float.BYTES, new Attribute(this.getContext().getPositionAttribute(), 2, GL4.GL_FLOAT, this.getPositionAttributeDivisor()));
	protected final VertexArray vao = new VertexArray(this.positionBuffer);
	
	protected GLBounds bounds = null;
	
	public GLPolygon(int rgb, int alpha) {
		super(rgb, alpha);
	} 
	
	protected void prepareGL() {
		if(this.bounds != null) {
			VertexArrayCache.load(new UID(this.bounds, this.getType()), this.getContext().getGL(), this.vao);
		}
	}
	
	protected int getPositionAttributeDivisor() {
		return 0;
	}
	
	public void updateBounds(Bounds newBounds) {
		if(newBounds != null) {
			this.bounds = new GLBounds(newBounds);
			this.updateBuffers(this.positionBuffer);
			this.vao.unload(); // Invalidate VAO
			this.prepare();
		}
	}

	public PrimitiveType getType() {
		return PrimitiveType.POLYGON;
	}
	
	public void render() {
		if(this.bounds != null) {	
			this.vao.bind(this.getContext().getGL());
			this.renderPolygon();
		}
	}
	
	protected GLPolygonContext getContext() {
		return GLRenderingContext.polygon;
	}
	
	protected abstract void updateBuffers(VertexBuffer mainPositionBuffer);
}