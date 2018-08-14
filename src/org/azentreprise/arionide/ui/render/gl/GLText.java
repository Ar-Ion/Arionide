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
import java.math.BigInteger;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.render.GLPrimitiveRenderer;
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.render.PrimitiveRenderer;
import org.azentreprise.arionide.ui.render.PrimitiveType;
import org.azentreprise.arionide.ui.render.RenderingContext;
import org.azentreprise.arionide.ui.render.Text;

import com.jogamp.opengl.GL4;

public class GLText extends Text {

	private Rectangle2D bounds;
	private float translateX;
	private float translateY;
	private float scaleX = 1.0f;
	private float scaleY = 1.0f;
	private String text;
	private int rgb;
	private int alpha;
	private float lightCenterX;
	private float lightCenterY;
	private float lightRadius = -2.0f;
	private float lightStrength = 1.0f;
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
	
	public void updateScale(float newScaleX, float newScaleY) {
		this.scaleX = newScaleX;
		this.scaleY = newScaleY;
	}

	public void updateTranslation(float newTranslateX, float newTranslateY) {
		this.translateX = newTranslateX;
		this.translateY = newTranslateY;
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

	public void updateLightCenter(float newCenterX, float newCenterY) {
		this.lightCenterX = newCenterX - 1.0f;
		this.lightCenterY = 1.0f - newCenterY;
	}

	public void updateLightRadius(float newRadius) {
		this.lightRadius = newRadius;
	}

	public void updateLightStrength(float newStrength) {
		this.lightStrength = newStrength;
	}
	
	public Point2D getRenderPosition() {
		return this.renderPosition;
	}
	
	public BigInteger getFingerprint() {
		return Identification.generateFingerprint(
				this.rgb, 
				this.alpha, 
				Float.floatToIntBits(this.lightStrength), 
				Float.floatToIntBits(this.lightRadius), 
				Float.floatToIntBits(this.lightCenterX) ^ Float.floatToIntBits(this.lightCenterY));
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
			case GLTextRenderingContext.RGB_IDENTIFIER:
				gl.glUniform3f(textContext.getRGBUniform(), Utils.getRed(this.rgb) / 255.0f, Utils.getGreen(this.rgb) / 255.0f, Utils.getBlue(this.rgb) / 255.0f);
				break;
			case GLTextRenderingContext.ALPHA_IDENTIFIER:
				gl.glUniform1f(textContext.getAlphaUniform(), this.alpha / 255.0f);
				break;
			case GLTextRenderingContext.LIGHT_CENTER_IDENTIFIER:
				gl.glUniform2f(textContext.getLightCenterUniform(), this.lightCenterX, this.lightCenterY);
				break;
			case GLTextRenderingContext.LIGHT_RADIUS_IDENTIFIER:
				gl.glUniform1f(textContext.getLightRadiusUniform(), this.lightRadius);
				break;
			case GLTextRenderingContext.LIGHT_STRENGTH_IDENTIFIER:
				gl.glUniform1f(textContext.getLightStrengthUniform(), this.lightStrength);
				break;
		}
	}

	public void render(PrimitiveRenderer renderer) {
		assert renderer instanceof GLPrimitiveRenderer;
		
		if(this.bounds != null) {
			double x = (this.bounds.getX() - 1.0d) * this.scaleX + 1.0d + this.translateX;
			double y = (this.bounds.getY() - 1.0d) * this.scaleY + 1.0d + this.translateY;
			double width = this.bounds.getWidth() * this.scaleX;
			double height = this.bounds.getHeight();

			this.renderPosition = ((GLPrimitiveRenderer) renderer).drawText(this.text, new Rectangle2D.Double(x, y, width, height));
		}
	}
	
	public String toString() {
		return this.text;
	}
}