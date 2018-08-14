package org.azentreprise.arionide.ui.render;

public interface UILighting {
	public void updateRGB(int newRGB);
	public void updateAlpha(int newAlpha);
	public void updateLightCenter(float newCenterX, float newCenterY);
	public void updateLightRadius(float newRadius);
	public void updateLightStrength(float newStrength);
}
