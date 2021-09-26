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
package ch.innovazion.arionide.ui.render.gl;

import java.math.BigInteger;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.render.Identification;
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
	private boolean isLatex;
	
	private Affine renderTransformation = new Affine();
	
	public GLText(String text, int rgb, int alpha) {
		this(text, rgb, alpha, false);
	}
	
	
	public GLText(String text, int rgb, int alpha, boolean isLatex) {
		super(rgb, alpha);
		this.text = text;
		this.isLatex = isLatex;
		requestAction(GLTextContext.FETCH_CACHE_ACTION_IDENTIFIER);
	}

	protected void prepareGL() {

	}
	
	public void setLatexEnabled(boolean latex) {
		this.isLatex = latex;
		requestAction(GLTextContext.FETCH_CACHE_ACTION_IDENTIFIER);
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

	public BigInteger getStateFingerprint() {
		return Identification.generateFingerprint(super.getStateFingerprint(), getTextureID());
	}
	
	private int getTextureID() {
		GLTextCacheEntry entry = null;

		if(isLatex) {
			entry = getContext().getLatexRenderer().getCacheEntry(text);
		} else {
			entry = getContext().getFontRenderer().getCacheEntry(text);	
		}
				
		if(entry != null) {
			return entry.getTextureID();
		} else {
			return -1;
		}
	}
	
	public void updateProperty(int identifier) {
		GLTextContext context = this.getContext();
		GL4 gl = context.getGL();
		
		switch(identifier) {
			case GLTextContext.TEXTURE_IDENTIFIER:
				int textureID = getTextureID();
								
				if(textureID != -1) {
					gl.glUniform1i(context.getSamplerUniform(), textureID);
				}
				
				break;
			case GLShapeContext.SCALE_IDENTIFIER:
				break;
			case GLShapeContext.TRANSLATION_IDENTIFIER:
				break;
			default:
				super.updateProperty(identifier);
		}
	}
	
	public void processAction(int identifier) {
		GLTextContext context = this.getContext();
		GL4 gl = context.getGL();
		GLTextCacheEntry entry = null;

		switch(identifier) {
			case GLTextContext.FETCH_CACHE_ACTION_IDENTIFIER:		
				
				if(isLatex) {
					entry = context.getLatexRenderer().fetch(gl, text);
				} else {
					entry = context.getFontRenderer().fetch(gl, text);	
				}

				if(entry != null && !entry.isInvalidated()) {
					clearAction(GLTextContext.FETCH_CACHE_ACTION_IDENTIFIER);
				}
				
				break;
			default: 
				super.processAction(identifier);
		}
	}
	
	public void render() {
		if(this.bounds != null) {
			Scalar scalar = this.affine.getScalar();
			Translation translation = this.affine.getTranslation();
			
			float x = (this.bounds.getX() - 1.0f) * scalar.getScaleX() + 1.0f + translation.getTranslateX();
			float y = (this.bounds.getY() - 1.0f) * scalar.getScaleY() + 1.0f + translation.getTranslateY();
			float width = this.bounds.getWidth() * scalar.getScaleX();
			float height = this.bounds.getHeight();

			if(isLatex || text.startsWith("\\tex")) {
				this.renderTransformation = this.getContext().getLatexRenderer().renderString(this.getContext().getGL(), text.replace("\\tex", ""), new Bounds(x, y, width, height));
			} else {
				this.renderTransformation = this.getContext().getFontRenderer().renderString(this.getContext().getGL(), text, new Bounds(x, y, width, height));
			}
		}
	}
	
	protected GLTextContext getContext() {
		return GLRenderingContext.text;
	}
	
	public String toString() {
		return this.text;
	}
}