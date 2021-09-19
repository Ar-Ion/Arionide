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
package ch.innovazion.arionide.ui.render;

import ch.innovazion.arionide.ui.render.gl.GLCursor;
import ch.innovazion.arionide.ui.render.gl.GLEdge;
import ch.innovazion.arionide.ui.render.gl.GLLine;
import ch.innovazion.arionide.ui.render.gl.GLShape;
import ch.innovazion.arionide.ui.render.gl.GLSolid;
import ch.innovazion.arionide.ui.render.gl.GLText;
import ch.innovazion.arionide.ui.render.gl.GLUnedgedRectangle;

public class PrimitiveFactory {
	
	private static final PrimitiveFactory singleton = new PrimitiveFactory();
	
	public static PrimitiveFactory instance() {
		return singleton;
	}
	
	private PrimitiveFactory() {
		;
	}
	
	public Text newText() {
		return this.newText(new String());
	}
	
	public Text newText(String text) {
		return this.newText(text, 0, 0);
	}

	public Text newText(String text, int rgb, int alpha) {		
		return this.newText(text, rgb, alpha, false);
	}
	
	public Text newText(String text, int rgb, int alpha, boolean latex) {		
		return new GLText(text, rgb, alpha, latex);
	}
	
	public Shape newRectangle() {
		return this.newRectangle(0, 0);
	}
	
	public Shape newRectangle(int rgb, int alpha) {
		GLShape rect = new GLUnedgedRectangle(rgb, alpha, 32.0f);
		GLShape edge = new GLEdge(rgb, alpha, 32.0f);
		
		return PrimitiveMulticaster.create(Shape.class, rect, edge);
	}
	
	public Shape newSolid() {
		return this.newSolid(0, 0);
	}
	
	public Shape newSolid(int rgb, int alpha) {
		return new GLSolid(rgb, alpha);
	}
	
	public Shape newLine() {
		return this.newLine(0, 0);
	}
	
	public Shape newLine(int rgb, int alpha) {
		return new GLLine(rgb, alpha);
	}
	
	public Cursor newCursor(float size) {
		return new GLCursor(size);
	}
}
