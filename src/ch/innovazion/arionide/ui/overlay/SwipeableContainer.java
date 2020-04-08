/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
/*package ch.innovazion.arionide.ui.overlay;

import java.util.function.Consumer;

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.animations.FieldModifierAnimation;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.components.Deformable;
import ch.innovazion.arionide.ui.overlay.views.MainView;
import ch.innovazion.arionide.ui.render.AffineTransformable;
import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Bounds;

public class SwipeableContainer extends Container {
	public static enum SwipeDirection {
		LEFT,
		RIGHT
	}
	
	private final Animation transformWidthAnimation;
	private final Animation bodyAlphaAnimation;
	
	private int pageID = 0;
	
	private float transformWidth = 1.0f;
	private int alpha = 0xFF;

	public SwipeableContainer(Container parent, LayoutManager layoutManager) {
		super(parent, layoutManager);
		
		this.transformWidthAnimation = new FieldModifierAnimation(this.getAppManager(), "transformWidth", MainView.class, this);
		this.bodyAlphaAnimation = new FieldModifierAnimation(this.getAppManager(), "bodyAlpha", MainView.class, this);
		
	}
	
	public void drawSurface(AppDrawingContext context) {
		for(Component component : this.page) {
			Bounds bounds = component.getBounds();
			
			if(bounds != null) {
				float leftToOrigin = Math.abs((float) bounds.getX() - 1.0f);
				float rightToOrigin = Math.abs((float) bounds.getX() + (float) bounds.getWidth() - 1.0f);
			
				if(component instanceof Deformable) {
					for(AffineTransformable primitive : ((Deformable) component).getDeformablePrimitives()) {
						if(this.transformWidth < 0.0d) {
							primitive.updateAffine(new Affine(-this.transformWidth, 1.0f, rightToOrigin * (this.transformWidth + 1.0f), 0.0f));
						} else {
							primitive.updateAffine(new Affine(this.transformWidth, 1.0f, leftToOrigin * (this.transformWidth - 1.0f), 0.0f));
						}	
					}
				}
			}
		}
		
		super.drawSurface(context);
	}
	
	private void makeHorizontalSwipe(SwipeDirection direction, Consumer<Void> completionHandler) {
		if(this.transformWidth == 1.0d) {
			this.getAppManager().getFocusManager().request(-1);
					
			float sign = direction.equals(SwipeDirection.LEFT) ? 1.0f : -1.0f;
			
			this.transformWidth *= sign;
			
			this.transformWidthAnimation.startAnimation(500, after -> {
				this.transformWidth = 1.0f;
			}, -sign);
			
			this.bodyAlphaAnimation.startAnimation(500, after -> {
				if(completionHandler != null) {
					completionHandler.accept(null);
				}
				
				this.alpha = 0xFF;
			}, -0xFF);
		}
	}
}*/
