/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui;

import java.awt.Font;
import java.awt.FontMetrics;

public class UIFontAdapter {
	
	private boolean setup = false;
	private FontMetrics metrics = null;
	private Font font = null;
	private float fontSize = 0.0f;
	private int fontHeight = 0;
	
	public boolean needSetup() {
		return !this.setup;
	}
	
	public void setup(FontMetrics metrics) {
		this.setup = true;
		this.metrics = metrics;
		this.font = this.metrics.getFont();
		this.fontHeight = this.metrics.getHeight() + this.metrics.getAscent();
		this.fontSize = this.font.getSize2D();
	}
	
	public Font adapt(String str, int width, int height, float relativeSize) {
		width *= relativeSize;
		height *= relativeSize;
		float fontSizeX = (float) width / this.metrics.stringWidth(str);
		float fontSizeY = (float) height / this.fontHeight;
		return this.font.deriveFont((float) this.fontSize * Math.min(fontSizeX, fontSizeY));
	}
}
