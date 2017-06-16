package org.azentreprise.arionide.ui.primitives;

import java.awt.Point;
import java.awt.Rectangle;

import org.azentreprise.arionide.ui.AppDrawingContext;

public interface IPrimitives {
	public void drawRect(AppDrawingContext context, Rectangle bounds);
	public void fillRect(AppDrawingContext context, Rectangle bounds);
	public void drawRoundRect(AppDrawingContext context, Rectangle bounds);
	public void fillRoundRect(AppDrawingContext context, Rectangle bounds);
	public void drawLine(AppDrawingContext context, int x1, int y1, int x2, int y2);
	public Point drawText(AppDrawingContext context, String text, Rectangle bounds);
}