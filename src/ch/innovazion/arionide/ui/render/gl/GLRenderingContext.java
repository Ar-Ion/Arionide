/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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

import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.ui.render.RenderingContext;
import ch.innovazion.arionide.ui.render.font.GLFontRenderer;

public abstract class GLRenderingContext implements RenderingContext {
	
	public static GLPolygonContext polygon;
	public static GLUnedgedRectangleContext unedgedRectangle;
	public static GLEdgeContext edge;
	public static GLCircleContext circle;
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
		circle = new GLCircleContext(gl, edge);
	}
}