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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.geom.Point2D;

import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.overlay.AlphaLayer;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public class Label extends Component {
	
	private String label;
	private int rgb = 0xCAFE;
	private int alpha = 0xFF;
	private int yCorrection = 0;
	
	protected Point2D textRenderPosition;

	public Label(View parent, String label) {
		super(parent);
		this.label = label;
	}
	
	public Label setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public Label setColor(int rgb) {
		if(rgb > 0xFFFFFF) {
			throw new IllegalArgumentException("Alpha values are not allowed");
		}
		
		this.rgb = rgb;
		return this;
	}
	
	public Label setAlpha(int alpha) {
		this.alpha = alpha;
		return this;
	}
	
	public Label setYCorrection(int correction) {
		this.yCorrection = correction;
		return this;
	}

	public void drawSurface(AppDrawingContext context) {
		if(this.alpha > 0) {
			this.preDraw(context);
		}
		
		this.drawComponent(context);
		
		if(this.alpha > 0) {
			this.postDraw();
		}
	}
	
	private void preDraw(AppDrawingContext context) {
		this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, this.alpha);
		context.setColor(this.rgb);
	}
	
	protected void drawComponent(AppDrawingContext context) {
		this.textRenderPosition = context.getPrimitives().drawText(context, this.label, this.getBounds(), this.yCorrection);
	}
	
	private void postDraw() {
		this.getAppManager().getAlphaLayering().pop(AlphaLayer.COMPONENT);
	}
	
	public String toString() {
		return this.label;
	}
	
	public boolean isFocusable() {
		return false;
	}
}
