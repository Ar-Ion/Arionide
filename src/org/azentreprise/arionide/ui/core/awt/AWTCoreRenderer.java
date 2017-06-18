package org.azentreprise.arionide.ui.core.awt;

import java.awt.Color;
import java.awt.Rectangle;

import org.azentreprise.arionide.IProject;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;

public class AWTCoreRenderer implements CoreRenderer {
	public void render(AppDrawingContext context, Rectangle bounds) {
		assert context instanceof AWTDrawingContext;
		
		AWTDrawingContext awtContext = (AWTDrawingContext) context;
		
		awtContext.setDrawingColor(Color.black);
		awtContext.getPrimitives().fillRect(context, bounds);
	}

	@Override
	public void setScene(RenderingScene scene) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadProject(IProject project) {
		// TODO Auto-generated method stub
		
	}
}