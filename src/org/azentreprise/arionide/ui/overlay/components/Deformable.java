package org.azentreprise.arionide.ui.overlay.components;

import java.util.List;

import org.azentreprise.arionide.ui.render.AffineTransformable;

public interface Deformable {
	public List<AffineTransformable> getDeformablePrimitives();
}