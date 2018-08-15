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

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.topology.Affine;
import org.azentreprise.arionide.ui.topology.Application;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;
import org.azentreprise.arionide.ui.topology.Size;

public class GLCoordinates {
	
	private static final Application glTransform = new Affine(1.0f, -1.0f, -1.0f, 1.0f);
	
	private DoubleBuffer buffer;
	private int counter;
	
	private final float x1;
	private final float y1;
	private final float x2;
	private final float y2;
	
	private final long uuid;
	
	public GLCoordinates(Bounds bounds) {
		this(bounds, true);
	}
	
	public GLCoordinates(Bounds bounds, boolean normalize) {
		
		bounds = bounds.copy(); // We don't want to mutate the original
		
		if(normalize) {
			glTransform.apply(bounds);
		}
		
		this.x1 = bounds.getX();
		this.y1 = bounds.getY();
		this.x2 = bounds.getWidth() + this.x1;
		this.y2 = bounds.getHeight() + this.y1;

		this.uuid = (this.getUID(this.x1) << 48) | (this.getUID(this.y1) << 32) | (this.getUID(this.x2) << 16) | this.getUID(this.y2);
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
	
	private long getUID(float component) {
		assert Math.abs(component) <= 1.0f;
		return Utils.convertToUnsignedLong((int) (component * 65535)); 
	}
		
	public long getUUID() {
		return this.uuid;
	}
	
	public GLCoordinates allocDataBuffer(int capacity) {
		this.buffer = DoubleBuffer.allocate(capacity);
		this.counter = 0;
		return this;
	}
	
	public DoubleBuffer getDataBuffer() {
		return this.buffer;
	}
	
	public int getDataBufferCount() {
		return this.counter;
	}
		
	public GLCoordinates putX1() {
		this.buffer.put(this.counter++, this.x1);
		return this;
	}
	
	public GLCoordinates putY1() {
		this.buffer.put(this.counter++, this.y1);
		return this;
	}
	
	public GLCoordinates putX2() {
		this.buffer.put(this.counter++, this.x2);
		return this;
	}
	
	public GLCoordinates putY2() {
		this.buffer.put(this.counter++, this.y2);
		return this;
	}
	
	public GLCoordinates putNW() {
		this.putX1();
		this.putY1();
		return this;
	}
	
	public GLCoordinates putNE() {
		this.putX2();
		this.putY1();
		return this;
	}
	
	public GLCoordinates putSW() {
		this.putX1();
		this.putY2();
		return this;
	}
	
	public GLCoordinates putSE() {
		this.putX2();
		this.putY2();
		return this;
	}
	
	public GLCoordinates putNorth() {
		this.putNW();
		this.putNE();
		return this;
	}
	
	public GLCoordinates putSouth() {
		this.putSW();
		this.putSE();
		return this;
	}
	
	public GLCoordinates putWest() {
		this.putNW();
		this.putSW();
		return this;
	}
	
	public GLCoordinates putEast() {
		this.putNE();
		this.putSE();
		return this;
	}
	
	public GLCoordinates putBoundingPoints() {
		this.putNW();
		this.putNE();
		this.putSE();
		this.putSW();
		return this;
	}
}