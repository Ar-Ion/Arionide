package org.azentreprise.arionide.ui.core;

import java.awt.Color;
import java.awt.Rectangle;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class AWTCoreRenderer implements CoreRenderer {
	public void render(AppDrawingContext context, Rectangle bounds) {
		assert context instanceof AWTDrawingContext;
		
		AWTDrawingContext awt = (AWTDrawingContext) context;
		
		awt.setDrawingColor(Color.black);
		awt.getPrimitives().fillRect(context, bounds);
	}
}