package org.azentreprise.arionide.ui.core.opengl;

import java.util.List;

import org.joml.Vector3f;

public interface Geometry {
	public void setGenerationSeed(long seed);
	public List<WorldElement> getCollisions(Vector3f player);
	public List<WorldElement> getElements();
}
