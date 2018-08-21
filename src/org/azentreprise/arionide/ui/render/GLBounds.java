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

import java.nio.DoubleBuffer;

import org.azentreprise.arionide.ui.topology.Affine;
import org.azentreprise.arionide.ui.topology.Application;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;
import org.azentreprise.arionide.ui.topology.Size;

public class GLBounds {
	
	private static final Application glTransform = new Affine(1.0f, -1.0f, -1.0f, 1.0f);
	
	private DoubleBuffer buffer;
	
	private final float x1;
	private final float y1;
	private final float x2;
	private final float y2;
		
	public GLBounds(Bounds bounds) {
		this(bounds, true);
	}
	
	public GLBounds(Bounds bounds, boolean normalize) {
		
		bounds = bounds.copy(); // We don't want to mutate the original
		
		if(normalize) {
			glTransform.apply(bounds);
		}
		
		this.x1 = bounds.getX();
		this.y1 = bounds.getY();
		this.x2 = bounds.getWidth() + this.x1;
		this.y2 = bounds.getHeight() + this.y1;
	}
	
	private void normalizeAWT(Bounds in) {
		in.setFrame(in.getX() / 2.0f + 0.5f, 0.5f - in.getY() / 2.0f, in.getWidth() / 2.0f, -in.getHeight() / 2.0f);
	}
		
	public Bounds getAWTBoundings(Size viewport) {
		Bounds bounds = new Bounds(new Point(this.x1, this.y1), new Point(this.x2, this.y2));
		
		this.normalizeAWT(bounds);

		viewport.apply(bounds);

		return bounds;
	}

	public GLBounds allocDataBuffer(int capacity) {
		this.buffer = DoubleBuffer.allocate(capacity);
		return this;
	}
	
	public DoubleBuffer getDataBuffer() {
		return this.buffer;
	}
		
	public GLBounds putX1() {
		this.buffer.put(this.x1);
		return this;
	}
	
	public GLBounds putY1() {
		this.buffer.put(this.y1);
		return this;
	}
	
	public GLBounds putX2() {
		this.buffer.put(this.x2);
		return this;
	}
	
	public GLBounds putY2() {
		this.buffer.put(this.y2);
		return this;
	}
	
	public GLBounds putNW() {
		this.putX1();
		this.putY1();
		return this;
	}
	
	public GLBounds putNE() {
		this.putX2();
		this.putY1();
		return this;
	}
	
	public GLBounds putSW() {
		this.putX1();
		this.putY2();
		return this;
	}
	
	public GLBounds putSE() {
		this.putX2();
		this.putY2();
		return this;
	}
	
	public GLBounds putNorth() {
		this.putNW();
		this.putNE();
		return this;
	}
	
	public GLBounds putSouth() {
		this.putSW();
		this.putSE();
		return this;
	}
	
	public GLBounds putWest() {
		this.putNW();
		this.putSW();
		return this;
	}
	
	public GLBounds putEast() {
		this.putNE();
		this.putSE();
		return this;
	}
	
	public GLBounds putBoundingPoints() {
		this.putNW();
		this.putNE();
		this.putSE();
		this.putSW();
		return this;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(this.x1);
		result = prime * result + Float.floatToIntBits(this.x2);
		result = prime * result + Float.floatToIntBits(this.y1);
		result = prime * result + Float.floatToIntBits(this.y2);
		return result;
	}

	public boolean equals(Object obj) {
		if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			GLBounds other = (GLBounds) obj;
			
			return Float.floatToIntBits(this.x1) == Float.floatToIntBits(other.x1)
				&& Float.floatToIntBits(this.x2) == Float.floatToIntBits(other.x2)
				&& Float.floatToIntBits(this.y1) == Float.floatToIntBits(other.y1)
				&& Float.floatToIntBits(this.y2) == Float.floatToIntBits(other.y2);
		}
	}

}