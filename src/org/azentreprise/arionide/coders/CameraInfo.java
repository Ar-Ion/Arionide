package org.azentreprise.arionide.coders;

public class CameraInfo {
	
	private final float x;
	private final float y;
	private final float z;
	private final float yaw;
	private final float pitch;

	public CameraInfo(float x, float y, float z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getZ() {
		return this.z;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public float getPitch() {
		return this.pitch;
	}
}