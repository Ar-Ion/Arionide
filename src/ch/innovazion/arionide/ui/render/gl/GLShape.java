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
package ch.innovazion.arionide.ui.render.gl;

import java.math.BigInteger;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.render.Identification;
import ch.innovazion.arionide.ui.render.Primitive;
import ch.innovazion.arionide.ui.render.Shape;
import ch.innovazion.arionide.ui.render.UILighting;
import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Point;
import ch.innovazion.arionide.ui.topology.Scalar;
import ch.innovazion.arionide.ui.topology.Translation;

public abstract class GLShape extends Primitive implements Shape {
		
	private int rgb = ApplicationTints.MAIN_COLOR;
	private int alpha = ApplicationTints.INACTIVE_ALPHA;
	private Point lightCenter = new Point();
	private float lightRadius = UILighting.NO_LIGHT;
	private float lightStrength = UILighting.DEFAULT_STRENGTH;
	private Affine affine = new Affine();
	
	protected GLShape(int rgb, int alpha) {
		this.rgb = rgb;
		this.alpha = alpha;
	}
	
	public void prepare() { // Avoid SIGSEGV by dispatching the preparation from the GL thread.
		this.requestAction(GLShapeContext.PREPARE_ACTION_IDENTIFIER); // Toggle bit in the action field and let it be executed from the PrimitiveRenderingSystem through processAction(int)
	}

	public void updateRGB(int newRGB) {
		this.rgb = newRGB;
	}
	
	public void updateAlpha(int newAlpha) {
		this.alpha = newAlpha;
	}
	
	public void updateLightCenter(Point center) {
		center = center.copy();
		Utils.getLayout2GL().apply(center);
		this.lightCenter = center;
	}
	
	public void updateLightRadius(float newRadius) {
		this.lightRadius = newRadius;
	}
	
	public void updateLightStrength(float newStrength) {
		this.lightStrength = newStrength;
	}
	
	public void updateAffine(Affine affine) {
		this.affine = affine;
	}

	public BigInteger getStateFingerprint() {
		return Identification.generateFingerprint(
			this.rgb, 
			this.affine.getScalar().hashCode(),
			this.affine.getTranslation().hashCode(), 
			this.alpha,
			Float.floatToIntBits(this.lightStrength), 
			Float.floatToIntBits(this.lightRadius),	  
			this.lightCenter.hashCode());
	}
	
	public void updateProperty(int identifier) {
		GLShapeContext context = this.getContext();
		GL4 gl = context.getGL();
				
		switch(identifier) {
			case GLShapeContext.RGB_IDENTIFIER:
				gl.glUniform3f(context.getRGBUniform(), Utils.getRed(this.rgb) / 255.0f, Utils.getGreen(this.rgb) / 255.0f, Utils.getBlue(this.rgb) / 255.0f);
				break;
			case GLShapeContext.ALPHA_IDENTIFIER:
				gl.glUniform1f(context.getAlphaUniform(), this.alpha / 255.0f);
				break;
			case GLShapeContext.LIGHT_CENTER_IDENTIFIER:
				gl.glUniform2f(context.getLightCenterUniform(), this.lightCenter.getX(), this.lightCenter.getY());
				break;
			case GLShapeContext.LIGHT_RADIUS_IDENTIFIER:
				gl.glUniform1f(context.getLightRadiusUniform(), this.lightRadius);
				break;
			case GLShapeContext.LIGHT_STRENGTH_IDENTIFIER:
				gl.glUniform1f(context.getLightStrengthUniform(), this.lightStrength);
				break;
			case GLShapeContext.SCALE_IDENTIFIER:
				Scalar scalar = this.affine.getScalar();
				gl.glUniform2f(context.getScaleUniform(), scalar.getScaleX(), scalar.getScaleY());
				break;
			case GLShapeContext.TRANSLATION_IDENTIFIER:
				Translation translation = this.affine.getTranslation();
				gl.glUniform2f(context.getTranslationUniform(), translation.getTranslateX(), translation.getTranslateY());
				break;
		}
	}
	
	public void processAction(int identifier) {
		switch(identifier) {
			case GLShapeContext.PREPARE_ACTION_IDENTIFIER:
				this.prepareGL();
				break;
			default: 
				return;
		}
		
		this.clearAction(identifier);
	}
	
	protected abstract void prepareGL();
	protected abstract GLShapeContext getContext();
}