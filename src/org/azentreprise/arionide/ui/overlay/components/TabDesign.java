package org.azentreprise.arionide.ui.overlay.components;

import java.awt.geom.Point2D;

import org.azentreprise.arionide.ui.AppDrawingContext;

public interface TabDesign {
	public void createDesign(AppDrawingContext context, Point2D center, int radius);
}