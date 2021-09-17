package ch.innovazion.arionide.ui.render.font;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Bounds;

public interface TextRenderer {
	
	public static final float BBOX_FIT_X = 0.9f;
	public static final float BBOX_FIT_Y = 0.5f;
	
	public void windowRatioChanged(float newRatio);
	public TextCacheEntry getCacheEntry(String str);
	public Affine renderString(GL4 gl, String str, Bounds bounds);
	public TextTessellator getTessellator();
}
