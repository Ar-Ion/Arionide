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
package org.azentreprise.arionide.ui.render.gl;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.render.GLPrimitiveRenderer;
import org.azentreprise.arionide.ui.render.PrimitiveRenderer;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.RenderingContext;
import org.azentreprise.arionide.ui.render.Text;

import com.jogamp.opengl.GL4;

public class GLText extends Text {

	private String text;
	private int rgb;
	private int alpha;
	private Rectangle2D bounds;
	private Point2D renderPosition = new Point2D.Double();
	
	public GLText(Rectangle2D bounds, String text, int rgb, int alpha) {
		this.bounds = bounds;
		this.text = text;
		this.rgb = rgb;
		this.alpha = alpha;
	}
	
	public void updateBounds(Rectangle2D newBounds) {
		this.bounds = newBounds;
	}
	
	public void updateRGB(int newRGB) {
		this.rgb = newRGB;
	}
	
	public void updateAlpha(int newAlpha) {
		this.alpha = newAlpha;
	}
	
	public void updateText(String newText) {
		this.text = newText;
	}
	
	public Point2D getRenderPosition() {
		return this.renderPosition;
	}
	
	public int getIdentificationFactor() {
		return (this.alpha << 24) | this.rgb;
	}

	public PrimitiveType getType() {
		return PrimitiveType.TEXT;
	}

	public void updateProperty(PrimitiveRenderer renderer, RenderingContext context, int identifier) {
		assert renderer instanceof GLPrimitiveRenderer;
		assert context instanceof GLTextRenderingContext;
		
		GLTextRenderingContext textContext = (GLTextRenderingContext) context;
		GL4 gl = ((GLPrimitiveRenderer) renderer).getGL();
		
		switch(identifier) {
			case GLTextRenderingContext.ALPHA_CHANNEL_IDENTIFIER:
				gl.glUniform1f(textContext.getAlphaUniform(), this.alpha / 255.0f);
				break;
			case GLTextRenderingContext.RGB_CHANNEL_IDENTIFIER:
				gl.glUniform3f(textContext.getRGBUniform(), Utils.getRed(this.rgb) / 255.0f, Utils.getGreen(this.rgb) / 255.0f, Utils.getBlue(this.rgb) / 255.0f);
				break;
		}
	}

	public void render(PrimitiveRenderer renderer) {
		assert renderer instanceof GLPrimitiveRenderer;
		
		if(this.bounds != null) {
			this.renderPosition = ((GLPrimitiveRenderer) renderer).drawText(this.text, this.bounds);
		}
	}
	
	public String toString() {
		return this.text;
	}
}