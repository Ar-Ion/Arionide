/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.Color;
import java.awt.Point;

import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Label extends Component {
	
	protected String label;
	protected int color = 0xCAFE;
	protected int opacity = 255;
	private int yCorrection = 0;
	
	protected Point textRenderPosition;

	public Label(View parent, String label) {
		super(parent);
		this.label = label;
	}
	
	public Label setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public Label setColor(int argb) {
		this.color = argb;
		this.opacity = argb >>> 24;
		return this;
	}
	
	public Label setOpacity(int opacity) {
		this.opacity = opacity & 0xFF;
		return this;
	}
	
	public Label setYCorrection(int correction) {
		this.yCorrection = correction;
		return this;
	}

	public void drawSurface(AppDrawingContext context) {
		if(this.opacity > 0) {
			context.setDrawingColor(new Color((this.opacity << 24) | (this.color & 0xFFFFFF), true));
		}
		
		this.textRenderPosition = context.getPrimitives().drawText(context, this.label, this.getBounds(), this.yCorrection);
	}
	
	public boolean isFocusable() {
		return false;
	}
}
