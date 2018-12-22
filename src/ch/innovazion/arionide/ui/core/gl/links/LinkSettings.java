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
package ch.innovazion.arionide.ui.core.gl.links;

import org.joml.Vector3f;
import org.joml.Vector4f;

import ch.innovazion.arionide.ui.core.gl.Abstract3DSettings;

public class LinkSettings extends Abstract3DSettings {
	
	private Vector3f camera;
	private Vector4f color;
	private float ambientFactor;
	
	public void setCamera(Vector3f camera) {
		this.camera = camera;
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public void setAmbientFactor(float factor) {
		this.ambientFactor = factor;
	}
	
	public Vector3f getCamera() {
		return camera;
	}
	
	public Vector4f getColor() {
		return color;
	}
	
	public float getAmbientFactor() {
		return ambientFactor;
	}
}
