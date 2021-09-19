package ch.innovazion.arionide.ui.render.font.latex;

import ch.innovazion.arionide.ui.render.font.TextCacheEntry;

public class GLLatexCacheEntry extends TextCacheEntry {
	protected GLLatexCacheEntry(float width, float height, int count) {
		super(width, height, count);
	}

	public int getTextureID() {
		return 2;
	}
}
