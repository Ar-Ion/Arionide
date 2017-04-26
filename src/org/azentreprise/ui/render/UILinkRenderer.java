package org.azentreprise.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class UILinkRenderer {
	
	private static volatile float[] fractions;
	private static volatile Color[] colors;
	
	public static void init(float hue, float saturation) {
		fractions = new float[] {
			0.0f,
			0.4f,
			0.45f,
			1.0f
		};
			
		colors = new Color[] {
			new Color(Color.HSBtoRGB(hue, saturation, 0.7f)),
			new Color(Color.HSBtoRGB(hue, saturation, 1.0f)),
			ComplexProjectRenderer.glow,
			new Color(0, true),
		};
	}
	
	public static void render(Graphics2D g2d, int x1, int y1, int x2, int y2, int lineRadius) {
		Rectangle bounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
		
		double delta = bounds.getHeight() / bounds.getWidth();
		double partial = Math.sqrt(1.0D + delta * delta);
		
		Point2D p1 = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		Point2D p2 = new Point2D.Double(bounds.getCenterX() - lineRadius * delta / partial, bounds.getCenterY() + lineRadius / partial);

		Paint gradient = new LinearGradientPaint(p1, p2, fractions, colors, CycleMethod.REFLECT);

		g2d.setPaint(gradient);
		g2d.drawLine(x1, y1, x2, y2);
	}
}
