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

import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.render.RenderingContext;
import org.azentreprise.arionide.ui.render.font.GLFontRenderer;

import com.jogamp.opengl.GL4;

public abstract class GLRenderingContext implements RenderingContext {
	
	public static GLPolygonContext polygon;
	public static GLUnedgedRectangleContext unedgedRectangle;
	public static GLEdgeContext edge;
	public static GLCursorContext cursor;
	public static GLTextContext text;
	
	private final GL4 gl;
	
	protected GLRenderingContext(GL4 gl) {
		this.gl = gl;
	}
	
	protected GL4 getGL() {
		return this.gl;
	}
	
	protected abstract int getShaderID();
	
	public static void init(GL4 gl, Resources resources, GLFontRenderer fontRenderer) {
		polygon = new GLPolygonContext(gl);
		unedgedRectangle = new GLUnedgedRectangleContext(gl);
		edge = new GLEdgeContext(gl, resources);
		cursor = new GLCursorContext(gl);
		text = new GLTextContext(gl, fontRenderer);
	}
}