package org.azentreprise.arionide.ui.core.opengl;

import java.awt.Rectangle;

import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;

public class OpenGLCoreRenderer implements CoreRenderer {
	public void render(AppDrawingContext context, Rectangle bounds) {
		assert context instanceof OpenGLDrawingContext;
		
		OpenGLDrawingContext glContext = (OpenGLDrawingContext) context;
	}
}