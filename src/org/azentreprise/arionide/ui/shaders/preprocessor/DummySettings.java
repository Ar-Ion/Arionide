package org.azentreprise.arionide.ui.shaders.preprocessor;

import com.jogamp.opengl.GL4;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DummySettings implements ShaderSettings {

	public static final ShaderSettings VERTEX = new DummySettings(GL4.GL_VERTEX_SHADER);
	public static final ShaderSettings FRAGMENT = new DummySettings(GL4.GL_FRAGMENT_SHADER);

	private final int type;
	
	private DummySettings(int type) {
		this.type = type;
	}
	
	public String resolveConstant(String name) {
		throw new NotImplementedException();
	}

	public String resolveFunction(String name) {
		throw new NotImplementedException();
	}

	public int getType() {
		return this.type;
	}
}