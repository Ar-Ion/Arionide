package org.azentreprise.arionide.ui.core.awt;

import java.awt.Color;
import java.awt.Rectangle;

import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;

public class AWTCoreRenderer implements CoreRenderer {
	public void render(AppDrawingContext context, Rectangle bounds) {
		assert context instanceof AWTDrawingContext;
		
		AWTDrawingContext awtContext = (AWTDrawingContext) context;
		
		awtContext.setDrawingColor(Color.black);
		awtContext.getPrimitives().fillRect(context, bounds);
	}
}