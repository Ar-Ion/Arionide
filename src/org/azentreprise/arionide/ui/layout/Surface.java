/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.layout;

import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.Drawable;
import org.azentreprise.arionide.ui.topology.Bounds;

public abstract class Surface implements Drawable {
		
	private Bounds bounds;
	private boolean hidden = true;
		
	public Surface setBounds(Bounds bounds) {
		this.bounds = bounds;
		return this;
	}
	
	public final void draw(AppDrawingContext context) {
		if(!this.hidden && this.bounds != null) {
			this.drawSurface(context);
		}
	}
	
	public boolean isHidden() {
		return this.hidden;
	}
	
	public void show() {
		this.hidden = false;
	}
	
	public void hide() {
		this.hidden = true;
	}
	
	public Bounds getBounds() {
		return this.bounds;
	}
	
	public abstract void load();
	public abstract void drawSurface(AppDrawingContext context);
}