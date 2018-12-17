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
package ch.innovazion.arionide.ui.render.gl.cache;

import com.jogamp.opengl.GL4;

public class Attribute {
	
	private final int id;
	private final int size;
	private final int type;
	private final int divisor;
	private final boolean normalize;
	
	public Attribute(int id, int size, int type) {
		this(id, size, type, 0);
	}
	
	
	public Attribute(int id, int size, int type, int divisor) {
		this(id, size, type, divisor, false);
	}
	
	public Attribute(int id, int size, int type, int divisor, boolean normalize) {
		this.id = id;
		this.size = size;
		this.type = type;
		this.divisor = divisor;
		this.normalize = normalize;
	}
	
	protected int getSize() {
		return this.size;
	}
	
	protected void load(GL4 gl, int stride, int pointer) {
		gl.glEnableVertexAttribArray(this.id);
		gl.glVertexAttribPointer(this.id, this.size, this.type, this.normalize, stride, pointer);
		gl.glVertexAttribDivisor(this.id, this.divisor);
	}
}