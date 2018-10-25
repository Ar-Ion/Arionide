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
package ch.innovazion.arionide.ui.render;

import ch.innovazion.arionide.ui.render.gl.GLCursor;
import ch.innovazion.arionide.ui.render.gl.GLEdge;
import ch.innovazion.arionide.ui.render.gl.GLLine;
import ch.innovazion.arionide.ui.render.gl.GLShape;
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
		return new GLText(text, rgb, alpha);
	}
	
	public Shape newRectangle() {
		return this.newRectangle(0, 0);
	}
	
	public Shape newRectangle(int rgb, int alpha) {
		GLShape rect = new GLUnedgedRectangle(rgb, alpha, 32.0f);
		GLShape edge = new GLEdge(rgb, alpha, 32.0f);
		
		return PrimitiveMulticaster.create(Shape.class, rect, edge);
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
