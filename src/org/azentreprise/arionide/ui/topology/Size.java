package org.azentreprise.arionide.ui.topology;

import java.util.Arrays;
import java.util.List;

public class Size extends Application implements Set {

	private Point size;
	
	public Size() {
		this(0.0f, 0.0f);
	}
	
	public Size(int width, int height) {
		this.size = new Point(width, height);
	}
	
	public Size(float width, float height) {
		this.size = new Point(width, height);
	}
	
	public void setWidth(float width) {
		this.size.setX(width);
	}
	
	public void setHeight(float height) {
		this.size.setY(height);
	}
	
	public void setSize(float width, float height) {
		this.size.setX(width);
		this.size.setY(height);
	}
	
	public float getWidth() {
		return this.size.getX();
	}
	
	public float getHeight() {
		return this.size.getY();
	}
	
	public int getWidthAsInt() {
		return this.size.getXAsInt();
	}
	
	public int getHeightAsInt() {
		return this.size.getYAsInt();
	}
	
	public void apply(Set input) {
		for(Point point : input.getPoints()) {
			point.setX(point.getX() * this.size.getX());
			point.setY(point.getY() * this.size.getY());
		}
	}
	
	public List<Point> getPoints() {
		return Arrays.asList(this.size);
	}
	
	public Size copy() {
		return new Size(this.size.getX(), this.size.getY());
	}
}
