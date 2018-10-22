package org.azentreprise.arionide.ui.shaders.preprocessor;

public interface ShaderSettings {
	public String resolveConstant(String name);
	public String resolveFunction(String name);
	public int getType();
}
