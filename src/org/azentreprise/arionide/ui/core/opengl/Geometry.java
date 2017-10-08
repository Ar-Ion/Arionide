package org.azentreprise.arionide.ui.core.opengl;

import java.util.List;
import java.util.stream.Stream;

import org.joml.Vector3f;

public interface Geometry {
	public void setGenerationSeed(long seed);
	public Stream<WorldElement> getCollisions(Vector3f player);
	public List<WorldElement> getElements();
}
