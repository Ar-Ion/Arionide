package ch.innovazion.arionide.ui.render.font.latex;

import java.nio.Buffer;
import java.util.Map;

import com.jogamp.opengl.util.texture.TextureData;

import ch.innovazion.arionide.ui.render.GLBounds;
import ch.innovazion.arionide.ui.render.font.GLTextTessellator;
import ch.innovazion.arionide.ui.render.font.TessellationOutput;
import ch.innovazion.arionide.ui.topology.Bounds;

public class GLLatexTessellator implements GLTextTessellator {

	private final Map<String, TextureData> textureCache;
	
	protected GLLatexTessellator(Map<String, TextureData> textureCache) {
		this.textureCache = textureCache;
	}
	
	public int getWidth(String input) {
		synchronized(textureCache) {
			if(textureCache.containsKey(input)) {
				return textureCache.get(input).getWidth();
			} else {
				return -1;
			}
		}
	}

	public int getHeight(String input) {
		synchronized(textureCache) {
			if(textureCache.containsKey(input)) {
				return textureCache.get(input).getHeight();
			} else {
				return -1;
			}
		}
	}

	public TessellationOutput tessellateString(String input) {
		TextureData texture;
		
		synchronized(textureCache) {
			texture = textureCache.get(input);
		}
		
		if(texture == null) {
			return null;
		}
		
		int width = texture.getWidth();
		int height = texture.getHeight();
		
		int mainDimension = Math.max(width, height);
		
		float halfFactor = 1.0f / mainDimension;
		float factor = 2.0f * halfFactor;
	
		Bounds verticesBounds = new Bounds(-halfFactor * width, -halfFactor * height, factor * width, factor * height);
		Bounds uvBounds = new Bounds(0.0f, 0.0f, 1.0f, 1.0f);
		
		Buffer vertices = new GLBounds(verticesBounds, false).allocDataBuffer(8).putNorth().putSouth().getDataBuffer().flip();
		Buffer textures = new GLBounds(uvBounds, false).allocDataBuffer(8).putNorth().putSouth().getDataBuffer().flip();

		return new TessellationOutput(vertices, textures, 0.75f * factor * width, 0.75f * factor * height, 1);
	}
}