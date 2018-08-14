package org.azentreprise.arionide.ui.overlay.components;

import java.util.List;

import org.azentreprise.arionide.ui.render.UILighting;

public interface Enlightenable {
	public void requestAlphaUpdate(int alpha);
	public List<UILighting> getEnlightenablePrimitives();
}
