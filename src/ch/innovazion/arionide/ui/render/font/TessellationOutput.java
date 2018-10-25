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
package ch.innovazion.arionide.ui.render.font;

import java.nio.Buffer;

public class TessellationOutput {
	
	private final Buffer vertices;
	private final Buffer uv;
	private final float width;
	private final float height;
	private final int count; // number of chars
	
	protected TessellationOutput(Buffer vertices, Buffer uv, float width, float height, int count) {
		this.vertices = vertices;
		this.uv = uv;
		this.width = width;
		this.height = height;
		this.count = count;
	}
	
	public Buffer getVerticesBuffer() {
		return this.vertices;
	}
	
	public Buffer getUVBuffer() {
		return this.uv;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public int getCount() {
		return this.count;
	}
}
