package ch.innovazion.arionide.ui.core.gl.stars;

import ch.innovazion.arionide.ui.core.gl.Settings;

public class StarsSettings implements Settings {
	
	private final float size;
	
	protected StarsSettings(float size) {
		this.size = size;
	}
	
	protected float getSize() {
		return size;
	}
}