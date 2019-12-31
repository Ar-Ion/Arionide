/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.topology.Inverse;
import ch.innovazion.arionide.ui.topology.Scalar;
import ch.innovazion.arionide.ui.topology.Size;

public class Viewport {
	public static Size getWindowSize(AppDrawingContext context) {
		Size size = context.getWindowSize();
		
		if(context instanceof OpenGLContext) {
			GL4 gl = ((OpenGLContext) context).getRenderer();
			
			IntBuffer viewport = IntBuffer.allocate(4);
			gl.glGetIntegerv(GL4.GL_VIEWPORT, viewport);
			
			Scalar scalar = new Scalar(viewport.get(2), viewport.get(3));
			scalar.apply(size);
		}
		
		return size;
	}
	
	public static Size getPixelSize(AppDrawingContext context) {
		Size windowSize = getWindowSize(context);
		Inverse.instance.apply(windowSize);
		return windowSize;
	}
	
	public static Size glGetPixelSize(GL4 gl) {
		Size size = new Size(1, 1);
		
		IntBuffer viewport = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL4.GL_VIEWPORT, viewport);
		
		Scalar scalar = new Scalar(viewport.get(2), viewport.get(3));		
		Inverse.instance.compose(scalar).apply(size);
		
		return size;
	}
}
