package org.azentreprise.arionide.ui.core.geom;

import java.util.List;

import org.azentreprise.arionide.project.HierarchyElement;

public class CoreGeometry extends HierarchicalGeometry {
	public CoreGeometry() {
		super(0.1f, 0.75f);
	}
	
	protected List<HierarchyElement> getHierarchyElements() {
		return this.getProject().getStorage().getHierarchy();
	}
}