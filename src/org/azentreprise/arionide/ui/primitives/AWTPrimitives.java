package org.azentreprise.arionide.ui.primitives;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class AWTPrimitives implements IPrimitives {
	
	public void drawRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void fillRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void drawRoundRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
	}

	public void fillRoundRect(AppDrawingContext context, Rectangle bounds) {
		this.loadContext(context).getRenderer().fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
	}

	public void drawLine(AppDrawingContext context, int x1, int y1, int x2, int y2) {
		this.loadContext(context).getRenderer().drawLine(x1, y1, x2, y2);
	}
	
	public Point drawText(AppDrawingContext context, String text, Rectangle bounds) {			
		Graphics2D g2d = this.loadContext(context).getRenderer();
		
		g2d.setFont(context.getFontAdapter().adapt(text, bounds.width, bounds.height));
		
		FontMetrics metrics = g2d.getFontMetrics();
		
		int delta = text.length() > 1 ? 1 : (metrics.getMaxDescent() - 1); // some height corrections for symbols like <
		
		int x = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
		int y = bounds.y + (bounds.height - metrics.getMaxDescent() + metrics.getMaxAscent() + delta) / 2;
		
		g2d.drawString(text, x, y);
		
		return new Point(x, y);
	}
	
	private AWTDrawingContext loadContext(AppDrawingContext context) {
		assert context instanceof AWTDrawingContext;
		return (AWTDrawingContext) context;
	}
}