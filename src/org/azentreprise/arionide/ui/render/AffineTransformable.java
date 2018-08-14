package org.azentreprise.arionide.ui.render;

public interface AffineTransformable {
	public void updateScale(float newScaleX, float newScaleY);
	public void updateTranslation(float newTranslateX, float newTranslateY);
}