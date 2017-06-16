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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.ui;

import java.awt.Font;
import java.awt.FontMetrics;

public class FontAdapter {
	
	private static final int CACHE_STEP = 2;
	private static final int WIDTH_CORRECTION = 4;
	
	private final Font[] cache = new Font[32];
	private String hash = new String();
	
	private FontMetrics metrics = null;
	private Font font = null;
	private float fontSize = 0.0f;
	private int fontHeight = 0;
	
	public FontAdapter(FontMetrics fm) {
		this.metrics = fm;
		this.font = this.metrics.getFont();
		this.fontHeight = this.metrics.getHeight() + this.metrics.getAscent();
		this.fontSize = this.font.getSize2D();
		
		for(int i = 0; i < this.cache.length; i++) {
			this.cache[i] = this.font.deriveFont(i * CACHE_STEP + 1.0f);
		}
	}
	
	public Font adapt(String str, int width, int height, float relativeSize) {
		String localHash = "hash@" + width + height + relativeSize;
		
		if(localHash != this.hash) {
			float fontSizeX = (float) width * relativeSize / this.metrics.stringWidth(str) / WIDTH_CORRECTION;
			float fontSizeY = (float) height * relativeSize / this.fontHeight;
			int size = (int) (this.fontSize * Math.min(fontSizeX, fontSizeY) / CACHE_STEP);
			
			if(size < 0) {
				size = 0;
			} else if(size >= this.cache.length) {
				size = this.cache.length - 1;
			}
			
			this.hash = localHash;
			
			return this.cache[size];
		} else {
			return null;
		}
	}
	
	public FontMetrics getMetrics() {
		return this.metrics;
	}
}
