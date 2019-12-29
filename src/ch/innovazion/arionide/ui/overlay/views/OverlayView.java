package ch.innovazion.arionide.ui.overlay.views;

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;

public abstract class OverlayView extends View {
		
	public OverlayView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
	}
	
	public void drawSurface(AppDrawingContext context) {	
		super.drawSurface(context);
	}
	
	public PrimitiveRenderingSystem getPreferedRenderingSystem(AppDrawingContext context) {
		return context.getOverlayRenderingSystem();
	}
}
