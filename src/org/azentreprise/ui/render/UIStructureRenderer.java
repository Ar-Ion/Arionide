package org.azentreprise.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

public class UIStructureRenderer {
	
	private static volatile float[] fractions;
	private static volatile Color[] colors;
	
	public static void init(float hue, float saturation) {
		fractions = new float[] {
				0.0f,
				0.7f,
				0.75f,
				1.0f
			};
			
		colors = new Color[] { 
			new Color(Color.HSBtoRGB(hue, saturation, 0.5f)),
			new Color(Color.HSBtoRGB(hue, saturation, 0.7f)), 
			ComplexProjectRenderer.glow,
			new Color(0, true)
		};
	}

	public static void render(Graphics2D g2d, int x, int y, int radius) {     
        Paint gradient = new RadialGradientPaint(new Point2D.Float(x, y), radius, fractions, colors);
        g2d.setPaint(gradient);
        g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}
}