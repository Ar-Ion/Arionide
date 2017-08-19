package org.azentreprise.arionide.ui.primitives;

import java.awt.Dimension;
import java.awt.Rectangle;
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
		this(bounds, true);
	}
	
	public GLCoordinates(Rectangle2D bounds, boolean normalize) {
		
		bounds = (Rectangle2D) bounds.clone(); // We don't want to mutate the original
		
		if(normalize) {
			this.normalizeGL(bounds);
		}
		
		this.x1 = bounds.getX();
		this.y1 = bounds.getY();
		this.x2 = bounds.getWidth() + this.x1;
		this.y2 = bounds.getHeight() + this.y1;

		this.uuid = (this.getUUID(this.x1) << 48) | (this.getUUID(this.y1) << 32) | (this.getUUID(this.x2) << 16) | this.getUUID(this.y2);
	}
	
	private void normalizeGL(Rectangle2D in) {
		in.setRect(in.getX() - 1.0d, 1.0d - in.getY(), in.getWidth(), -in.getHeight());
	}
	
	private void normalizeAWT(Rectangle2D in) {
		in.setRect(in.getX() / 2.0d + 0.5d, 0.5d - in.getY() / 2.0d, in.getWidth() / 2.0d, -in.getHeight() / 2.0d);
	}
	
	private void applyOrtho(Rectangle2D bounds, Dimension viewport) {
		bounds.setRect(bounds.getX() * viewport.getWidth(), bounds.getY() * viewport.getHeight(), bounds.getWidth() * viewport.getWidth(), bounds.getHeight() * viewport.getHeight());
	}
		
	public Rectangle getAWTBoundings(Dimension viewport) {
		Rectangle2D bounds = new Rectangle2D.Double(this.x1, this.y1, this.x2 - this.x1, this.y2 - this.y1);
		
		this.normalizeAWT(bounds);

		this.applyOrtho(bounds, viewport);

		return bounds.getBounds();
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