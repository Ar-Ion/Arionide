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
package org.azentreprise.arionide.ui.render;

import org.azentreprise.arionide.ui.render.gl.GLLine;
import org.azentreprise.arionide.ui.render.gl.GLRectangle;
import org.azentreprise.arionide.ui.render.gl.GLText;
import org.azentreprise.arionide.ui.topology.Bounds;

public class PrimitiveFactory {
	
	private static final PrimitiveFactory singleton = new PrimitiveFactory();
	
	public static PrimitiveFactory instance() {
		return singleton;
	}
	
	public Text newText() {
		return this.newText(new String());
	}
	
	public Text newText(String text) {
		return this.newText(text, 0, 0);
	}
	
	public Text newText(String text, int rgb, int alpha) {
		return this.newText(null, text, rgb, alpha);
	}
	
	public Text newText(Bounds bounds, String text, int rgb, int alpha) {
		return new GLText(bounds, text, rgb, alpha);
	}
	
	public Rectangle newRectangle() {
		return this.newRectangle(0, 0);
	}
	
	public Rectangle newRectangle(int rgb, int alpha) {
		return this.newRectangle(null, rgb, alpha);
	}
	
	public Rectangle newRectangle(Bounds bounds, int rgb, int alpha) {
		return new GLRectangle(bounds, rgb, alpha);
	}
	
	public Rectangle newLine() {
		return this.newLine(0, 0);
	}
	
	public Rectangle newLine(int rgb, int alpha) {
		return this.newLine(null, rgb, alpha);
	}
	
	public Rectangle newLine(Bounds bounds, int rgb, int alpha) {
		return new GLLine(bounds, rgb, alpha);
	}
}
