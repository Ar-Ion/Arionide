package ch.innovazion.arionide.ui.render.font.latex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.TextureData;

import ch.innovazion.arionide.ui.render.font.GLTextCacheEntry;
import ch.innovazion.arionide.ui.render.font.GLTextRenderer;
import ch.innovazion.arionide.ui.render.font.TessellationOutput;

public class GLLatexRenderer extends GLTextRenderer implements LatexRenderer {
	
	public static final int GL_CACHE_CAPACITY = 16; // Limited by maximum number of hardware textures
	private static final int GL_BASE_TEXTURE = GL4.GL_TEXTURE5;
	private static final int BASE_TEXTURE = 5;
	private static final int TEXTURE_CACHE_CAPACITY = 64; // Limited by RAM capacity
	
	private static final Map<String, TextureData> textureCache = Collections.synchronizedMap(new LinkedHashMap<String, TextureData>(TEXTURE_CACHE_CAPACITY, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Map.Entry<String, TextureData> eldest) {
			if(this.size() > TEXTURE_CACHE_CAPACITY) {
				eldest.getValue().destroy();
				return true;
			} else {
				return false;
			}
		}
	});
	
	private final LatexBackend backend = new LatexBackend(textureCache);
	
	private int[] textures;
	
	public GLLatexRenderer() {
		super(new GLLatexTessellator(textureCache), GL_CACHE_CAPACITY);
	}

	public void initRenderer(GL4 gl, int shader, int[] textures, int translationUniform, int scaleUniform) {
		this.textures = textures;
		super.initRenderer(gl, shader, translationUniform, scaleUniform);
	}
	
	public GLTextCacheEntry alloc(GL4 gl, String str) {
		GLTextCacheEntry entry = super.alloc(gl, str);
		
		if(entry == null) {
			backend.requestLatex(str);
		}
		
		return entry;
	}
	
	public GLTextCacheEntry createCacheEntry(GL4 gl, String str, TessellationOutput tess, int vao, int verticesBuffer, int uvBuffer) {
		int textureID;
		TextureData data;
		
		synchronized(textureCache) {
			data = textureCache.get(str);
			textureID = new ArrayList<>(textureCache.keySet()).indexOf(str);
		}
		
		assert data != null;
		
		gl.glActiveTexture(GL_BASE_TEXTURE + textureID);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textures[textureID]);
		gl.glPixelStorei(GL4.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, data.getWidth(), data.getHeight(), 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, data.getBuffer());
		gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		
		return new GLTextCacheEntry(tess.getWidth(), tess.getHeight(), tess.getCount(), vao, BASE_TEXTURE + textureID, new int[] {verticesBuffer, uvBuffer});
	}
	
	public void renderPrimitives(GL4 gl, GLTextCacheEntry entry) {
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}
}