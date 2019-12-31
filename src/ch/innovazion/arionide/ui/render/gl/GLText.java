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
package ch.innovazion.arionide.ui.render.gl;

import ch.innovazion.arionide.ui.render.PrimitiveType;
import ch.innovazion.arionide.ui.render.Text;
import ch.innovazion.arionide.ui.render.font.GLTextCacheEntry;
import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Scalar;
import ch.innovazion.arionide.ui.topology.Translation;

public class GLText extends GLShape implements Text {
	
	private Bounds bounds = null;
	private Affine affine = new Affine();
	private String text = new String();
	private GLTextCacheEntry entry = null;
	
	private Affine renderTransformation = new Affine();
	
	public GLText(String text, int rgb, int alpha) {
		super(rgb, alpha);
		this.text = text;
	}

	protected void prepareGL() {
		this.entry = this.getContext().getFontRenderer().fetch(this.getContext().getGL(), this.text);
	}
	
	public void updateBounds(Bounds newBounds) {
		this.bounds = newBounds;
	}
	
	public void updateText(String newText) {
		this.text = newText;
		this.prepare();
	}
	
	public void updateAffine(Affine affine) {
		this.affine = affine;
	}
	
	public Affine getRenderTransformation() {
		if(this.renderTransformation != null) {
			return this.renderTransformation;
		} else {
			throw new IllegalStateException("Requesting the affine transformation of a GLText without having been rendered.");
		}
	}

	public PrimitiveType getType() {
		return PrimitiveType.TEXT;
	}

	public void updateProperty(int identifier) {
		switch(identifier) {
			case GLShapeContext.SCALE_IDENTIFIER:
				break;
			case GLShapeContext.TRANSLATION_IDENTIFIER:
				break;
			default:
				super.updateProperty(identifier);
		}
	}
	
	public void render() {
		if(this.bounds != null && this.entry != null) {
			Scalar scalar = this.affine.getScalar();
			Translation translation = this.affine.getTranslation();
			
			float x = (this.bounds.getX() - 1.0f) * scalar.getScaleX() + 1.0f + translation.getTranslateX();
			float y = (this.bounds.getY() - 1.0f) * scalar.getScaleY() + 1.0f + translation.getTranslateY();
			float width = this.bounds.getWidth() * scalar.getScaleX();
			float height = this.bounds.getHeight();

			this.renderTransformation = this.getContext().getFontRenderer().renderString(this.getContext().getGL(), this.entry, new Bounds(x, y, width, height));
		}
	}
	
	protected GLTextContext getContext() {
		return GLRenderingContext.text;
	}
	
	public String toString() {
		return this.text;
	}
}