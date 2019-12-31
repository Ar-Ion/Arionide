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
package ch.innovazion.arionide.ui.core.gl.fx;

import java.nio.FloatBuffer;

import org.joml.Vector2f;

import ch.innovazion.arionide.ui.core.gl.Settings;

public class FBOFrameSettings implements Settings {
	
	private FloatBuffer C2PVM;
	private Vector2f lightPosition;
	private float exposure = 0.001f;
	private Vector2f pixelSize;
	
	public FloatBuffer getC2PVM() {
		return C2PVM;
	}
	
	public void setC2PVM(FloatBuffer buffer) {
		this.C2PVM = buffer;
	}
	
	public Vector2f getLightPosition() {
		return lightPosition;
	}
	
	public void setLightPosition(Vector2f lightPosition) {
		this.lightPosition = lightPosition;
	}
	
	public float getExposure() {
		return exposure;
	}
	
	public void setExposure(float exposure) {
		this.exposure = exposure;
	}
	
	public Vector2f getPixelSize() {
		return pixelSize;
	}
	
	public void setPixelSize(Vector2f pixelSize) {
		this.pixelSize = pixelSize;
	}
}