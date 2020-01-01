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
package ch.innovazion.arionide.ui.render.font;

public class CharMeta {
	
	private final int charID;
	private final int base;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final int xOffset;
	private final int yOffset;
	private final int advance;
	
	protected CharMeta(int id, int base, int x, int y, int width, int height, int xOffset, int yOffset, int advance) {
		this.charID = id;
		this.base = base;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.advance = advance;
	}

	public int getCharID() {
		return this.charID;
	}
	
	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
	
	public int getXOffset() {
		return this.xOffset;
	}

	public int getYOffset() {
		return this.yOffset;
	}
	
	public int getAscent() {
		return this.base - this.yOffset;
	}
	
	public int getDescent() {
		return this.yOffset + this.height - this.base;
	}
	
	public int getX1() {
		return this.x;
	}

	public int getY1() {
		return this.y;
	}
	
	public int getX2() {
		return this.x + this.width;
	}

	public int getY2() {
		return this.y + this.height;
	}
	
	public int getAdvance() {
		return this.advance;
	}
}
