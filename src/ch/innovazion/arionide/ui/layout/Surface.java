/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.layout;

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.Drawable;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;
import ch.innovazion.arionide.ui.topology.Bounds;

public abstract class Surface implements Drawable {
		
	private Bounds bounds;
	private boolean visible;
		
	public Surface setBounds(Bounds bounds) {
		this.bounds = bounds;
		return this;
	}
	
	public final void draw(AppDrawingContext context) {
		if(this.visible && this.bounds != null) {
			this.drawSurface(context);

		}
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public Surface setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public Bounds getBounds() {
		return this.bounds;
	}
	
	public PrimitiveRenderingSystem getPreferedRenderingSystem(AppDrawingContext context) {
		return context.getRenderingSystem();
	}

	public abstract void load();
	public abstract void drawSurface(AppDrawingContext context);
}