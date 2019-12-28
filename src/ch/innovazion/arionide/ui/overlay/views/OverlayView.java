package ch.innovazion.arionide.ui.overlay.views;

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.components.Button;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;
import ch.innovazion.arionide.ui.render.Shape;
import ch.innovazion.arionide.ui.topology.Bounds;

public class OverlayView extends View {
	
	private final Shape overlay = PrimitiveFactory.instance().newSolid(0, 127);
	
	public OverlayView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.1f, 0.1f, 0.9f, 0.9f);
	
		setBorderColor(ApplicationTints.MAIN_COLOR);
		
		overlay.updateBounds(new Bounds(0.0f, 0.0f, 2.0f, 2.0f));
		
		this.add(new Button(this, "Create").setSignal("create"), 0.1f, 0.8f, 0.45f, 0.9f);
		this.add(new Button(this, "Cancel").setSignal("cancel"), 0.55f, 0.8f, 0.9f, 0.9f);
	}
	
	public void drawSurface(AppDrawingContext context) {	
		context.getOverlayRenderingSystem().renderLater(overlay);
		super.drawSurface(context);
	}
	
	public PrimitiveRenderingSystem getPreferedRenderingSystem(AppDrawingContext context) {
		return context.getOverlayRenderingSystem();
	}
}
