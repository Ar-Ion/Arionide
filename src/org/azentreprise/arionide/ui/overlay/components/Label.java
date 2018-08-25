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
package org.azentreprise.arionide.ui.overlay.components;

import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.ApplicationTints;
import org.azentreprise.arionide.ui.overlay.AlphaLayer;
import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.render.PrimitiveFactory;
import org.azentreprise.arionide.ui.render.Text;
import org.azentreprise.arionide.ui.render.UILighting;
import org.azentreprise.arionide.ui.topology.Bounds;

public class Label extends Component implements Enlightenable {
	
	private final Text text;

	private int alpha = ApplicationTints.ACTIVE_ALPHA;
	
	public Label(View parent, String label) {
		super(parent);
		
		this.text = PrimitiveFactory.instance().newText(label, ApplicationTints.MAIN_COLOR, ApplicationTints.ACTIVE_ALPHA);
	}
	
	public void load() {
		this.text.prepare();
	}
	
	public Label setBounds(Bounds bounds) {
		super.setBounds(bounds);
		this.text.updateBounds(bounds);
		return this;
	}
	
	public Label setLabel(String label) {
		this.text.updateText(label);
		return this;
	}
	
	public Label setColor(int rgb) {
		this.text.updateRGB(rgb);
		return this;
	}
	
	public Label setAlpha(int alpha) {
		Utils.checkColorRange("Alpha", alpha);
		this.alpha = alpha;
		return this;
	}
	
	public void requestAlphaUpdate(int alpha) {
		this.setAlpha(alpha);
	}
	
	public List<UILighting> getEnlightenablePrimitives() {
		return Arrays.asList(this.text);
	}
	
	public Text getText() {
		return this.text;
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
		this.text.updateAlpha(this.getAppManager().getAlphaLayering().push(AlphaLayer.COMPONENT, this.alpha));
	}
	
	protected void drawComponent(AppDrawingContext context) {
		context.getRenderingSystem().renderLater(this.text);
	}
	
	private void postDraw() {
		this.getAppManager().getAlphaLayering().pop(AlphaLayer.COMPONENT);
	}
	
	public String toString() {
		return this.text.toString();
	}
	
	public boolean isFocusable() {
		return false;
	}
}
