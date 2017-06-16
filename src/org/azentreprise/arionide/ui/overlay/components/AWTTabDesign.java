package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class AWTTabDesign implements TabDesign {
	public void createDesign(AppDrawingContext context, Point2D center, int radius) {
		assert context instanceof AWTDrawingContext;
		Graphics2D g2d = ((AWTDrawingContext) context).getRenderer();
		g2d.setPaint(new RadialGradientPaint(center, radius, new float[] {0.0f, 1.0f}, new Color[] {new Color(g2d.getColor().getRGB(), false), g2d.getColor()}));
	}
}