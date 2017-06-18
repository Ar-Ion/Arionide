package org.azentreprise.arionide.ui.core.opengl;

import java.awt.Rectangle;

import org.azentreprise.arionide.IProject;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;

public class OpenGLCoreRenderer implements CoreRenderer {
	public void render(AppDrawingContext context, Rectangle bounds) {
		assert context instanceof OpenGLDrawingContext;
		
		OpenGLDrawingContext glContext = (OpenGLDrawingContext) context;
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