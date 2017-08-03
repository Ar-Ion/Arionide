package org.azentreprise.arionide.ui.primitives;

import java.awt.geom.Rectangle2D;
import java.nio.DoubleBuffer;

public class GLCoordinates {
	
	private DoubleBuffer buffer;
	private int counter;
	
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	
	private final long uuid;
	
	public GLCoordinates(Rectangle2D bounds) {		
		this.x1 = bounds.getX() - 1.0d;
		this.y1 = -bounds.getY() + 1.0d;
		this.x2 = bounds.getWidth() + bounds.getX() - 1.0d;
		this.y2 = -bounds.getHeight() - bounds.getY() + 1.0d;
		
		this.uuid = (this.getUUID(this.x1) << 48) | (this.getUUID(this.y1) << 32) | (this.getUUID(this.x2) << 16) | this.getUUID(this.y2);
	}
	
	private long getUUID(double component) {
		assert Math.abs(component) <= 1.0d;
		return (long) (component * Short.MAX_VALUE) + Short.MAX_VALUE;
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